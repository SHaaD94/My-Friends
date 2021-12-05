package com.github.shaad.myfriends.service

import com.github.shaad.myfriends.domain.Event
import javax.enterprise.context.ApplicationScoped

interface SyncDataProvider {
    fun getUpdates(fromTs: Long): Sequence<Event>
}

@ApplicationScoped
class CrossServiceSyncDataProvider(private val apiClient: SyncAPIClient) : SyncDataProvider {
    override fun getUpdates(fromTs: Long): Sequence<Event> {
        return apiClient.getEvents(fromTs, "http://127.0.0.1:8081").toCompletableFuture().get().asSequence()
    }
}
