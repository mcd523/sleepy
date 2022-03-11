import com.fasterxml.jackson.module.kotlin.KotlinModule
import health.ApplicationHealthCheck
import hk2.ApplicationBinder
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
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

        // Configure object mapper
        environment.objectMapper.registerModule(KotlinModule.Builder().build())

        val sharedClient = ClientBuilder.newClient()

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
