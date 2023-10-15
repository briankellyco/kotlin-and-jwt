package co.bk.kotlinandjwt.tokenssimply.exceptionhandling

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.zalando.problem.Problem
import org.zalando.problem.Status

private val logger = KotlinLogging.logger {}

@ControllerAdvice(annotations = [RestController::class])
internal class ApplicationExceptionHandler {

    val VALID_HTTP_STATUS_CODES: Map<String, Status> = Status.values()
        .associateBy({it.statusCode.toString()}, {it})

    /**
     * ALWAYS HAVE THIS IN A RESTFUL SPRING MICROSERVICE.
     *
     * Spring controllers annotations can throw exceptions. For example "required=true" attribute
     * on @RequestParam.
     *
     * Convert the exception so that clients are able to detect the HttpStatus code
     * and throw correct Exception sub-type.
     *
     * Exceptions thrown:
     * - HttpMessageNotReadableException -> if cmd object has strict enum types then this could be thrown.
     *
     * @param ex the spring controller exception thrown when params are missing.
     * @return error response.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(
        org.springframework.web.bind.MissingServletRequestParameterException::class,
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException::class,
        org.springframework.web.bind.ServletRequestBindingException::class,
        org.springframework.http.converter.HttpMessageNotReadableException::class,
        // Adding some LDAP exception translation
        java.lang.IllegalStateException::class,
        org.springframework.security.authentication.InternalAuthenticationServiceException::class,
        java.net.ConnectException::class
    )
    protected fun handleContainerException(exception: java.lang.Exception): ResponseEntity<Problem> {

        var statusZalandoProblem: Status = Status.BAD_REQUEST

        return ResponseEntity
            .status(HttpStatus.valueOf(statusZalandoProblem.getStatusCode()))
            .body(
                Problem.builder()
                    .withTitle(statusZalandoProblem.getReasonPhrase())
                    .withStatus(statusZalandoProblem)
                    .withDetail(
                        java.lang.String.join(
                            " ",
                            statusZalandoProblem.toString(),
                            exception.message
                        )
                    )
                    .with("application_code", TokensSimplyServiceException.ErrorCode.RESERVED_FOR_UNKNOWN_ERRORS.applicationCode)
                    .build()
            )
    }

    @ExceptionHandler(TokensSimplyServiceException::class)
    fun handleApplicationException(exception: TokensSimplyServiceException): ResponseEntity<Problem> {
        var status: Status = Status.BAD_REQUEST
        var applicationCode = "unknown"
        if (exception.message != null && exception.message.length >= 3) {
            val codeFromMessage: String = exception.message.substring(0, 3)
            val lookedUpStatus: Status? = VALID_HTTP_STATUS_CODES[codeFromMessage]
            if (lookedUpStatus != null) {
                status = lookedUpStatus
                applicationCode = exception.applicationCode
            }
        }
        return ResponseEntity
            .status(HttpStatus.valueOf(status.getStatusCode()))
            .body(
                Problem.builder()
                    .withTitle(status.getReasonPhrase())
                    .withStatus(status)
                    .withDetail(
                        String.format(
                            "Problem detected. The error code is: %s",
                            applicationCode
                        )
                    )
                    .with("application_code", applicationCode)
                    .build()
            )
    }
}