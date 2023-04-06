package resources

import client.model.league.bracket.BracketType
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.container.AsyncResponse
import jakarta.ws.rs.container.Suspended
import jakarta.ws.rs.core.MediaType
import services.SleepyService

@Path("/league")
@Produces(MediaType.APPLICATION_JSON)
class SleepyLeagueResource @Inject constructor(private val sleepyService: SleepyService): AsyncResource() {

    @GET
    @Path("/{leagueId}/bracket")
    fun getRostersForLeague(
        @Suspended response: AsyncResponse,
        @PathParam("leagueId") leagueId: Long
    ) {
        workerScope.respondAsync(response) {
            sleepyService.getRostersForLeague(leagueId)
        }
    }

    @GET
    @Path("/{leagueId}/bracket/{bracketType}")
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

    @GET
    @Path("/{userName}/winner")
    fun getWinningLeagues(
        @Suspended response: AsyncResponse,
        @PathParam("userName") userName: String,
        @QueryParam("sport") @DefaultValue("nfl") sports: List<String>,
        @QueryParam("season") seasons: List<String>
    ) {
        val validatedSports = SleepyUserResource.validatedSports(sports)
        val validatedSeasons = SleepyUserResource.validatedSeasons(seasons)

        workerScope.respondAsync(response) {
            sleepyService.getMyWinningLeagues(userName, validatedSports, validatedSeasons)
        }
    }
}
