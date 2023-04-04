import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.core.Configuration
import org.jvnet.hk2.annotations.Service

@Service
class SleepyConfiguration: Configuration() {
    @JsonProperty
    var foo: String? = null

    @JsonProperty
    var baseUrl: String? = null

    data class RedisConfiguration(val host: String? = null, val port: Int? = null, val password: String? = null)

    @JsonProperty
    var redis: RedisConfiguration = RedisConfiguration()
}
