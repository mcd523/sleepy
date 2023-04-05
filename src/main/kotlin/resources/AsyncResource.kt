package resources

import com.google.common.util.concurrent.ThreadFactoryBuilder
import jakarta.ws.rs.container.AsyncResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class AsyncResource {
    companion object {
        private val logger = LoggerFactory.getLogger(AsyncResource::class.java)
        private val threadFactory = ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("async-resource-thread-%d")
            .build()
        private val dispatcher = Executors.newCachedThreadPool(threadFactory).asCoroutineDispatcher()
    }

    protected val workerScope = CoroutineScope(dispatcher + SupervisorJob())

    fun <T> CoroutineScope.respondAsync(response: AsyncResponse, timeout: Long = 30, block: suspend () -> T) {
        launch {
            try {
                logger.info("Handling request")
                val output = block()
                response.resume(output)
            } catch (e: Exception) {
                logger.error("Exception in block", e)
                response.resume(e)
            }
        }.also { job ->
            response.setTimeout(timeout, TimeUnit.SECONDS)
            response.setTimeoutHandler { job.cancel() }
        }
    }
}
