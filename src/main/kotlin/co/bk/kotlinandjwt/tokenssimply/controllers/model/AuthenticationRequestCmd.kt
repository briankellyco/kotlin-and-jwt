package co.bk.kotlinandjwt.tokenssimply.controllers.model

/*
 * Command pattern wrapping JSON data submitted to REST endpoint.
 *
 * Example CURL:
 *   curl -X POST --header "Content-type: application/json" --header "Accept: application/json"  --data '{"username": "isabella", "password": "supersecret"}' http://localhost:8080/api/auth/generatetoken
 */
// immutable object not using "data" keyword
class AuthenticationRequestCmd {

    val username: String

    val password: String

    constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }
}