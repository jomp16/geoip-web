package ovh.rwx.geoip.web.controllers.api.v1.geoip

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ovh.rwx.geoip.web.objects.api.v1.geoip.*
import ovh.rwx.geoip.web.services.GeoIpService
import java.net.InetAddress

@RestController
@RequestMapping("/api/v1/geoip")
class GeoIpApiV1Controller(
        @Autowired
        private val geoIpService: GeoIpService
) {
    @PostMapping("/search")
    suspend fun search(@RequestBody geoIpSearchRequestV1Api: Set<String>): List<GeoIpSearchResponseV1Api> {
        val returnSearch = geoIpSearchRequestV1Api.map {
            GlobalScope.async {
                val inetAddress = InetAddress.getByName(it)
                val searchIp = geoIpService.searchIp(inetAddress)

                GeoIpSearchResponseV1Api(
                        GeoIpSearchIpV1(inetAddress.hostAddress, inetAddress.canonicalHostName),
                        GeoIpSearchCityV1(searchIp.cityResponse.city.names["en"] ?: "No city", searchIp.cityResponse.mostSpecificSubdivision.names["en"] ?: "No state", searchIp.cityResponse.country.names["en"] ?: "No country"),
                        GeoIpSearchASNV1("AS${searchIp.asnResponse.autonomousSystemNumber}", searchIp.asnResponse.autonomousSystemOrganization)
                )
            }
        }.awaitAll()

        return returnSearch
    }
}