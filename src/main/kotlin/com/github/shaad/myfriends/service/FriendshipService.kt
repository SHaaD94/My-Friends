package com.github.shaad.myfriends.service

import com.github.shaad.myfriends.domain.Person
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class FriendshipService {
    fun addPerson(person: Person) = "Person ${person.name} added"
}