package ovh.rwx.geoip.web.tasks

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.util.FileCopyUtils
import java.io.File
import java.net.URL

@Component
class GeoIpDatabase {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val GEOIP_CITY_URL = URL("https://geolite.maxmind.com/download/geoip/database/GeoLite2-City.tar.gz")
    private val GEOIP_COUNTRY_URL = URL("https://geolite.maxmind.com/download/geoip/database/GeoLite2-Country.tar.gz")
    private val GEOIP_ASN_URL = URL("https://geolite.maxmind.com/download/geoip/database/GeoLite2-ASN.tar.gz")

    val GEOIP_CITY_DB_FILE = File.createTempFile("GeoLite2-City", ".mmdb").apply { deleteOnExit() }
    val GEOIP_COUNTRY_DB_FILE = File.createTempFile("GeoLite2-Country", ".mmdb").apply { deleteOnExit() }
    val GEOIP_ASN_DB_FILE = File.createTempFile("GeoLite2-ASN", ".mmdb").apply { deleteOnExit() }

    // At 00:00 on every Wednesday
    @Scheduled(cron = "0 0 0 ? * WED")
    fun updateGeoIpDatabase() {
        logger.info("Downloading new database...")

        GEOIP_CITY_URL.openStream().use { inputStream ->
            GEOIP_CITY_DB_FILE.outputStream().use { outputStream ->
                FileCopyUtils.copy(inputStream, outputStream)
            }
        }

        GEOIP_COUNTRY_URL.openStream().use { inputStream ->
            GEOIP_COUNTRY_DB_FILE.outputStream().use { outputStream ->
                FileCopyUtils.copy(inputStream, outputStream)
            }
        }

        GEOIP_ASN_URL.openStream().use { inputStream ->
            GEOIP_ASN_DB_FILE.outputStream().use { outputStream ->
                FileCopyUtils.copy(inputStream, outputStream)
            }
        }
        
        logger.info("Downloaded new database")
    }
}