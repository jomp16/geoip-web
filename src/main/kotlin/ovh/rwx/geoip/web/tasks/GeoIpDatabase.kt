package ovh.rwx.geoip.web.tasks

import net.java.truevfs.access.TFile
import net.java.truevfs.access.TFileInputStream
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.util.FileCopyUtils
import java.io.File
import java.net.URL
import java.nio.file.Files

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

        downloadAndExtractFile(GEOIP_CITY_URL, GEOIP_CITY_DB_FILE)
        downloadAndExtractFile(GEOIP_COUNTRY_URL, GEOIP_COUNTRY_DB_FILE)
        downloadAndExtractFile(GEOIP_ASN_URL, GEOIP_ASN_DB_FILE)

        logger.info("Downloaded new database")
    }

    private fun downloadAndExtractFile(url: URL, outputFile: File) {
        val file = Files.createTempFile("GeoLite2", ".tar.gz").toFile()

        url.openStream().use { inputStream ->
            file.outputStream().use { outputStream ->
                FileCopyUtils.copy(inputStream, outputStream)
            }
        }

        val tfile = TFile(file)

        val firstDir = tfile.listFiles()?.firstOrNull { it.isDirectory }

        firstDir?.listFiles()?.forEach { fileTarGZ ->
            if (fileTarGZ.extension == "mmdb") {
                TFileInputStream(fileTarGZ).use { inputStream ->
                    outputFile.outputStream().use { outputStream ->
                        FileCopyUtils.copy(inputStream, outputStream)
                    }
                }
            }
        }

        file.delete()
    }
}