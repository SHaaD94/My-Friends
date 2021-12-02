package com.github.shaad.myfriends.endpoints

import com.github.shaad.myfriends.domain.AddPersonRequest
import com.github.shaad.myfriends.domain.Friendship
import com.github.shaad.myfriends.domain.Person
import com.github.shaad.myfriends.domain.RemovePersonRequest
import com.github.shaad.myfriends.service.FriendshipService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/person")
class PersonEndpoint @Inject constructor(val friendshipService: FriendshipService) {
    @POST
    @Path("add")
    fun addPerson(req: AddPersonRequest) = friendshipService.addPerson(req.person)
    @POST
    @Path("remove")
    fun removePerson(req: RemovePersonRequest) = friendshipService.addPerson(req.person)
    @POST
    @Path("exists")
    fun doesPersonExist(req: Person) = true
    @GET
    @Path("friends")
    fun getFriends(req: Person) = listOf<Person>()
    @GET
    @Path("handshakes")
    fun handshakes(req: Person) = listOf<Friendship>()
}

