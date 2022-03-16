package resources

import client.model.PlayoffMatchup
import client.model.league.SleeperRoster
import client.model.league.bracket.BracketType
import services.SleepyService
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

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
        return sleepyService.getBracket(leagueId, bracketType)
    }

}
