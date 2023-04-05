package redis

import SleepyConfiguration
import client.SleeperClient
import client.model.user.SleeperUser
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.github.crackthecodeabhi.kreds.connection.newClient
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
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
                runBlocking { it.auth(redisConfig.password!!) }
            }
        }
    }

    private val redis = buildRedisClient(configuration.redis)

    suspend fun getUser(userName: String): SleeperUser {
        val key = "User:$userName"
        return redis.get(key)
            ?.let {
                parseValue(it)
            } ?: run {
                val httpUser = sleeper.getUser(userName)
                redis.set(key, stringify(httpUser))
                httpUser
            }
    }

    private inline fun <reified T> parseValue(stringValue: String): T? {
        return try {
            mapper.readValue(stringValue, T::class.java)
        } catch (e: Exception) {
            logger.error("Failed to parse string value as ${T::class.java}", e)
            null
        }
    }

    private fun stringify(value: Any): String {
        return mapper.writeValueAsString(value)
    }

}