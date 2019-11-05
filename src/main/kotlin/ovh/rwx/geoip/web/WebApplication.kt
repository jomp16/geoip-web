package ovh.rwx.geoip.web

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import ovh.rwx.geoip.web.tasks.GeoIpDatabase

@SpringBootApplication
@EnableScheduling
class WebApplication {
    @Bean
    fun init(geoIpDatabase: GeoIpDatabase) = CommandLineRunner {
        geoIpDatabase.updateGeoIpDatabase()
    }
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
