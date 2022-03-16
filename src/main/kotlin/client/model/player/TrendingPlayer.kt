package client.model.player

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TrendingPlayer(
    @JsonProperty("player_id")
    val playerId: String,
    @JsonProperty("count")
    val count: Long
)
