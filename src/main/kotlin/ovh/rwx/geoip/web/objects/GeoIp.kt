package ovh.rwx.geoip.web.objects

import com.maxmind.geoip2.model.AsnResponse
import com.maxmind.geoip2.model.CityResponse

data class GeoIp(
        val cityResponse: CityResponse?,
        val asnResponse: AsnResponse?
)