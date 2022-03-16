package services

import client.SleeperClient
import client.model.league.bracket.BracketType
import client.model.league.SleeperLeague
import client.model.league.SleeperRoster
import client.model.user.SleeperUser
import client.model.league.bracket.Bracket
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import org.jvnet.hk2.annotations.Service
import org.slf4j.LoggerFactory
import pmapSuspend
import javax.inject.Inject

@Service
class SleepyService @Inject constructor(private val mapper: ObjectMapper, private val sleeper: SleeperClient) {
    companion object {
        private val logger = LoggerFactory.getLogger(SleepyService::class.java)
    }

    fun getUser(userName: String): SleeperUser {
        return sleeper.getUser(userName)
    }

    fun getLeaguesForSeason(userId: Long, sport: String, season: String): List<SleeperLeague> {
        return sleeper.getLeaguesForSeason(userId, sport, season).toList()
    }

    fun getRostersForLeague(leagueId: Long): List<SleeperRoster> {
        return sleeper.getRostersForLeague(leagueId).toList()
    }

    fun getBracket(leagueId: Long, bracketType: BracketType): Bracket {
        return sleeper.getPlayoffBracket(leagueId, bracketType).toList()
    }

    data class LeagueSeason(val sport: String, val season: String, val leagues: List<SleeperLeague>)
    suspend fun getMyWinningLeagues(userName: String, sports: List<String>, seasons: List<String>): JsonNode {
        val fullUser = getUser(userName)
        val leagueSeasons = sports.map { sport ->
            seasons.map { season ->
                LeagueSeason(sport, season, listOf())
            }
        }.flatten()

        val deferred = CoroutineScope(Dispatchers.IO).async {
            leagueSeasons.pmapSuspend { (sport, season, _) ->
                logger.info("Getting info for sport $sport season $season")
                val leagues = getLeaguesForSeason(fullUser.userId, sport, season)
                LeagueSeason(sport, season, leagues)
            }
        }

        val allLeagues = deferred.await()
        val leagueIds = allLeagues.map { it.leagues.map { league -> league.leagueId } }.flatten()

        val deferredRosters = CoroutineScope(Dispatchers.IO).async {
            leagueIds.pmapSuspend { it to getRostersForLeague(it) }
        }
        val deferredBrackets = CoroutineScope(Dispatchers.IO).async {
            leagueIds.pmapSuspend { it to getBracket(it, BracketType.WINNER) }
        }

        val rosters = deferredRosters.await().toMap()
        val brackets = deferredBrackets.await().toMap()

        val result = mutableListOf<Pair<SleeperLeague, SleeperRoster>>()
        allLeagues.forEach { (sport, season, leagues) ->
            leagues.forEach {
                val winner = BracketInspector.getWinner(brackets[it.leagueId]!!)
                val winningRoster = rosters[it.leagueId]!!.find { it.rosterId == winner }!!
                if (winningRoster.ownerId == fullUser.userId.toString()) {
                    result.add(it to winningRoster)
                }
            }
        }

        return mapper.convertValue(result, JsonNode::class.java)
    }

}
