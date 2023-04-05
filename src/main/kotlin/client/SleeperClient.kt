package client

import SleepyConfiguration
import client.model.league.bracket.BracketType
import client.model.draft.SleeperDraft
import client.model.draft.SleeperPick
import client.model.league.*
import client.model.league.bracket.Bracket
import client.model.player.PlayerTrend
import client.model.player.SleeperPlayer
import client.model.player.TrendingPlayer
import client.model.user.SleeperUser
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.ws.rs.HttpMethod
import jakarta.ws.rs.ServerErrorException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.core.GenericType
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.future.await
import org.slf4j.LoggerFactory
import util.SuspendableRetry
import java.time.Duration
import java.util.concurrent.CompletionStage

class SleeperClient @Inject constructor(config: SleepyConfiguration, client: Client, private val mapper: ObjectMapper) {
    companion object {
        private val logger = LoggerFactory.getLogger(SleeperClient::class.java)
    }

    private val retryPolicy = SuspendableRetry(
        listOf(ServerErrorException::class.java),
        maxRetries = 6,
        delay = Duration.ofMillis(100)
    )

    private val webTarget = client
        .target(config.baseUrl)
//        .register(LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.HEADERS_ONLY, 8192))

    // USERS
    suspend fun getUser(userId: Long): SleeperUser = getUser(userId.toString())
    suspend fun getUser(userName: String): SleeperUser {
        val path = "/user/$userName"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<SleeperUser>() {})
    }

    // LEAGUES
    suspend fun getLeaguesForSeason(userId: Long, sport: String, season: String): List<SleeperLeague> {
        val path = "/user/$userId/leagues/$sport/$season"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<List<SleeperLeague>>() {})
    }

    suspend fun getLeagueById(leagueId: Long): SleeperLeague {
        val path = "/league/$leagueId"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<SleeperLeague>() {})
    }

    suspend fun getRostersForLeague(leagueId: Long): List<SleeperRoster> {
        val path = "/league/$leagueId/rosters"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<List<SleeperRoster>>() {})
    }

    suspend fun getUsersForLeague(leagueId: Long): List<SleeperUser> {
        val path = "/league/$leagueId/users"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<List<SleeperUser>>() {})
    }

    suspend fun getMatchupsForLeague(leagueId: Long, week: Long): List<SleeperMatchup>  {
        val path = "/league/$leagueId/matchups/$week"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<List<SleeperMatchup>>() {})
    }

    suspend fun getPlayoffBracket(leagueId: Long, bracket: BracketType): Bracket {
        val path = "/league/$leagueId/${bracket.bracketName}"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<Bracket>() {})
    }

    suspend fun getLeagueTransactions(leagueId: Long, round: Long): List<SleeperTransaction> {
        val path = "/league/$leagueId/transactions/$round"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<List<SleeperTransaction>>() {})
    }

    suspend fun getTradedPicksForLeague(leagueId: Long): List<SleeperTradedPick> {
        val path = "/league/$leagueId/traded_picks"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<List<SleeperTradedPick>>() {})
    }

    suspend fun getState(sport: String): SleeperState {
        val path = "/state/$sport"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<SleeperState>() {})
    }

    // DRAFTS
    suspend fun getDraftsForUser(userId: Long, sport: String, season: String): List<SleeperDraft> {
        val path = "/user/$userId/drafts/$sport/$season"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<List<SleeperDraft>>() {})
    }

    suspend fun getDraftsForLeague(leagueId: Long): List<SleeperDraft> {
        val path = "/user/$leagueId/drafts"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<List<SleeperDraft>>() {})
    }

    suspend fun getDraft(draftId: Long): SleeperDraft {
        val path = "/draft/$draftId"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<SleeperDraft>() {})
    }

    suspend fun getPicksForDraft(draftId: Long): List<SleeperPick> {
        val path = "/draft/$draftId/picks"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<List<SleeperPick>>() {})
    }

    suspend fun getTradedPicksForDraft(draftId: Long): List<SleeperTradedPick> {
        val path = "/draft/$draftId/traded_picks"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<List<SleeperTradedPick>>() {})
    }

    // PLAYERS
    suspend fun getAllPlayers(sport: String): Map<String, SleeperPlayer> {
        val path = "/players/$sport"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request, object: GenericType<Map<String, SleeperPlayer>>() {})
    }

    suspend fun getTrendingPlayers(sport: String, type: PlayerTrend, lookbackHours: Long = 24, limit: Long = 25): List<TrendingPlayer> {
        val path = "/players/$sport/trending/${type.trendName}"
        val queryParams = buildQueryParams(listOf("lookback_hours" to lookbackHours, "limit" to limit))
        val request = buildRequest(path, HttpMethod.GET, queryParams)

        return execute(request, object: GenericType<List<TrendingPlayer>>() {})
    }

    private fun buildRequest(
        path: String,
        method: String,
        params: Map<String, List<String>> = mapOf(),
        entity: Entity<*>? = null,
        vararg mediaTypes: MediaType = arrayOf(MediaType.APPLICATION_JSON_TYPE)
    ): CompletionStage<Response> {
        return webTarget
            .path(path)
            .also { params.forEach { (t, u) -> it.queryParam(t, u) } }
            .request()
            .accept(*mediaTypes)
            .rx()
            .method(method, entity)
    }

    private fun buildQueryParams(rawParams: List<Pair<String, Any>>): Map<String, List<String>> {
        val final = mutableMapOf<String, MutableList<String>>()
        rawParams.forEach { (key, value) ->
            if (final[key] != null) {
                final[key]!!.add(value.toString())
            } else {
                final[key] = mutableListOf(value.toString())
            }
        }

        return final
    }

    private suspend inline fun <reified T: Any> execute(request: CompletionStage<Response>, genericType: GenericType<T>): T {
        val result: T = retryPolicy.retry {
            request.await().readEntity(genericType)
        }

        return result
    }
}
