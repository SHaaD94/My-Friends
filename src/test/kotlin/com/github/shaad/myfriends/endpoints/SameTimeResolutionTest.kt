package com.github.shaad.myfriends.endpoints

import com.github.shaad.myfriends.service.CurrentTimeProvider
import com.github.shaad.myfriends.service.FriendshipService
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.wildfly.common.Assert.assertTrue
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SameTimeResolutionTest {
    private val singleTimeProvider = object : CurrentTimeProvider {
        override fun now(): Long = 0L
    }
    private val service = FriendshipService(singleTimeProvider)

    @Test
    fun `If timestamp of add and remove are same, consider element removed`() {
        service.addPerson("a")
        service.removePerson("a")
        assertFalse(service.doesExist("a"))
    }
}