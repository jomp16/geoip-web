package ovh.rwx.geoip.web.services.impl

import com.maxmind.geoip2.DatabaseReader
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import ovh.rwx.geoip.web.objects.GeoIp
import ovh.rwx.geoip.web.services.GeoIpService
import ovh.rwx.geoip.web.tasks.GeoIpDatabase
import java.net.InetAddress

@Service
internal class GeoIpServiceImpl(
        private val geoIpDatabase: GeoIpDatabase
) : GeoIpService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun searchIp(inetAddress: InetAddress): GeoIp {
        val databaseReaderCity: DatabaseReader = DatabaseReader.Builder(geoIpDatabase.GEOIP_CITY_DB_FILE).build()
        val databaseReaderASN: DatabaseReader = DatabaseReader.Builder(geoIpDatabase.GEOIP_ASN_DB_FILE).build()

        val city = try {
            databaseReaderCity.city(inetAddress)
        } catch (e: Exception) {
            logger.error("An error happened while searching for city for IP ${inetAddress.hostAddress}", e)
            
            null
        }
        val asn = try {
            databaseReaderASN.asn(inetAddress)
        } catch (e: Exception) {
            logger.error("An error happened while searching for ASN for IP ${inetAddress.hostAddress}", e)

            null
        }

        return GeoIp(cityResponse = city, asnResponse = asn)
    }
}