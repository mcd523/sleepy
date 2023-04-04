package hk2

import client.SleeperClient
import jakarta.inject.Singleton
import jakarta.ws.rs.client.Client
import org.glassfish.hk2.utilities.binding.AbstractBinder
import redis.SleepyCache
import services.SleepyService

class ApplicationBinder(private val client: Client): AbstractBinder() {

    override fun configure() {
        bind(client).to(Client::class.java)

        bindAsContract(SleepyService::class.java).`in`(Singleton::class.java)
        bindAsContract(SleepyCache::class.java).`in`(Singleton::class.java)

        bindAsContract(SleeperClient::class.java).`in`(Singleton::class.java)
    }

}
