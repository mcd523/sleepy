package mongodb

import SleepyConfiguration
import client.model.player.SleeperPlayer
import com.mongodb.kotlin.client.coroutine.MongoClient
import jakarta.inject.Inject
import kotlinx.coroutines.flow.FlowCollector
import org.slf4j.LoggerFactory

class SleepyMongoClient @Inject constructor(
    configuration: SleepyConfiguration
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SleepyMongoClient::class.java)
    }

    private val mongo = MongoClient.create("mongodb+srv://${configuration.mongodb.user}:${configuration.mongodb.password}@${configuration.mongodb.host}?retryWrites=true&w=majority")

    suspend fun doThing(): List<String> {
        val dbList = mutableListOf<String>()
        val dbs = mongo.listDatabaseNames()
        dbs.collect {
            dbList.add(it)
        }
        logger.info(dbList.toString())

        return dbList
    }

    suspend fun addPlayer(player: SleeperPlayer) {
        val collection = mongo.getDatabase("sleeper-players")
            .getCollection<SleeperPlayer>("players")

        val result = collection.insertOne(player)
        result.insertedId
    }
}