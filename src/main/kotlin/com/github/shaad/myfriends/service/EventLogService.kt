package com.github.shaad.myfriends.service

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.shaad.myfriends.domain.Event
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import javax.enterprise.context.ApplicationScoped

interface EventLogService {
    fun writeEvent(event: Event)
    fun readEvents(fromTs: Long): Iterator<Event>
}

@ApplicationScoped
class InMemoryEventLogService : EventLogService {
    private val ts2Events = ConcurrentSkipListMap<Long, MutableSet<Event>>()
    override fun writeEvent(event: Event) {
        ts2Events.computeIfAbsent(event.ts) { ConcurrentHashMap.newKeySet() }.add(event)
    }

    override fun readEvents(fromTs: Long): Iterator<Event> =
        ts2Events.tailMap(fromTs).values.asSequence().flatten().iterator()
}