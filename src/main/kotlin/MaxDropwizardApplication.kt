import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.module.kotlin.KotlinModule
import health.ApplicationHealthCheck
import hk2.ApplicationBinder
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.glassfish.jersey.client.ClientConfig
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.h2.H2DatabasePlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import resources.SleepyResource
import javax.ws.rs.client.ClientBuilder

class MaxDropwizardApplication: Application<MaxDropwizardConfiguration>() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            MaxDropwizardApplication().run(*args)
        }
    }

    override fun initialize(bootstrap: Bootstrap<MaxDropwizardConfiguration>) {

    }

    override fun run(configuration: MaxDropwizardConfiguration?, environment: Environment?) {
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
        environment.jersey().register(SleepyResource::class.java)
    }
}
