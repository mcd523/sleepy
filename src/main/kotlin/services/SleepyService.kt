package services

import client.SleeperClient
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
class SleepyService @Inject constructor(val mapper: ObjectMapper) {
    companion object {
        private val logger = LoggerFactory.getLogger(SleepyService::class.java)
    }

    @Inject
    private lateinit var sleeper: SleeperClient

    fun getUser(userName: String): SleeperUser {
        return sleeper.getUser(userName)
    }

    fun getLeaguesForSeason(userId: Long, sport: String, season: String): List<SleeperLeague> {
        return sleeper.getLeaguesForSeason(userId, sport, season)
    }

    suspend fun getMyWinningLeagues(userName: String, sports: List<String>, seasons: List<String>): JsonNode {
        val fullUser = getUser(userName)

        val deferred = CoroutineScope(Dispatchers.IO).async {
            seasons.pmapSuspend { season ->
                logger.info("Getting info for season $season")
                sports.pmapSuspend { sport ->
                    logger.info("Getting info for sport $sport in season $season")
                    getLeaguesForSeason(fullUser.userId, sport, season)
                }
            }
        }

        return mapper.convertValue(deferred.await(), JsonNode::class.java)


//        val allLeagues = seasons.associateWith { season ->
//            sports.associateWith { sport ->
//                getLeaguesForSeason(fullUser.userId, sport, season)
//            }
//        }
//
//        return mapper.convertValue(allLeagues, JsonNode::class.java)
    }

}
