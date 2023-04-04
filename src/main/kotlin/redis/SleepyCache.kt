package redis

import SleepyConfiguration
import client.model.user.SleeperUser
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.github.crackthecodeabhi.kreds.connection.newClient
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking

class SleepyCache @Inject constructor(
    private val mapper: ObjectMapper,
    configuration: SleepyConfiguration
) {
    companion object {
        private fun buildRedisClient(redisConfig: SleepyConfiguration.RedisConfiguration): KredsClient {
            return newClient(Endpoint(redisConfig.host!!, redisConfig.port!!)).also {
                runBlocking { it.auth(redisConfig.password!!) }
            }
        }
    }

    private val redis = buildRedisClient(configuration.redis)

    suspend fun setFoo(): String? {
        return redis.set("foo", "bar")
    }

    suspend fun getFoo(): String? {
        return redis.get("foo")
    }

    suspend fun getUser(userName: String): SleeperUser? {
        return redis.get("User:$userName")?.let {
            mapper.readValue(it, SleeperUser::class.java)
        }
    }

    suspend fun setUser(userName: String, user: SleeperUser): String? {
        return redis.set("User:$userName", mapper.writeValueAsString(user))
    }

}