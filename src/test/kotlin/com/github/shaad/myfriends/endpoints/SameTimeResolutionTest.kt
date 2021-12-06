package com.github.shaad.myfriends.endpoints

import com.github.shaad.myfriends.service.CurrentTimeProvider
import com.github.shaad.myfriends.service.FriendshipService
import com.github.shaad.myfriends.service.InMemoryEventLogService
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class SameTimeResolutionTest {
    private val singleTimeProvider = object : CurrentTimeProvider {
        override fun now(): Long = 0L
    }
    private val service = FriendshipService(singleTimeProvider, InMemoryEventLogService())

    @Test
    fun `If timestamp of add and remove are same, consider element removed`() {
        service.addPerson("a")
        service.removePerson("a")
        assertFalse(service.doesExist("a"))
    }
}