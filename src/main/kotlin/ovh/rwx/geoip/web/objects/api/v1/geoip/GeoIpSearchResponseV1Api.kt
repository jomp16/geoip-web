package ovh.rwx.geoip.web.objects.api.v1.geoip

import com.fasterxml.jackson.annotation.JsonProperty

data class GeoIpSearchResponseV1Api(
        @JsonProperty("ip")
        val ip: GeoIpSearchIpV1,
        @JsonProperty("city")
        val city: GeoIpSearchCityV1,
        @JsonProperty("asn")
        val asn: GeoIpSearchASNV1
)

data class GeoIpSearchIpV1(
        @JsonProperty("ip")
        val ip: String,
        @JsonProperty("ptr")
        val ptr: String?
)

data class GeoIpSearchCityV1(
        @JsonProperty("name")
        val name: String,
        @JsonProperty("state")
        val state: String,
        @JsonProperty("country")
        val country: String
)

data class GeoIpSearchASNV1(
        @JsonProperty("number")
        val number: String,
        @JsonProperty("name")
        val name: String
)
