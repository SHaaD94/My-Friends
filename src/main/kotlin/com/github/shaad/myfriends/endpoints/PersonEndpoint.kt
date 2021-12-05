package com.github.shaad.myfriends.endpoints

import com.github.shaad.myfriends.domain.*
import com.github.shaad.myfriends.service.FriendshipService
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/person")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PersonEndpoint @Inject constructor(val friendshipService: FriendshipService) {
    @POST
    @Path("add")
    fun addPerson(req: AddPersonRequest) {
        friendshipService.addPerson(req.name)
    }

    @DELETE
    @Path("remove")
    fun removePerson(req: RemovePersonRequest) {
        friendshipService.removePerson(req.name)
    }

    @POST
    @Path("exists")
    fun doesPersonExist(req: DoesPersonExistRequest) = DoesPersonExistResponse(friendshipService.doesExist(req.name))

    @POST
    @Path("friends")
    fun getFriends(req: GetFriendsRequest) = GetFriendsResponse(friendshipService.getFriends(req.name))

    @POST
    @Path("handshakes")
    fun handshakes(req: GetHandshakesRequest) = GetHandshakesResponse(friendshipService.getHandshakes(req.p1, req.p2))
}

