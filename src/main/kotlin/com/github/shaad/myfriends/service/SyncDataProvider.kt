package com.github.shaad.myfriends.service

import com.github.shaad.myfriends.domain.Event
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

interface SyncDataProvider {
    fun getUpdates(fromTs: Long): Sequence<Event>
}

@ApplicationScoped
class CrossServiceSyncDataProvider @Inject constructor(
    private val apiClient: SyncAPIClient,
    private val instancesListProvider: InstancesListProvider
) :
    SyncDataProvider {
    override fun getUpdates(fromTs: Long): Sequence<Event> {
        return instancesListProvider.getServerList().asSequence().flatMap { server ->
            apiClient.getEvents(fromTs, server).toCompletableFuture().get().asSequence()
        }
    }
}
