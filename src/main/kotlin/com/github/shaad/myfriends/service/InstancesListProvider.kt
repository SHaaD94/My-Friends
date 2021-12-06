package com.github.shaad.myfriends.service

import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.ApplicationScoped

interface InstancesListProvider {
    fun getServerList(): List<String>
}

@ApplicationScoped
class ConfigBasedInstancesListProvider : InstancesListProvider {
    @ConfigProperty(name = "sync.instances", defaultValue = "")
    var rawConfigValue: String? = null
    val parsedServerList: List<String> by lazy {
        rawConfigValue
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }

    override fun getServerList(): List<String> = parsedServerList
}