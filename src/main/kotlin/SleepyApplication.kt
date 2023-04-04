import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider
import com.fasterxml.jackson.module.kotlin.KotlinModule
import health.ApplicationHealthCheck
import hk2.ApplicationBinder
import io.dropwizard.core.Application
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import jakarta.ws.rs.client.ClientBuilder
import org.glassfish.jersey.client.ClientConfig
import resources.SleepyLeagueResource
import resources.SleepyUserResource

class SleepyApplication: Application<SleepyConfiguration>() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SleepyApplication().run(*args)
        }
    }

    override fun initialize(bootstrap: Bootstrap<SleepyConfiguration>) {

    }

    override fun run(configuration: SleepyConfiguration?, environment: Environment?) {
        if (environment == null) return

        // Configure db
//        val jdbi = Jdbi.create("jdbc:h2:mem:test", "sa", "")
//        jdbi.installPlugin(KotlinPlugin())
//        jdbi.installPlugin(KotlinSqlObjectPlugin())
//        jdbi.installPlugin(H2DatabasePlugin())
//        val handle = jdbi.open()

        // Configure object mapper
        environment.objectMapper.registerModule(KotlinModule.Builder().build())

        val clientConfig = ClientConfig().register(JacksonJsonProvider(environment.objectMapper))
        val sharedClient = ClientBuilder.newClient(clientConfig)

        // Register binders
        environment.jersey().register(configuration)
        environment.jersey().register(environment.objectMapper)
        environment.jersey().register(ApplicationBinder(sharedClient))

        // Register health checks
        environment.healthChecks().register("Application", ApplicationHealthCheck())

        // Register resource classes
        environment.jersey().register(SleepyUserResource::class.java)
        environment.jersey().register(SleepyLeagueResource::class.java)
    }
}
