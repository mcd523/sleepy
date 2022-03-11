package hk2

import client.SleeperClient
import org.glassfish.hk2.utilities.binding.AbstractBinder
import services.SleepyService
import javax.inject.Singleton
import javax.ws.rs.client.Client

class ApplicationBinder(private val client: Client): AbstractBinder() {

    override fun configure() {
        bind(client).to(Client::class.java)
        bindAsContract(SleepyService::class.java).`in`(Singleton::class.java)
        bindAsContract(SleeperClient::class.java).`in`(Singleton::class.java)
    }

}
