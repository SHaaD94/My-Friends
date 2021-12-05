package com.github.shaad.myfriends.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

data class AddPersonRequest(val name: String)
data class DoesPersonExistRequest(val name: String)
data class DoesPersonExistResponse(val exists: Boolean)
data class GetFriendsRequest(val name: String)
data class GetFriendsResponse(val friends: List<String>)
data class GetHandshakesRequest(val p1: String, val p2: String)
data class GetHandshakesResponse(val handshakes: List<String>)
data class RemovePersonRequest(val name: String)
data class AddFriendshipRequest(val firstPerson: String, val secondPerson: String)
data class RemoveFriendshipRequest(val firstPerson: String, val secondPerson: String)

data class Person constructor(val name: String) : Comparable<Person> {
    override fun compareTo(other: Person): Int = name.compareTo(other.name)
}

data class Friendship private constructor(val p1: Person, val p2: Person) {
    companion object {
        fun instance(p1: Person, p2: Person) = Friendship(minOf(p1, p2), maxOf(p1, p2))
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AddPersonEvent::class, name = "addPersonEvent"),
    JsonSubTypes.Type(value = RemovePersonEvent::class, name = "removePersonEvent"),
    JsonSubTypes.Type(value = AddFriendshipEvent::class, name = "addFriendshipEvent"),
    JsonSubTypes.Type(value = RemoveFriendshipEvent::class, name = "removeFriendshipEvent")
)
sealed class Event(open val ts: Long)
data class AddPersonEvent(override val ts: Long, val name: String) : Event(ts)
data class RemovePersonEvent(override val ts: Long, val name: String) : Event(ts)
data class AddFriendshipEvent(override val ts: Long, val from: String, val to: String) : Event(ts)
data class RemoveFriendshipEvent(override val ts: Long, val from: String, val to: String) : Event(ts)

