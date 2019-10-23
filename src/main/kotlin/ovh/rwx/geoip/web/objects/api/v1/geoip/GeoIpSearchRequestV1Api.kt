package ovh.rwx.geoip.web.objects.api.v1.geoip

import com.fasterxml.jackson.annotation.JsonProperty

data class GeoIpSearchRequestV1Api(
        @JsonProperty("ip")
        val ip: String
)