package com.bigosvaap.util.http

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.net.UnknownHostException

@Component
class ServiceUtil @Autowired constructor(@Value("\${server.port}") private val port: String) {

    val serviceAddress: String
        get() = "${findMyHostname()}/${findMyIpAddress()}:$port"

    private fun findMyHostname(): String? {
        return try {
            InetAddress.getLocalHost().hostName
        } catch (e: UnknownHostException) {
            "unknown host name"
        }
    }

    private fun findMyIpAddress(): String? {
        return try {
            InetAddress.getLocalHost().hostAddress
        } catch (e: UnknownHostException) {
            "unknown IP address"
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ServiceUtil::class.java)
    }
}