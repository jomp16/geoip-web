package ovh.rwx.geoip.web.services.impl

import com.maxmind.db.CHMCache
import com.maxmind.geoip2.DatabaseReader
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import ovh.rwx.geoip.web.objects.GeoIp
import ovh.rwx.geoip.web.services.GeoIpService
import java.net.InetAddress

@Service
internal class GeoIpServiceImpl : GeoIpService {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val databaseReaderCity: DatabaseReader by lazy { DatabaseReader.Builder(ClassPathResource("geoip/GeoLite2-City.mmdb").file).withCache(CHMCache()).build() }
    private val databaseReaderASN: DatabaseReader by lazy { DatabaseReader.Builder(ClassPathResource("geoip/GeoLite2-ASN.mmdb").file).withCache(CHMCache()).build() }

    override fun searchIp(inetAddress: InetAddress): GeoIp {
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