import com.fasterxml.jackson.module.kotlin.KotlinModule
import hk2.ApplicationBinder
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import resources.MyResource

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
        environment.objectMapper
            .registerModule(KotlinModule.Builder().build())


        environment.jersey().register(ApplicationBinder::class.java)

        // Register resource classes
        environment.jersey().register(MyResource::class.java)
    }


}
