package resources

import client.model.league.bracket.BracketType
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.container.AsyncResponse
import jakarta.ws.rs.container.Suspended
import jakarta.ws.rs.core.MediaType
import services.SleepyService

@Path("/league")
@Produces(MediaType.APPLICATION_JSON)
class SleepyLeagueResource @Inject constructor(private val sleepyService: SleepyService): AsyncResource() {

    @GET
    @Path("/bracket/{leagueId}")
    fun getRostersForLeague(
        @Suspended response: AsyncResponse,
        @PathParam("leagueId") leagueId: Long
    ) {
        workerScope.respondAsync(response) {
            sleepyService.getRostersForLeague(leagueId)
        }
    }

    @GET
    @Path("/bracket/{leagueId}/{bracketType}")
    fun getBracketForLeague(
        @Suspended response: AsyncResponse,
        @PathParam("leagueId") leagueId: Long,
        @PathParam("bracketType") bracketType: BracketType
    ) {
        workerScope.respondAsync(response) {
            sleepyService.getBracket(leagueId, bracketType)
        }
    }

    @GET
    @Path("/{leagueId}/members")
    fun getLeagueMembers(
        @Suspended asyncResponse: AsyncResponse,
        @PathParam("leagueId") leagueId: Long
    ) {
        workerScope.respondAsync(asyncResponse) {
            sleepyService.getUsersForLeague(leagueId)
        }
    }

}
