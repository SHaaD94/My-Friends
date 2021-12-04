package com.github.shaad.myfriends.domain

data class AddPersonRequest(val name: String)
data class DoesPersonExistRequest(val name: String)
data class GetFriendsRequest(val name: String)
data class GetHandshakesRequest(val p1: String, val p2: String)
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


