package com.github.shaad.myfriends.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

// FIXME - ALL VARS AND DEFAULT VARIABLES HERE ARE WORKAROUND OF NATIVE IMAGE JACKSON KOTLIN REFLECTION ISSUES
data class AddPersonRequest(var name: String = "")
data class DoesPersonExistRequest(var name: String = "")
data class DoesPersonExistResponse(var exists: Boolean = false)
data class GetFriendsRequest(var name: String = "")
data class GetFriendsResponse(var friends: List<String> = emptyList())
data class GetHandshakesRequest(var p1: String = "", var p2: String = "")
data class GetHandshakesResponse(var handshakes: List<String> = emptyList())
data class RemovePersonRequest(var name: String = "")
data class AddFriendshipRequest(var firstPerson: String = "", var secondPerson: String = "")
data class RemoveFriendshipRequest(var firstPerson: String = "", var secondPerson: String = "")

data class Person constructor(val name: String) : Comparable<Person> {
    override fun compareTo(other: Person): Int = name.compareTo(other.name)
}

data class Friendship private constructor(val p1: Person, val p2: Person) {
    companion object {
        fun instance(p1: Person, p2: Person) = Friendship(minOf(p1, p2), maxOf(p1, p2))
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AddPersonEvent::class, name = "addPersonEvent"),
    JsonSubTypes.Type(value = RemovePersonEvent::class, name = "removePersonEvent"),
    JsonSubTypes.Type(value = AddFriendshipEvent::class, name = "addFriendshipEvent"),
    JsonSubTypes.Type(value = RemoveFriendshipEvent::class, name = "removeFriendshipEvent")
)
sealed class Event(open var ts: Long = 0)
data class AddPersonEvent(override var ts: Long = 0, var name: String = "") : Event(ts)
data class RemovePersonEvent(override var ts: Long = 0, var name: String = "") : Event(ts)
data class AddFriendshipEvent(override var ts: Long = 0, var from: String = "", var to: String = "") : Event(ts)
data class RemoveFriendshipEvent(override var ts: Long = 0, var from: String = "", var to: String = "") : Event(ts)

