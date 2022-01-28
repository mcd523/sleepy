package hk2

import MaxDropwizardConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import org.glassfish.hk2.utilities.binding.AbstractBinder
import services.MyService
import javax.inject.Singleton

class ApplicationBinder: AbstractBinder() {

    override fun configure() {
        bindAsContract(MyService::class.java).`in`(Singleton::class.java)
        bindAsContract(ObjectMapper::class.java).`in`(Singleton::class.java)
        bindAsContract(MaxDropwizardConfiguration::class.java).`in`(Singleton::class.java)
    }

}
