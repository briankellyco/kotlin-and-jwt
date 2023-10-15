package co.bk.kotlinandjwt.tokenssimply.controllers.model

data class AuthenticationResponse (
    val access_token: String,
    val token_type: String = "bearer"
)