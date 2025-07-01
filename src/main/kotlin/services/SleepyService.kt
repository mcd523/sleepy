package services

import client.SleeperClient
import client.model.league.bracket.BracketType
import client.model.league.SleeperLeague
import client.model.league.SleeperRoster
import client.model.user.SleeperUser
import client.model.league.bracket.Bracket
import client.model.player.SleeperPlayer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import kotlinx.coroutines.*
import org.jvnet.hk2.annotations.Service
import org.slf4j.LoggerFactory
import pmap
import pmapSuspend
import redis.SleepyCache

@Service
class SleepyService @Inject constructor(
    private val sleeper: SleeperClient,
    private val sleepyCache: SleepyCache
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SleepyService::class.java)

        data class LeagueSeason(val sport: String, val season: String, val leagues: List<SleeperLeague>)
        data class LeagueRoster(val league: SleeperLeague, val roster: SleeperRoster)
    }

    suspend fun getUser(userName: String): SleeperUser {
        return sleepyCache.getUser(userName)
    }

    suspend fun getLeaguesForSeason(userId: Long, sport: String, season: String): List<SleeperLeague> {
        return sleeper.getLeaguesForSeason(userId, sport, season)
    }

    suspend fun getRostersForLeague(leagueId: Long): List<SleeperRoster> {
        return sleepyCache.getLeagueRosters(leagueId)
    }

    suspend fun getUsersForLeague(leagueId: Long): List<SleeperUser> {
        return getRostersForLeague(leagueId).pmapSuspend {
            getUserById(it.ownerId!!)
        }
    }

    suspend fun getUserById(ownerId: Long): SleeperUser {
        return sleepyCache.getUser(ownerId)
    }

    suspend fun getBracket(leagueId: Long, bracketType: BracketType): Bracket {
        return sleepyCache.getPlayoffBracket(leagueId, bracketType)
    }

    suspend fun getSleeperPlayer(playerId: String, sport: String): SleeperPlayer {
        return sleepyCache.getSleeperPlayer(playerId, sport)
    }

    data class PlayerUsage(val player: SleeperPlayer, val count: Int)
    suspend fun getMostUsedPlayers(userName: String, sport: String, seasons: List<String>): List<PlayerUsage> {
        val user = getUser(userName)
        val leagues = getAllLeaguesForUser(user, listOf(sport), seasons)

        val allRosters = leagues.map { season ->
            season.leagues.map { league ->
                getRostersForLeague(league.leagueId)
            }.flatten()
        }.flatten()

        return allRosters
            .mapNotNull { it.players }
            .flatten()
            .groupBy { it }
            .map {
                PlayerUsage(getSleeperPlayer(it.key, sport), it.value.size)
            }
            .sortedByDescending { it.count }
    }

    data class PlayerResults(val userName: String, val totalLeagues: Int, val winningLeagues: Int, val winningPercentage: Float, val seasonResults: List<PlayerSeasonResults> = listOf(), val winningRosters: List<LeagueRoster> = listOf())
    data class PlayerSeasonResults(val season: String, val sport: String, val totalLeagues: Int, val winningLeagues: Int, val winningPercentage: Float)
    suspend fun getMyWinningLeagues(userName: String, sports: List<String>, seasons: List<String>): PlayerResults {
        val fullUser = getUser(userName)

        val allLeagues = getAllLeaguesForUser(fullUser, sports, seasons)
        val leagueIds = allLeagues.map { it.leagues.map { league -> league.leagueId } }.flatten()

        val deferredRosters = CoroutineScope(Dispatchers.IO).async {
            leagueIds.pmapSuspend { it to getRostersForLeague(it) }
        }
        val deferredBrackets: Deferred<List<Pair<Long, Bracket>>> = CoroutineScope(Dispatchers.IO).async {
            leagueIds.pmapSuspend { it to getBracket(it, BracketType.WINNER) }
        }

        val rosters = deferredRosters.await().toMap()
        val brackets = deferredBrackets.await().toMap()

        val result = mutableListOf<LeagueRoster>()
        allLeagues.forEach { (sport, season, leagues) ->
            leagues.forEach leagues@{ league ->
                val winner = BracketInspector.getWinner(brackets[league.leagueId]!!) ?: return@leagues
                val winningRoster = rosters[league.leagueId]!!.find { it.rosterId == winner }!!
                if (winningRoster.ownerId == fullUser.userId) {
                    val winningStarters = winningRoster.starters
                    val winningPlayers = winningRoster.players
                    val finalRoster = winningRoster.copy(
                        fullStarters = winningStarters.map { playerId ->
                            getSleeperPlayer(playerId, sport)
                        },
                        fullPlayers = winningPlayers.map { playerId ->
                            getSleeperPlayer(playerId, sport)
                        }
                    )
                    result.add(LeagueRoster(league, finalRoster))
                }
            }
        }

        val seasonResults = allLeagues.map {(sport, season, leagues) ->
            val totalLeagues = leagues.size
            val winningLeagues = result.filter { it.league.season == season && it.league.sport == sport }.size
            val winningPercentage = if (winningLeagues > 0 && totalLeagues > 0) {
                winningLeagues.toFloat().div(totalLeagues)
            } else {
                0F
            }
            PlayerSeasonResults(season, sport, totalLeagues, winningLeagues, winningPercentage)
        }

        return PlayerResults(
            userName = userName,
            totalLeagues = rosters.size,
            winningLeagues = result.size,
            winningPercentage = (result.size.toFloat() / rosters.size),
            seasonResults = seasonResults,
            winningRosters = result
        )
    }

    private suspend fun getAllLeaguesForUser(user: SleeperUser, sports: List<String>, seasons: List<String>): List<LeagueSeason> {
        val leagueSeasons = sports.map { sport ->
            seasons.map { season ->
                LeagueSeason(sport, season, listOf())
            }
        }.flatten()

        return leagueSeasons.pmapSuspend { (sport, season, _) ->
            logger.info("Getting info for sport $sport season $season")
            val leagues = getLeaguesForSeason(user.userId, sport, season)
            LeagueSeason(sport, season, leagues)
        }
    }
}
