package com.github.shaad.myfriends.endpoints

import com.github.shaad.myfriends.domain.Event
import com.github.shaad.myfriends.service.*
import org.junit.jupiter.api.Test
import org.wildfly.common.Assert.assertTrue
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.assertEquals

class FriendshipServiceTest {
    private val monotonicallyIncreasingTimeProvider = object : CurrentTimeProvider {
        private val now = AtomicLong(0)
        override fun now(): Long = now.incrementAndGet()
    }
    private val emptySyncProvider = object : SyncDataProvider {
        override fun getUpdates(fromTs: Long): Sequence<Event> = emptySequence()
    }
    private val service =
        FriendshipService(monotonicallyIncreasingTimeProvider, InMemoryEventLogService(), emptySyncProvider)

    @Test
    fun `Must add and check existence properly`() {
        service.addPerson("a")
        assertTrue(service.doesExist("a"))
    }

    @Test
    fun `Must add friendship properly`() {
        service.addPerson("a")
        service.addPerson("b")
        service.addFriendship("a", "b")
    }

    @Test
    fun `Must properly get friends`() {
        service.addPerson("a")
        service.addPerson("b")
        service.addPerson("c")
        service.addPerson("d")
        service.addPerson("e")
        service.addFriendship("a", "b")
        service.addFriendship("c", "b")
        service.addFriendship("d", "e")

        assertEquals(listOf("b", "c"), service.getFriends("a"))
        assertEquals(listOf("d"), service.getFriends("e"))
    }

    @Test
    fun `If friend removed, friendships must be removed as well`() {
        service.addPerson("a")
        service.addPerson("b")
        service.addPerson("c")
        service.addFriendship("a", "b")
        service.addFriendship("c", "b")
        service.removePerson("b")

        assertEquals(emptyList(), service.getFriends("a"))
        assertEquals(emptyList(), service.getFriends("c"))

        service.addPerson("b")

        assertEquals(emptyList(), service.getFriends("a"))
        assertEquals(emptyList(), service.getFriends("c"))
    }

    @Test
    fun `Must properly get paths`() {
        service.addPerson("a")
        service.addPerson("b")
        service.addPerson("c")
        service.addPerson("d")
        service.addPerson("e")
        service.addFriendship("a", "b")
        service.addFriendship("c", "b")
        service.addFriendship("d", "e")

        assertEquals(listOf("a", "b", "c"), service.getHandshakes("a", "c"))
        assertEquals(listOf("a", "b"), service.getHandshakes("a", "b"))
        assertEquals(listOf("b", "c"), service.getHandshakes("b", "c"))
        assertEquals(listOf("e", "d"), service.getHandshakes("e", "d"))
        assertEquals(emptyList(), service.getHandshakes("a", "d"))
    }

    @Test
    fun `Must not find a friend if friendship was removed`() {
        service.addPerson("a")
        service.addPerson("b")
        service.addPerson("c")

        service.addFriendship("a", "b")
        service.addFriendship("b", "c")
//        assertEquals(listOf("b", "c"), service.getFriends("a"))
        service.removeFriendship("b", "c")
        assertEquals(listOf("b"), service.getFriends("a"))
    }

    @Test
    fun `Must not find a friend if person was removed`() {
        service.addPerson("a")
        service.addPerson("b")
        service.addPerson("c")

        service.addFriendship("a", "b")
        service.addFriendship("b", "c")

        assertEquals(listOf("b", "c"), service.getFriends("a"))
        service.removePerson("c")
        assertEquals(listOf("b"), service.getFriends("a"))
        service.removePerson("b")
        assertEquals(emptyList(), service.getFriends("a"))
    }

    @Test
    fun `People should stop being friend if connecting friend was removed`() {
        service.addPerson("a")
        service.addPerson("b")
        service.addPerson("c")

        service.addFriendship("a", "b")
        service.addFriendship("b", "c")

        assertEquals(listOf("b", "c"), service.getFriends("a"))
        service.removePerson("b")
        assertEquals(emptyList(), service.getFriends("a"))
        assertEquals(emptyList(), service.getFriends("c"))
    }

    @Test
    fun `Must properly find different path then one was deleted`() {
        service.addPerson("a")
        service.addPerson("b")
        service.addPerson("c")
        service.addPerson("d")
        service.addPerson("e")

        service.addFriendship("a", "b")
        service.addFriendship("c", "b")
        service.addFriendship("c", "e")

        assertEquals(listOf("a", "b", "c", "e"), service.getHandshakes("a", "e"))
        service.addFriendship("d", "e")
        service.addFriendship("d", "c")

        assertEquals(listOf("a", "b", "c", "e"), service.getHandshakes("a", "e"))
        service.removeFriendship("c", "e")

        assertEquals(listOf("a", "b", "c", "d", "e"), service.getHandshakes("a", "e"))
    }
}