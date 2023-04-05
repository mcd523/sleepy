package util

import com.github.michaelbull.retry.ContinueRetrying
import com.github.michaelbull.retry.StopRetrying
import com.github.michaelbull.retry.policy.RetryPolicy
import com.github.michaelbull.retry.policy.constantDelay
import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.policy.plus
import java.time.Duration

/**
 * Kotlin-Coroutine based retries
 * This is an encapsulation of [com.github.michaelbull.retry.retry]
 *
 * @param handleExceptions - retry blocks will retry on these exceptions only.  On an eventual failure, they will throw the last exception.
 * @param maxRetries - retry blocks will retry this many additional times, excluding the initial invocation.  E.g. maxReties = 1 will run the block at most 2 times.
 * @param delay - retry blocks will add this delay between retries
 */
class SuspendableRetry(
    handleExceptions: List<Class<out Throwable>>,
    maxRetries: Int? = null,
    delay: Duration? = null
) {
    private val retryPolicy: RetryPolicy<Throwable>
    private val exceptions: List<Class<out Throwable>>

    init {
        var policy = retryOnException(handleExceptions)

        if (maxRetries != null) {
            assert(maxRetries > 0) { "If max retries are included it must be greater than 0" }
            policy += limitAttempts(1 + maxRetries)
        }

        if (delay != null) {
            policy += constantDelay(delay.toMillis())
        }

        this.retryPolicy = policy
        this.exceptions = handleExceptions
    }

    suspend fun <T> retry(
        block: suspend () -> T
    ): T {
        return com.github.michaelbull.retry.retry(policy = retryPolicy, block = block)
    }

    suspend fun <T> retry(
        init: suspend () -> Unit,
        block: suspend () -> T
    ): T {
        return com.github.michaelbull.retry.retry(policy = retryWithInit(init) + retryPolicy, block = block)
    }

    private suspend fun retryWithInit(initBlock: suspend () -> Unit): RetryPolicy<Throwable> = {
        if (exceptions.any { thing -> thing.isAssignableFrom(reason::class.java) }) {
            initBlock()
            ContinueRetrying
        } else {
            StopRetrying
        }
    }

    companion object {
        fun retryOnException(classes: List<Class<*>>): RetryPolicy<Throwable> = {
            if (classes.any { it.isAssignableFrom(reason::class.java) }) ContinueRetrying else StopRetrying
        }
    }
}
