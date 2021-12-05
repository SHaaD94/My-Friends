package com.github.shaad.myfriends.endpoints

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

@QuarkusTest
class EndpointsFunctionalTests : BaseFunctionalTest() {
    @Test
    fun `Should properly add person`() {
        addPerson("name")
    }

    @Test
    fun `Should properly remove person`() {
        removePerson("name")
    }

    @Test
    fun `Should properly check person existence`() {
        addPerson("person1")
        assertTrue(checkExistence("person1"))
    }

    @Test
    fun `Should properly add friendship`() {
        addFriendship("a", "b")
    }

    @Test
    fun `Should properly remove friendship`() {
        removeFriendship("a", "b")
    }

    @Test
    fun `Should properly get friends`() {
        addPerson("a")
        addPerson("b")
        addPerson("c")
        addFriendship("a", "b")
        addFriendship("b", "c")

        assertContentEquals(listOf("b", "c"), getFriends("a").friends, "Wrong content")
    }

    @Test
    fun `Should properly get path between friends`() {
        addPerson("a")
        addPerson("b")
        addPerson("c")
        addFriendship("a", "b")
        addFriendship("b", "c")

        assertContentEquals(listOf("a", "b", "c"), getHandshakes("a", "c").handshakes, "Wrong content")
    }

}