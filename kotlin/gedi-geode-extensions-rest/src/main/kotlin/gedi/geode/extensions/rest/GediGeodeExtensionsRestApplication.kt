package gedi.geode.extensions.rest

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class GediGeodeExtensionsRestApplication

/**
 * The application main metho
 */
fun main(args: Array<String>) {
    SpringApplication.run(GediGeodeExtensionsRestApplication::class.java, *args)
}
