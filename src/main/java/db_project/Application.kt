package db_project

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Created by pacman29 on 23.02.17.
 */
@SpringBootApplication
open class Application {
    companion object {
        @Throws(Exception::class)
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}