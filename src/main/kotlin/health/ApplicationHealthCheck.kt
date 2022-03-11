package health

import com.codahale.metrics.health.HealthCheck

class ApplicationHealthCheck: HealthCheck() {
    override fun check(): Result {
        return Result.healthy()
    }
}
