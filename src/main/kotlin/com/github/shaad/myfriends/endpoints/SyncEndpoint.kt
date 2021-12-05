package com.github.shaad.myfriends.endpoints

import com.github.shaad.myfriends.domain.Event
import com.github.shaad.myfriends.service.EventLogService
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/sync")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SyncEndpoint(private val logService: EventLogService) {
    @GET
    @Path("/events")
    fun getEvents(@QueryParam("fromTs") fromTs: Long): List<Event> {
        return logService.readEvents(fromTs).asSequence().toList()
    }
}
