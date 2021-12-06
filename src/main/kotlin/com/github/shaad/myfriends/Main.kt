package com.github.shaad.myfriends

import com.github.shaad.myfriends.service.FriendshipService
import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.Startup
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

fun main(args: Array<String>) {
    Quarkus.run(*args)
}

@Startup
@ApplicationScoped
class AppInitializer @Inject constructor(friendshipService: FriendshipService) {
    init {
        friendshipService.startSync()
    }
}
