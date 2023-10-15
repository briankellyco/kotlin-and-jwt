package co.bk.kotlinandjwt.tokenssimply.config

import co.bk.kotlinandjwt.tokenssimply.exceptions.JWTAuthorizationFilter
import co.bk.kotlinandjwt.tokenssimply.security.PathConstants
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder

import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

import org.springframework.security.ldap.DefaultSpringSecurityContextSource
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter

//private val logger = KotlinLogging.logger {}

@Configuration
@EnableWebSecurity
class SecurityConfig(

    @Value("\${spring.ldap.embedded.base-dn}")
    private val baseDnLdapServer: String,

    @Value("\${de.myorg.ad.userSearchFilter}")
    private val userSearchFilter: String,

    @Value("\${de.myorg.ad.userSearchBase}")
    private val userSearchBase: String,

    @Value("\${de.myorg.ad.groupSearchFilter}")
    private val groupSearchFilter: String,

    @Value("\${de.myorg.ad.groupSearchBase}")
    private val groupSearchBase: String,

    @Value("\${spring.ldap.embedded.port}")
    private val portForEmbeddedLdapServer: Int

) : WebSecurityConfigurerAdapter() {

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun authorizationFilter(): JWTAuthorizationFilter? {
        return JWTAuthorizationFilter(PathConstants.PUBLIC_ENDPOINTS)
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {

        http.csrf()
            .disable()
            // dont authenticate these paths
            .authorizeRequests().antMatchers(*PathConstants.PUBLIC_ENDPOINTS).permitAll()
            // all other requests need to be authenticated. Will be carried out by authenticationManager in ResourceServer.
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(authorizationFilter(), AbstractPreAuthenticatedProcessingFilter::class.java)
            // make sure we use stateless session; session won't be used to store user's state.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    /**
     * For LDAP server lookup for "ldap-standard-schema.ldif"
     */
    override fun configure(auth: AuthenticationManagerBuilder) {

        auth.ldapAuthentication()
            .let { this.configureContextSource(it) }
            .userDnPatterns("uid={0},ou=people") // ONLY required with using ldap-standard-schema.ldif
            .userSearchBase(userSearchBase)
            .userSearchFilter(userSearchFilter)
            .groupSearchBase(groupSearchBase)
            .groupSearchFilter(groupSearchFilter)
    }

//    /**
//     * For LDAP server lookup for "ldap-custom-schema.ldif"
//     */
//    override fun configure(auth: AuthenticationManagerBuilder) {
//
//        auth.ldapAuthentication()
//            .let { this.configureContextSource(it) }
//            .userSearchBase(userSearchBase)
//            .userSearchFilter(userSearchFilter)
//            .groupSearchBase(groupSearchBase)
//            .groupSearchFilter(groupSearchFilter)
//            .authoritiesMapper { it.map { SimpleGrantedAuthority(it.authority.replace("R-", "")) } }
//    }

    fun configureContextSource(configurer: LdapAuthenticationProviderConfigurer<AuthenticationManagerBuilder>): LdapAuthenticationProviderConfigurer<AuthenticationManagerBuilder> {
        return configurer.contextSource(ldapContextSource())
    }

    fun ldapContextSource(): DefaultSpringSecurityContextSource {

        // For embedded server no username/password for LDAP connection is required.
        // Spring reports "Property 'userDn' not set - anonymous context will be used for read-write operations"
        var contextSource = DefaultSpringSecurityContextSource(
            listOf("ldap://localhost:$portForEmbeddedLdapServer/"), baseDnLdapServer)
        contextSource.afterPropertiesSet()
        return contextSource
    }
}
