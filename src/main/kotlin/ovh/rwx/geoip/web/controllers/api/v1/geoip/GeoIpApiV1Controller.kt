package ovh.rwx.geoip.web.controllers.api.v1.geoip

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ovh.rwx.geoip.web.objects.api.v1.geoip.GeoIpSearchASNV1
import ovh.rwx.geoip.web.objects.api.v1.geoip.GeoIpSearchCityV1
import ovh.rwx.geoip.web.objects.api.v1.geoip.GeoIpSearchIpV1
import ovh.rwx.geoip.web.objects.api.v1.geoip.GeoIpSearchResponseV1Api
import ovh.rwx.geoip.web.services.GeoIpService
import java.net.InetAddress

@RestController
@RequestMapping("/api/v1/geoip")
class GeoIpApiV1Controller(
        @Autowired
        private val geoIpService: GeoIpService
) {
    @PostMapping("/search")
    fun search(@RequestBody geoIpSearchRequestV1Api: Set<String>, @RequestHeader(value = "RESOLVE-PTR", required = false, defaultValue = "false") resolvePtr: Boolean): Flow<GeoIpSearchResponseV1Api> {
        return geoIpSearchRequestV1Api.asFlow().map {
            val inetAddress = InetAddress.getByName(it)

            val searchIp = geoIpService.searchIp(inetAddress)

            GeoIpSearchResponseV1Api(
                    GeoIpSearchIpV1(inetAddress.hostAddress, if (resolvePtr) inetAddress.canonicalHostName else null),
                    GeoIpSearchCityV1(searchIp.cityResponse.city.names["en"]
                            ?: "No city", searchIp.cityResponse.mostSpecificSubdivision.names["en"]
                            ?: "No state", searchIp.cityResponse.country.names["en"] ?: "No country"),
                    GeoIpSearchASNV1("AS${searchIp.asnResponse.autonomousSystemNumber}", searchIp.asnResponse.autonomousSystemOrganization))
        }
    }
}