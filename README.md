# kotlin-and-jwt

![stability-badge](https://img.shields.io/badge/stability-Stable-success.svg?style=for-the-badge)

Create a Spring Security Principal from a JWT token using Kotlin.

## What is the use case for this microservice?

I supported a customer with an unconventional security requirement for their Kotlin Spring Boot Application. The application was 
an internal facing service with no public internet access.

The requirements were:

1. Remove Spring Form based security.
2. Add security that can authenticate the JWT tokens issued by the built-in Authorization server (a pre-existing OAuth integration used across the organisation).
3. Add security that can authenticate JWT tokens that have been manually issued by the application via an API endpoint. The purpose of this requirement was to provide 
   a fallback solution if the customer decided to switch off their existing OAuth integration. 


This proof of concept (POC) application demonstrates:

1. How to manually issue JWT tokens via an API endpoint.
2. And how to authenticate those JWT tokens in the Spring Security filter chain.

An additional task required by the production system, was to create a Custom security filter that would authenticate both 
the manually created JWT token as well as the OAuth issued JWT token. This functionality is not present in the POC but feel free to reach out
if you need help with this.

The application uses an embedded LDAP server to manage the user accounts and assigned role privileges.

## Run the application
1.  Checkout the github repo, import into your IDE and run using Java 11.
2.  Call the REST API using curl or Postman:

    * Postman collection is available at /docs/kotlin-and-jwt-tokens-simply.postman_collection.json

    Curl examples:

    * curl --location --request POST 'http://localhost:8080/api/auth/generatetoken' --header 'Content-Type: application/json' --data-raw '{"username": "bob", "password": "bobspassword"}'
    * curl --location --request GET 'http://localhost:8080/api/auth/secured/with/http/security' --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VybmFtZSI6ImJvYiIsImF1dGhvcml0aWVzIjpbIlJPTEVfREVWRUxPUEVSUyJdLCJpYXQiOjE2OTczNzI3MzYsImV4cCI6MTY5NzQxNTkzNn0.4fax9DW3QgsZvDaBv1Y3eVFwSZfzM9g-iOUGln3pqTWGZwt-m-ncszOAgUfijRow2l5Y6PIQCyMQZaY_srrObg'


## Success Criteria - JWT token authenticated and principal is present in the security context

![image](./docs/principal-is-present.jpg)

## Technologies used to build the API
``` 
Spring boot project init:
    https://start.spring.io/
Choosing:
    Spring 2.5.5
    Kotlin 1.5.31
```

## Author

[Brian Kelly](https://github.com/briankellyco)