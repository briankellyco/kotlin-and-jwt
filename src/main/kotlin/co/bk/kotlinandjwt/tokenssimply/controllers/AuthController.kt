package co.bk.kotlinandjwt.tokenssimply.controllers

import co.bk.kotlinandjwt.tokenssimply.exceptionhandling.TokensSimplyServiceException
import co.bk.kotlinandjwt.tokenssimply.service.TokenService
import co.bk.kotlinandjwt.tokenssimply.controllers.model.AuthenticationRequestCmd
import co.bk.kotlinandjwt.tokenssimply.controllers.model.AuthenticationResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/auth")
class AuthController(
    val authenticationManager: AuthenticationManager,
    val tokenService: TokenService
) {

    @PostMapping("/generatetoken")
    fun authenticateUser(@RequestBody authenticationRequestCmd: AuthenticationRequestCmd): ResponseEntity<*>? {

        if (authenticationRequestCmd.username.isNullOrEmpty() || authenticationRequestCmd.password.isNullOrEmpty()) {
            throw TokensSimplyServiceException(TokensSimplyServiceException.ErrorCode.INVALID_CREDENTIALS_SUPPLIED)
        }

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequestCmd.username,
                authenticationRequestCmd.password
            )
        )
        val jwtToken: String = tokenService.generateToken(authentication)

        return ResponseEntity.ok<Any>(AuthenticationResponse(jwtToken))
    }

    @GetMapping("/secured/with/http/security")
    fun testJwtTokenConvertedToSpringSecurity(principal: Principal): String {
        return "JWT token was successfully converted to Spring Security Principal for User: ${principal.name}"
    }
}