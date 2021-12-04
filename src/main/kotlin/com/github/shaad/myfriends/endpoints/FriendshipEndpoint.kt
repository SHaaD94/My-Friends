package com.github.shaad.myfriends.endpoints

import com.github.shaad.myfriends.domain.AddFriendshipRequest
import com.github.shaad.myfriends.domain.RemoveFriendshipRequest
import com.github.shaad.myfriends.service.FriendshipService
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/friendship")
class FriendshipEndpoint @Inject constructor(val friendshipService: FriendshipService) {
    @POST
    @Path("add")
    fun addFriend(req: AddFriendshipRequest) = friendshipService.addFriendship(req.firstPerson, req.secondPerson)
    @POST
    @Path("remove")
    fun removeFriend(req: RemoveFriendshipRequest) =
        friendshipService.removeFriendship(req.firstPerson, req.secondPerson)
}

