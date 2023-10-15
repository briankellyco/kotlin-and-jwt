package co.bk.kotlinandjwt.tokenssimply.controllers.model

class ApiResponse {

    val success: Boolean

    val message: String

    constructor(successParam: Boolean, messageParam: String) {
        success = successParam
        message = messageParam
    }
}