package com.github.shaad.myfriends.endpoints

import com.github.shaad.myfriends.domain.ChunksRequest
import com.github.shaad.myfriends.domain.Event
import com.github.shaad.myfriends.service.EventLogService
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/sync")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SyncEndpoint(private val logService: EventLogService) {
    @POST
    @Path("/events")
    fun getEvents(req: ChunksRequest): List<Event> {
        return logService.readEvents(req.fromTs).asSequence().toList()
    }
}
