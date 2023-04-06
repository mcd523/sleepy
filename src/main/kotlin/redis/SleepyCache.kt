package redis

import SleepyConfiguration
import client.SleeperClient
import client.model.league.SleeperLeague
import client.model.league.SleeperRoster
import client.model.league.bracket.Bracket
import client.model.league.bracket.BracketType
import client.model.player.SleeperPlayer
import client.model.user.SleeperUser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.github.crackthecodeabhi.kreds.connection.newClient
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import pmapSuspend
import org.slf4j.LoggerFactory

class SleepyCache @Inject constructor(
    private val sleeper: SleeperClient,
    private val mapper: ObjectMapper,
    configuration: SleepyConfiguration
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SleepyCache::class.java)
        private fun buildRedisClient(redisConfig: SleepyConfiguration.RedisConfiguration): KredsClient {
            return newClient(Endpoint(redisConfig.host!!, redisConfig.port!!)).also {
                //runBlocking { it.auth(redisConfig.password!!) }
            }
        }
    }

    private val redis = buildRedisClient(configuration.redis)

    suspend fun getUser(ownerId: Long) = getUser(ownerId.toString())
    suspend fun getUser(userName: String): SleeperUser {
        val key = "User:$userName"
        return redis.get(key)
            ?.let {
                parseValue(it)
            } ?: run {
                logger.info("Grabbing user [$userName] from Sleeper API")
                val httpUser = sleeper.getUser(userName)
                redis.set(key, stringify(httpUser))
                httpUser
            }
    }

    suspend fun getLeague(leagueId: Long): SleeperLeague {
        val key = "League:$leagueId"
        return redis.get(key)
            ?.let {
                parseValue(it)
            } ?: run {
                logger.info("Grabbing league [$leagueId] from Sleeper API")
                val httpLeague = sleeper.getLeagueById(leagueId)
                redis.set(key, stringify(httpLeague))
                httpLeague
            }
    }

    suspend fun getLeagueRosters(leagueId: Long): List<SleeperRoster> {
        val key = "League:$leagueId:Rosters"
        return redis.get(key)
            ?.let {
                parseValue(it)
            } ?: run {
            logger.info("Grabbing rosters for league [$leagueId] from Sleeper API")
            val httpRosters = sleeper.getRostersForLeague(leagueId)
            redis.set(key, stringify(httpRosters))
            httpRosters
        }
    }

    suspend fun getPlayoffBracket(leagueId: Long, bracketType: BracketType): Bracket {
        val key = "League:$leagueId:Bracket:$bracketType"
        return redis.get(key)
            ?.let {
                parseValue(it)
            } ?: run {
            logger.info("Grabbing bracket [$bracketType] for league [$leagueId] from Sleeper API")
            val httpBracket = sleeper.getPlayoffBracket(leagueId, bracketType)
            redis.set(key, stringify(httpBracket))
            httpBracket
        }
    }

    suspend fun getSleeperPlayer(playerId: String, sport: String): SleeperPlayer {
        fun buildKey(playerId: String, sport: String): String = "Player:$sport:$playerId"
        val key = buildKey(playerId, sport)
        return redis.get(key)
            ?.let {
                parseValue(it)
            } ?: run {
                logger.info("Grabbing all players for sport [$sport] from Sleeper API")
                sleeper.getAllPlayers(sport).toList()
                    .pmapSuspend { (id, player) ->
                        redis.set(buildKey(id, sport), stringify(player))
                    }

            parseValue(redis.get(key)!!)!!
        }
    }

    private inline fun <reified T> parseValue(stringValue: String): T? {
        return try {
            mapper.readValue(stringValue, object: TypeReference<T>() {})
        } catch (e: Exception) {
            logger.error("Failed to parse string value as ${T::class.java}", e)
            null
        }
    }

    private fun stringify(value: Any): String {
        return mapper.writeValueAsString(value)
    }

}