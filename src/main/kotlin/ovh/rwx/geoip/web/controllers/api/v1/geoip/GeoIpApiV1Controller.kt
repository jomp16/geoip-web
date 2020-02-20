package ovh.rwx.geoip.web.controllers.api.v1.geoip

import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
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
@CrossOrigin
class GeoIpApiV1Controller(
        @Autowired
        private val geoIpService: GeoIpService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/search")
    suspend fun search(@RequestBody geoIpSearchRequestV1ApiFlow: Flow<Any>): Flow<GeoIpSearchResponseV1Api> {
        return geoIpSearchRequestV1ApiFlow.filterNotNull().map { it.toString() }.filterNot { it.isBlank() }.mapNotNull { resolveIp(it, true) }
    }

    @Suppress("SameParameterValue")
    private fun resolveIp(ip: String, resolvePtr: Boolean): GeoIpSearchResponseV1Api? {
        val inetAddress = try {
            InetAddress.getByName(ip)
        } catch (e: UnknownHostException) {
            logger.error("Could't get address from {}", ip)

            return null
        }

        if (inetAddress.isSiteLocalAddress || inetAddress.isLoopbackAddress) {
            logger.error("Private IP found, skipping {}", inetAddress.hostAddress)

            return null
        }

        val searchIp = geoIpService.searchIp(inetAddress)

        var ptr: String? = null

        if (resolvePtr) {
            try {
                ptr = Address.getHostName(inetAddress)?.substringBeforeLast('.')
            } catch (e: UnknownHostException) {
                logger.error("Error looking up the PTR of IP $ip", e)
            }
        }

        return GeoIpSearchResponseV1Api(
                GeoIpSearchIpV1(inetAddress.hostAddress, ptr),
                if (searchIp.cityResponse == null) null else GeoIpSearchCityV1(
                        searchIp.cityResponse.city.names["en"] ?: "No city",
                        searchIp.cityResponse.mostSpecificSubdivision.names["en"] ?: "No state",
                        searchIp.cityResponse.country.names["en"] ?: "No country",
                        searchIp.cityResponse.country.isoCode ?: "ZZ"
                ),
                if (searchIp.asnResponse == null) null else GeoIpSearchASNV1("AS${searchIp.asnResponse.autonomousSystemNumber}", searchIp.asnResponse.autonomousSystemOrganization))
    }
}