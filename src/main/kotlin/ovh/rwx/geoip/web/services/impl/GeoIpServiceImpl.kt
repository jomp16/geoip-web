package ovh.rwx.geoip.web.services.impl

import com.maxmind.geoip2.DatabaseReader
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import ovh.rwx.geoip.web.objects.GeoIp
import ovh.rwx.geoip.web.services.GeoIpService
import java.net.InetAddress

@Service
internal class GeoIpServiceImpl : GeoIpService {
    private val databaseReaderCity: DatabaseReader by lazy { DatabaseReader.Builder(ClassPathResource("geoip/GeoLite2-City.mmdb").file).build() }
    private val databaseReaderASN: DatabaseReader by lazy { DatabaseReader.Builder(ClassPathResource("geoip/GeoLite2-ASN.mmdb").file).build() }

    override fun searchIp(inetAddress: InetAddress): GeoIp {
        val city = databaseReaderCity.city(inetAddress)
        val asn = databaseReaderASN.asn(inetAddress)

        return GeoIp(cityResponse = city, asnResponse = asn)
    }
}