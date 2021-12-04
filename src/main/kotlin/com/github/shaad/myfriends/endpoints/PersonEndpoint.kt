package com.github.shaad.myfriends.endpoints

import com.github.shaad.myfriends.domain.*
import com.github.shaad.myfriends.service.FriendshipService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/person")
class PersonEndpoint @Inject constructor(val friendshipService: FriendshipService) {
    @POST
    @Path("add")
    fun addPerson(req: AddPersonRequest) = friendshipService.addPerson(req.name)
    @POST
    @Path("remove")
    fun removePerson(req: RemovePersonRequest) = friendshipService.removePerson(req.name)
    @POST
    @Path("exists")
    fun doesPersonExist(req: DoesPersonExistRequest) = friendshipService.doesExist(req.name)
    @GET
    @Path("friends")
    fun getFriends(req: GetFriendsRequest) = friendshipService.getFriends(req.name)
    @GET
    @Path("handshakes")
    fun handshakes(req: GetHandshakesRequest) = friendshipService.getHandshakes(req.p1, req.p2)
}

