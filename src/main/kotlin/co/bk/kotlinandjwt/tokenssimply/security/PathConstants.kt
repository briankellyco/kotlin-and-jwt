package co.bk.kotlinandjwt.tokenssimply.security

object PathConstants {

    val SECURED_ENDPOINTS = arrayOf<String>(
        "/auth/secured/with/http/security"
    )

    val PUBLIC_ENDPOINTS = arrayOf<String>(
        "/auth/generatetoken"
    )
}