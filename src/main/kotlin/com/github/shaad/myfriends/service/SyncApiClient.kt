package com.github.shaad.myfriends.service

import com.github.shaad.myfriends.domain.Event
import com.github.shaad.myfriends.util.WithLogger
import javax.enterprise.context.ApplicationScoped
import java.util.concurrent.Executors
import javax.ws.rs.client.ClientBuilder
import java.util.concurrent.CompletionStage
import javax.inject.Inject
import javax.ws.rs.core.GenericType

@ApplicationScoped
class SyncAPIClient @Inject constructor() : WithLogger {
    private val executorService = Executors.newCachedThreadPool()
    private val client = ClientBuilder.newBuilder()
        .executorService(executorService)
        .build()

    fun getEvents(
        fromTs: Long,
        apiUrl: String
    ): CompletionStage<List<Event>> {
        log().info("Requesting sync events from $apiUrl")
        return client.target("$apiUrl/sync/events")
            .queryParam("fromTs", fromTs)
            .request()
            .rx()
            .get(object : GenericType<List<Event>>() {})
    }
}
