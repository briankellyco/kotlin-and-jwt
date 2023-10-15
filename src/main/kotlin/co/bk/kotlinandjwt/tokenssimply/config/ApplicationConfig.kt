package co.bk.kotlinandjwt.tokenssimply.config

import co.bk.kotlinandjwt.tokenssimply.service.TokenService
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.zalando.jackson.datatype.money.MoneyModule
import org.zalando.problem.ProblemModule

@Configuration
class ApplicationConfig {

    @Value("\${de.myorg.jwt-signing-secret}")
    lateinit var jwtSigningKey: String

    @Value("\${de.myorg.jwt-expires-in-secs}")
    lateinit var jwtExpiresInSecs: String

    @Bean
    @Primary
    fun applicationObjectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.registerModule(MoneyModule())
        objectMapper.registerModule(KotlinModule())
        objectMapper.registerModule(ProblemModule().withStackTraces())
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
        return objectMapper
    }

    @Bean
    fun tokenService(): TokenService {
        return TokenService(jwtSigningKey, jwtExpiresInSecs.toInt());
    }
}