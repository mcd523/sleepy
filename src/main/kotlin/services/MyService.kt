package services

import MaxDropwizardConfiguration
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.glassfish.jersey.client.JerseyClientBuilder
import org.glassfish.jersey.client.JerseyInvocation
import javax.inject.Inject
import javax.ws.rs.core.MediaType

class MyService @Inject constructor(val config: MaxDropwizardConfiguration, val mapper: ObjectMapper) {

    val client = JerseyClientBuilder
        .createClient()
        .target("https://api.sleeper.app/v1")


    fun getUser(userId: String): JsonNode {
        val request = client
            .path("/user/$userId")
            .request()
            .accept(MediaType.APPLICATION_JSON_TYPE)

        return callGet(request, JsonNode::class.java)
    }

    fun getLeaguesForSeason(user: String, sport: String, season: String): JsonNode {
        val request = client
            .path("/user/$user/leagues/$sport/$season")
            .request()
            .accept(MediaType.APPLICATION_JSON_TYPE)

        return callGet(request, JsonNode::class.java)
    }

    private fun <T> callGet(request: JerseyInvocation.Builder, clazz: Class<T>): T {
        val response = request.buildGet().invoke(clazz)
        println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response))
        return response
    }



}
