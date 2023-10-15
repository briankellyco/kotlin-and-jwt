package co.bk.kotlinandjwt.tokenssimply.exceptions


import co.bk.kotlinandjwt.tokenssimply.exceptionhandling.TokensSimplyServiceException
import co.bk.kotlinandjwt.tokenssimply.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.Exception
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(
    publicEndpoints: Array<String>
) : OncePerRequestFilter() {

    private val publicEndpoints: Array<String>

    private val AUTHORIZATION_HEADER = "Authorization"
    private val TOKEN_PREFIX = "Bearer"

    @Autowired
    private val tokenService: TokenService? = null

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain) {

        if (isPublicEndpointIncludingWildcard(request.servletPath)) {
            chain.doFilter(request, response)
            return
        }

        val jwtToken = extractHeaderToken(request)

        try {

            if (jwtToken != null && SecurityContextHolder.getContext().authentication == null) {

                if ( tokenService!!.validateJwtToken(jwtToken)) {

                    val authenticationForSpring = tokenService.createUsernamePasswordAuthenticationTokenFromJwtToken(jwtToken)
                    authenticationForSpring.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authenticationForSpring
                }
            }

        } catch (e: Exception) {
            logger.error { "Token may have expired. Problem creating authenticationForSpring: ${e}" }
            throw TokensSimplyServiceException(TokensSimplyServiceException.ErrorCode.TOKEN_COULD_NOT_BE_AUTHORIZED)
        }
        chain.doFilter(request, response)
    }

    private fun extractHeaderToken(request: HttpServletRequest): String? {
        val headerAuth = request.getHeader(AUTHORIZATION_HEADER)
        return if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(TOKEN_PREFIX)) {
            headerAuth.substring(7, headerAuth.length)
        } else null
    }

    private fun isPublicEndpoint(endpointPath: String): Boolean {
        return if (publicEndpoints != null) { // Match servlet paths exactly
            Arrays.stream(publicEndpoints).anyMatch { item: String -> item.compareTo(endpointPath) == 0 }
        } else false
    }

    private fun isPublicEndpointIncludingWildcard(endpointPath: String): Boolean {

        // Checks if path
        // a) matches exactly OR
        // b) matches to wildcard chars e.g public endpoint "/api/user/**" matches endpointPath "/api/user/two"
        // c) does not match paths after wildcards e.g "/api/user/**/something/else" matches only to "/api/user/two"
        var result : Boolean = false
        if (publicEndpoints != null) {
            for (item in publicEndpoints) {

                if (item.compareTo(endpointPath) == 0)  {
                    // exact match
                    result = true
                    break
                } else if (item.contains("/**")) {
                    // matches up to wildcard
                    val pathBeforeWildcard = item.split("/**").get(0)
                    if (endpointPath.startsWith(pathBeforeWildcard)) {
                        result = true
                        break
                    }
                }
            }
        }
        return result
    }

    init {
        this.publicEndpoints = publicEndpoints
    }
}