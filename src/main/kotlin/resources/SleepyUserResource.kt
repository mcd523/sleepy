package resources

import client.model.league.SleeperLeague
import client.model.user.SleeperUser
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.container.AsyncResponse
import jakarta.ws.rs.container.Suspended
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import redis.SleepyCache
import services.SleepyService

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
class SleepyUserResource @Inject constructor(
    private val sleepyService: SleepyService,
    private val sleepyCache: SleepyCache
): AsyncResource() {
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
    fun getUser(@PathParam("name") name: String, @QueryParam("cache") cache: Boolean = false): SleeperUser {
        return sleepyService.getUser(name, cache)
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

    @GET
    @Path("redis")
    fun getFoo(@Suspended response: AsyncResponse) {
        workerScope.respondAsync(response) {
            sleepyCache.getFoo()
        }
    }

    @POST
    @Path("redis")
    fun setFoo(@Suspended response: AsyncResponse) {
        workerScope.respondAsync(response) {
            sleepyCache.setFoo()
        }
    }

}
