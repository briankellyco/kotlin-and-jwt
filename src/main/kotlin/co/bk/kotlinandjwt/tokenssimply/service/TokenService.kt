package co.bk.kotlinandjwt.tokenssimply.service;

import com.sun.security.auth.UserPrincipal
import io.jsonwebtoken.*
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl
import java.time.Instant
import java.util.*

private val logger = KotlinLogging.logger {}

class TokenService(
    val jwtSecret: String, val jwtExpiresInSecs: Int
) {

    fun generateToken(authentication: Authentication): String {

        val userPrincipal = authentication.principal as LdapUserDetailsImpl

        val now = Instant.now()
        val issuedAt = Date.from(now)
        val expiryTime = now.plusSeconds(jwtExpiresInSecs.toLong())

        val claims: Claims = Jwts.claims()
        claims.put("username", userPrincipal.getUsername())
        val authoritiesList: MutableList<String> = mutableListOf<String>()
        authentication.authorities?.forEach {
            val role = it.authority
            authoritiesList.add(role)
        }
        claims.put("authorities", authoritiesList)

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(Date.from(expiryTime))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    fun validateJwtToken(authToken: String): Boolean {
        try {
            // JWT signing key. It can be either a simple MAC key or an RSA key
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message)
        }
        return false
    }

    fun getUsernameFromJwtToken(jwtToken: String): String {

        val claims: Claims =  Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body
        val username = claims["username"].toString()
        return username
    }

    /**
     * Parse the JWT token and build a token that will be checked by Spring when checking API endpoints
     * secured with @PreAuthorize annotation.
     *
     * Example JWT:
     * {
     *   "username": "fps.test-admin",
     *   "authorities": [
     *      "ROLE_ADMIN",
     *      "ROLE_EDITOR"
     *   ],
     *   "iat": 1637167593,
     *   "exp": 1637173593
     *  }
     */
    fun createUsernamePasswordAuthenticationTokenFromJwtToken(jwtToken: String): UsernamePasswordAuthenticationToken {

        val username = getUsernameFromJwtToken(jwtToken)
        val userPrincipal = UserPrincipal(username)

        val claims: Claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).body

        val authorities = mutableListOf<SimpleGrantedAuthority>()
        claims.get("authorities", List::class.java)
            .forEach { it ->
                authorities.add(SimpleGrantedAuthority(it.toString()))
            }
        val authoritiesNonMutable = ArrayList(authorities)

        return UsernamePasswordAuthenticationToken(userPrincipal, null, authoritiesNonMutable)
    }
}