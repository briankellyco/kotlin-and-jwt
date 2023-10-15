# kotlin-and-jwt

![stability-badge](https://img.shields.io/badge/stability-Stable-success.svg?style=for-the-badge)

Create a Spring Security Principal from a JWT token using Kotlin.

## What is the use case for this microservice?

A customer had no Java or Kotlin developers and I was asked to extend the security mechanism of an existing Spring Boot application. I needed
to avoid any network/firewall issues with their LDAP server and choose to create a POC with an embedded server.

The proof of concept (POC) was required to prove:

1.  Spring Security Principals can be created from JWT tokens.
2.  And that JWT tokens can be authenticated in the spring security filter chain (in an existing Spring Boot application) without having 
    to drop the existing UsernamePassword authentication mechanism.

This microservice demonstrates part (1) and for ease of testing is integrated with a test LDAP server. The LDAP server contains user accounts and assigned role privileges.

## Run the application
1.  Checkout the github repo, import into your IDE and run using Java 11.
2.  Call the REST API using curl or Postman:

    * Postman collection is available at /docs/kotlin-and-jwt-tokens-simply.postman_collection.json

    Curl examples:

    * curl --location --request POST 'http://localhost:8080/api/auth/generatetoken' --header 'Content-Type: application/json' --data-raw '{"username": "bob", "password": "bobspassword"}'
    * curl --location --request GET 'http://localhost:8080/api/auth/secured/with/http/security' --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VybmFtZSI6ImJvYiIsImF1dGhvcml0aWVzIjpbIlJPTEVfREVWRUxPUEVSUyJdLCJpYXQiOjE2OTczNzI3MzYsImV4cCI6MTY5NzQxNTkzNn0.4fax9DW3QgsZvDaBv1Y3eVFwSZfzM9g-iOUGln3pqTWGZwt-m-ncszOAgUfijRow2l5Y6PIQCyMQZaY_srrObg'


## Success Criteria - principal is present in the security context

![image](./docs/principal-is-present.jpg)

## Technologies used to build the API
``` 
Spring boot project init:
    https://start.spring.io/
Choosing:
    Spring 2.5.5
    Java 11
```

## Author

[Brian Kelly](https://github.com/briankellyco)