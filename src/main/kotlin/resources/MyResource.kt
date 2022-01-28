package resources

import services.MyService
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
class MyResource @Inject constructor(private val myService: MyService) {

    @GET
    fun getMe(): Response {
        return Response.status(200).entity("hello world").build()
    }

    @GET
    @Path("/{name}")
    fun getUser(@PathParam("name") name: String): Response {
        val user = myService.getUser(name)
        return Response.status(200).entity(user).build()
    }

    @GET
    @Path("/{name}/leagues")
    fun getLeagues(
        @PathParam("name") name: String,
        @QueryParam("sport") @DefaultValue("nfl") sport: String,
        @QueryParam("season") @DefaultValue("2021") season: String
    ): Response {
        val leagues = myService.getLeaguesForSeason(name, sport, season)
        return Response.status(200).entity(leagues).build()
    }
}
