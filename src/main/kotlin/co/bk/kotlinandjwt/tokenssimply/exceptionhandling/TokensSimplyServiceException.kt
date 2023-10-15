package co.bk.kotlinandjwt.tokenssimply.exceptionhandling

import java.lang.RuntimeException

data class TokensSimplyServiceException(val errorCode: ErrorCode) : RuntimeException() {

    enum class ErrorCode {
        RESERVED_FOR_UNKNOWN_ERRORS(
            "TSBACKEND-0",
            ""),
        INVALID_CREDENTIALS_SUPPLIED(
            "TSBACKEND-1",
            "400 Invalid username or password supplied."
        ),
        TOKEN_COULD_NOT_BE_AUTHORIZED(
            "TSBACKEND-2",
            "403 Token could not be authorized."
        );

        var applicationCode: String = "No applicationCode provided"
            private set
        var message = "No message provided"
            get() = field
            private set

        constructor() {}

        // Private constructor only allows objects to be constructed from within the class.
        constructor(applicationCode: String, message: String) {
            this.applicationCode = applicationCode
            this.message = message
        }
    }

    val applicationCode: String
        get() {
            var applicationCode: String = "undefined"
            if (errorCode != null) {
                applicationCode = errorCode.applicationCode
            }
            return applicationCode
        }
}