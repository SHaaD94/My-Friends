package com.github.shaad.myfriends.service

import com.github.shaad.myfriends.domain.Event
import com.github.shaad.myfriends.util.WithLogger
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.lang.Long.max
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

interface SyncManager {
    fun startSync()
    fun stopSync()
}

@ApplicationScoped
class CrossServiceSyncManager @Inject constructor(
    private val apiClient: SyncAPIClient,
    private val instancesListProvider: InstancesListProvider,
    private val friendshipService: FriendshipService
) : SyncManager, WithLogger {
    private val schedulerES = Executors.newSingleThreadScheduledExecutor()
    private val lastSyncDate = AtomicLong(0)

    @ConfigProperty(name = "sync.frequency")
    private var syncFrequency: Long = 60
    private lateinit var schedulingTask: ScheduledFuture<*>

    override fun startSync() {
        log().info("Start sync scheduler")
        schedulingTask = schedulerES.scheduleAtFixedRate({ sync() }, syncFrequency, syncFrequency, TimeUnit.SECONDS)
    }

    override fun stopSync() {
        schedulingTask.cancel(true)
    }

    private fun sync() {
        if (instancesListProvider.getServerList().isEmpty()) return
        val lastSynced = lastSyncDate.get()
        var maxTs = lastSynced
        try {
            log().info("Starting sync")
            var eventsCounter = 0
            instancesListProvider.getServerList().asSequence().flatMap { server ->
                apiClient.getEvents(lastSynced, server).toCompletableFuture().get().asSequence()
            }.onEach {
                maxTs = max(maxTs, it.ts)
                eventsCounter += 1
            }.forEach { event -> friendshipService.feedEvent(event) }
            log().info("Sync completed - processed $eventsCounter events")
            lastSyncDate.set(maxTs + 1)
        } catch (e: Exception) {
            log().error("Failed to sync", e)
        }
    }
}
