package com.github.shaad.myfriends.service

import com.github.shaad.myfriends.domain.Event
import javax.enterprise.context.ApplicationScoped

interface SyncDataProvider {
    fun getUpdates(fromTs: Long): Iterator<Event>
}

@ApplicationScoped
class CrossServiceSyncDataProvider(private val logService: EventLogService) : SyncDataProvider {
    override fun getUpdates(fromTs: Long): Iterator<Event> {
        return logService.readEvents(fromTs)
    }
}