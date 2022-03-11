package client

import MaxDropwizardConfiguration
import client.model.SleeperLeague
import client.model.SleeperUser
import com.fasterxml.jackson.databind.ObjectMapper
import net.jodah.failsafe.Failsafe
import net.jodah.failsafe.RetryPolicy
import org.glassfish.jersey.logging.LoggingFeature
import java.time.Duration
import java.util.function.Predicate
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import javax.ws.rs.HttpMethod
import javax.ws.rs.ProcessingException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.ResponseProcessingException
import javax.ws.rs.core.MediaType

class SleeperClient @Inject constructor(config: MaxDropwizardConfiguration, client: Client, private val mapper: ObjectMapper) {
    companion object {
        private val logger: Logger = Logger.getLogger(this::class.simpleName)
    }

    private val retryPolicy: RetryPolicy<Any> = RetryPolicy<Any>()
        .withDelay(Duration.ofMillis(100))
        .handleIf(Predicate {
            when (it) {
                is ProcessingException -> it !is ResponseProcessingException
                is WebApplicationException -> {
                    it.response.status > 500
                }
                else -> false
            }
        })
        .onRetry {
            logger.info("Retry attempt: ${it.attemptCount}. Retrying execution for ${it.lastFailure}")
        }

    private val webTarget = client
        .target(config.baseUrl)
        .register(LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.HEADERS_ONLY, 8192))


    fun getUser(userId: Long): SleeperUser = getUser(userId.toString())
    fun getUser(userName: String): SleeperUser {
        val path = "/user/$userName"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request)
    }

    fun getLeaguesForSeason(userId: Long, sport: String, season: String): List<SleeperLeague> {
        val path = "/user/$userId/leagues/$sport/$season"
        val request = buildRequest(path, HttpMethod.GET)

        return execute(request)
    }

    private fun buildRequest(
        path: String,
        method: String,
        params: Map<String, List<String>> = mapOf(),
        entity: Entity<*>? = null,
        vararg mediaTypes: MediaType = arrayOf(MediaType.APPLICATION_JSON_TYPE)
    ): Invocation {
        return webTarget
            .path(path)
            .also { params.forEach { (t, u) -> it.queryParam(t, u) } }
            .request()
            .accept(*mediaTypes)
            .build(method, entity)
    }

    private inline fun <reified T> execute(request: Invocation): T {
        return Failsafe.with(retryPolicy).get { _ ->
            request.invoke(T::class.java)
        }
    }
}
