package ovh.rwx.geoip.web.controllers.api.v1.geoip

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.xbill.DNS.Address
import ovh.rwx.geoip.web.objects.api.v1.geoip.GeoIpSearchASNV1
import ovh.rwx.geoip.web.objects.api.v1.geoip.GeoIpSearchCityV1
import ovh.rwx.geoip.web.objects.api.v1.geoip.GeoIpSearchIpV1
import ovh.rwx.geoip.web.objects.api.v1.geoip.GeoIpSearchResponseV1Api
import ovh.rwx.geoip.web.services.GeoIpService
import java.net.InetAddress
import java.net.UnknownHostException

@RestController
@RequestMapping("/api/v1/geoip")
class GeoIpApiV1Controller(
        @Autowired
        private val geoIpService: GeoIpService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/search")
    fun search(@RequestBody geoIpSearchRequestV1Api: Set<String>): Flow<GeoIpSearchResponseV1Api> {
        val resolvePtr = geoIpSearchRequestV1Api.size <= 10

        return geoIpSearchRequestV1Api.asFlow().map { resolveIp(it, resolvePtr) }
    }

    private suspend fun resolveIp(ip: String, resolvePtr: Boolean): GeoIpSearchResponseV1Api {
        val inetAddress = InetAddress.getByName(ip)

        val searchIp = geoIpService.searchIp(inetAddress)

        var ptr: String? = null

        if (resolvePtr) {
            try {
                ptr = Address.getHostName(inetAddress)
            } catch (e: UnknownHostException) {
                logger.error("Error looking up the PTR of IP $ip", e)
            }
        }

        return GeoIpSearchResponseV1Api(
                GeoIpSearchIpV1(inetAddress.hostAddress, ptr),
                GeoIpSearchCityV1(
                        searchIp.cityResponse.city.names["en"] ?: "No city",
                        searchIp.cityResponse.mostSpecificSubdivision.names["en"] ?: "No state",
                        searchIp.cityResponse.country.names["en"] ?: "No country"
                ),
                GeoIpSearchASNV1("AS${searchIp.asnResponse.autonomousSystemNumber}", searchIp.asnResponse.autonomousSystemOrganization))
    }
}