package services

import client.SleeperClient
import client.model.BracketType
import client.model.PlayoffMatchup
import client.model.SleeperLeague
import client.model.SleeperUser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import org.jvnet.hk2.annotations.Service
import org.slf4j.LoggerFactory
import pmapSuspend
import javax.inject.Inject

@Service
class SleepyService @Inject constructor(val mapper: ObjectMapper, val sleeper: SleeperClient) {
    companion object {
        private val logger = LoggerFactory.getLogger(SleepyService::class.java)
    }

    fun getUser(userName: String): SleeperUser {
        return sleeper.getUser(userName)
    }

    fun getLeaguesForSeason(userId: Long, sport: String, season: String): List<SleeperLeague> {
        return sleeper.getLeaguesForSeason(userId, sport, season).toList()
    }

    fun getBracket(leagueId: Long, bracketType: BracketType): List<PlayoffMatchup> {
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

        val result = deferred.await()
        return mapper.convertValue(result, JsonNode::class.java)


//        val allLeagues = seasons.associateWith { season ->
//            sports.associateWith { sport ->
//                getLeaguesForSeason(fullUser.userId, sport, season)
//            }
//        }
//
//        return mapper.convertValue(allLeagues, JsonNode::class.java)
    }

}
