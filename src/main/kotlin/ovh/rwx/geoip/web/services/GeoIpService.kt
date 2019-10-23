package ovh.rwx.geoip.web.services

import ovh.rwx.geoip.web.objects.GeoIp
import java.net.InetAddress

interface GeoIpService {
    fun searchIp(inetAddress: InetAddress): GeoIp
}