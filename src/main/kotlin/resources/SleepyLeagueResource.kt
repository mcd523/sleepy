package resources

import client.model.league.SleeperRoster
import client.model.league.bracket.BracketType
import client.model.league.bracket.PlayoffMatchup
import client.model.user.SleeperUser
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
        @PathParam("leagueId") leagueId: Long
    ): List<SleeperRoster> {
        return sleepyService.getRostersForLeague(leagueId)
    }

    @GET
    @Path("/bracket/{leagueId}/{bracketType}")
    fun getBracketForLeague(
        @PathParam("leagueId") leagueId: Long,
        @PathParam("bracketType") bracketType: BracketType
    ): List<PlayoffMatchup> {
        val bracket = sleepyService.getBracket(leagueId, bracketType)
        return bracket
    }

    @GET
    @Path("/{leagueId}/members")
    fun getLeagueMembers(@Suspended asyncResponse: AsyncResponse, @PathParam("leagueId") leagueId: Long){
        return workerScope.respondAsync(asyncResponse) {
            sleepyService.getUsersForLeague(leagueId)
        }
    }

}
