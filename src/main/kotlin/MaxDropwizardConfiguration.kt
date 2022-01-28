import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import org.jvnet.hk2.annotations.Service

@Service
class MaxDropwizardConfiguration: Configuration() {
    @JsonProperty
    var foo: String? = null

    @JsonProperty
    var baseUrl: String? = null
}
