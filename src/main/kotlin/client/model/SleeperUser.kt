package client.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleeperUser(
    @JsonProperty("username")
    val userName: String,
    @JsonProperty("user_id")
    val userId: Long,
    @JsonProperty("display_name")
    val displayName: String
)
