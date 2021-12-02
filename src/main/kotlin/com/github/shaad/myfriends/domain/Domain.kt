package com.github.shaad.myfriends.domain

abstract class LWWRequest(open val timestamp: Long)
data class AddPersonRequest constructor(val person: Person, override val timestamp: Long) : LWWRequest(timestamp)
data class AddFriendshipRequest constructor(val friendship: Friendship, override val timestamp: Long) :
    LWWRequest(timestamp)

data class RemovePersonRequest constructor(val person: Person, override val timestamp: Long) :
    LWWRequest(timestamp)

data class RemoveFriendshipRequest constructor(val friendship: Friendship, override val timestamp: Long) :
    LWWRequest(timestamp)

data class Person constructor(val name: String)
data class Friendship constructor(val p1: Person, val p2: Person)


