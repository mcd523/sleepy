package resources

import client.model.league.SleeperLeague
import client.model.user.SleeperUser
import org.slf4j.LoggerFactory
import services.SleepyService
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
class SleepyUserResource @Inject constructor(private val sleepyService: SleepyService): AsyncResource() {
    companion object {
        private val logger = LoggerFactory.getLogger(SleepyUserResource::class.java)
        private val validSeasons = listOf("2018", "2019", "2020", "2021")
        private val validSports = listOf("nfl")

        fun validatedSeasons(seasons: List<String>) = validate(seasons, validSeasons, "season")
        fun validatedSports(sports: List<String>) = validate(sports, validSports, "sport")
        private fun validate(values: List<String>, validValues: List<String>, term: String): List<String> {
            val invalid = mutableListOf<String>()
            values.forEach {
                if (!validValues.contains(it)) {
                    invalid.add(it)
                }
            }

            if (invalid.isNotEmpty()) {
                throw WebApplicationException(
                    "Invalid $term value(s): $invalid",
                    Response.Status.BAD_REQUEST
                )
            }

            return values.ifEmpty { validValues }
        }
    }

    @GET
    @Path("/{name}")
    fun getUser(@PathParam("name") name: String): SleeperUser {
        return sleepyService.getUser(name)
    }

    @GET
    @Path("/{userId}/leagues")
    fun getLeagues(
        @PathParam("userId") userId: Long,
        @QueryParam("sport") @DefaultValue("nfl") sport: String,
        @QueryParam("season") @DefaultValue("2021") season: String
    ): List<SleeperLeague> {
        return sleepyService.getLeaguesForSeason(userId, sport, season)
    }

    @GET
    @Path("/winner/{userName}")
    fun getWinningLeagues(
        @Suspended response: AsyncResponse,
        @PathParam("userName") userName: String,
        @QueryParam("sport") @DefaultValue("nfl") sports: List<String>,
        @QueryParam("season") seasons: List<String>
    ) {
        val validatedSports = validatedSports(sports)
        val validatedSeasons = validatedSeasons(seasons)

        workerScope.respondAsync(response) {
            sleepyService.getMyWinningLeagues(userName, validatedSports, validatedSeasons)
        }
    }

}
