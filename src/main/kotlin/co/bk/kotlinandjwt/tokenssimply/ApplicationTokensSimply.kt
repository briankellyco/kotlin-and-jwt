package co.bk.kotlinandjwt.tokenssimply

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["co.bk.kotlinandjwt.tokenssimply.config"])
class ApplicationTokensSimply

fun main(args: Array<String>) {
    runApplication<ApplicationTokensSimply>(*args)
}
