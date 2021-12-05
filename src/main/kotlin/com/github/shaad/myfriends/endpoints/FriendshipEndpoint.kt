package com.github.shaad.myfriends.endpoints

import com.github.shaad.myfriends.domain.AddFriendshipRequest
import com.github.shaad.myfriends.domain.RemoveFriendshipRequest
import com.github.shaad.myfriends.service.FriendshipService
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/friendship")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FriendshipEndpoint @Inject constructor(val friendshipService: FriendshipService) {
    @POST
    @Path("add")
    fun addFriend(req: AddFriendshipRequest) {
        friendshipService.addFriendship(req.firstPerson, req.secondPerson)
    }

    @DELETE
    @Path("remove")
    fun removeFriend(req: RemoveFriendshipRequest) {
        friendshipService.removeFriendship(req.firstPerson, req.secondPerson)
    }
}

