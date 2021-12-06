package com.github.shaad.myfriends.endpoints;

import com.github.shaad.myfriends.domain.*
import com.github.shaad.myfriends.service.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FriendshipServiceSyncTest {
    private val manualTimeProvider = object : CurrentTimeProvider {
        val now = 0L
        override fun now(): Long = now
    }
    private val service =
        FriendshipService(manualTimeProvider, InMemoryEventLogService())

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

        sequenceOf(
            RemoveFriendshipEvent(-1L, "a", "b"),
            RemovePersonEvent(-1L, "a"),
            AddPersonEvent(1L, "e"),
            AddFriendshipEvent(1L, "d", "e"),
            RemoveFriendshipEvent(1L, "a", "b"),
            RemovePersonEvent(2L, "c"),
        ).forEach { service.feedEvent(it) }

        assertTrue(service.doesExist("a"))
        Assertions.assertIterableEquals(emptyList<String>(), service.getFriends("a"))
        assertFalse(service.doesExist("c"))
        Assertions.assertIterableEquals(listOf("e"), service.getFriends("d"))
        Assertions.assertIterableEquals(listOf("d"), service.getFriends("e"))
    }
}