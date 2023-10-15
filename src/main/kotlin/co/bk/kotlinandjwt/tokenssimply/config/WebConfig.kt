package co.bk.kotlinandjwt.tokenssimply.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.ServletContext

/**
 * Web servlet container and CORS configuration.
 */
@Configuration
class WebConfig : WebMvcConfigurer {

    @Autowired
    private lateinit var applicationObjectMapper: ObjectMapper

    @Bean
    fun customCorsFilter(): FilterRegistrationBean<CorsFilter> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        val bean = FilterRegistrationBean(CorsFilter(source))
        bean.order = Ordered.HIGHEST_PRECEDENCE
        return bean
    }

    @Bean
    fun servletContextInitializer(): ServletContextInitializer {
        return ServletContextInitializer { servletContext: ServletContext ->
            servletContext.setSessionTrackingModes(
                emptySet()
            )
        }
    }

    @Bean
    fun jsonConverter(): MappingJackson2HttpMessageConverter {
        return MappingJackson2HttpMessageConverter(applicationObjectMapper)
    }

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>?>) {
        converters.add(StringHttpMessageConverter())
    }
}