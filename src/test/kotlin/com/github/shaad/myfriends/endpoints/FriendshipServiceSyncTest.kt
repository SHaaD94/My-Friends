package com.github.shaad.myfriends.endpoints;

import com.github.shaad.myfriends.service.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.wildfly.common.Assert
import java.util.concurrent.atomic.AtomicLong

class FriendshipServiceSyncTest {
    private val manualTimeProvider = object : CurrentTimeProvider {
        val now = 0L
        override fun now(): Long = now
    }
    private val syncProvider = object : SyncDataProvider {
        private val events = ArrayList<Event>()
        fun addEvents(vararg events: Event) {
            events.forEach { this.events.add(it) }
        }

        override fun getUpdates(fromTs: Long): Iterator<Event> = events.filter { it.ts >= fromTs }.iterator()
    }
    private val service =
        FriendshipService(manualTimeProvider, InMemoryEventLogService(), syncProvider)

    @Test
    fun `Must properly sync`() {
        service.addPerson("a")
        service.addPerson("b")
        service.addPerson("c")
        service.addPerson("d")
        service.addFriendship("a", "b")
        service.addFriendship("b", "c")
        service.addFriendship("c", "d")
        assertTrue(service.doesExist("a"))
        Assertions.assertIterableEquals(listOf("b", "c", "d"), service.getFriends("a"))
        syncProvider.addEvents(
            RemoveFriendshipEvent(-1L, "a", "b"),
            RemovePersonEvent(-1L, "a"),
            AddPersonEvent(1L, "e"),
            AddFriendshipEvent(1L, "d", "e"),
            RemoveFriendshipEvent(1L, "a", "b"),
            RemovePersonEvent(2L, "c"),
        )
        service.sync()
        assertTrue(service.doesExist("a"))
        Assertions.assertIterableEquals(emptyList<String>(), service.getFriends("a"))
        assertFalse(service.doesExist("c"))
        Assertions.assertIterableEquals(listOf("e"), service.getFriends("d"))
        Assertions.assertIterableEquals(listOf("d"), service.getFriends("e"))
    }
}