package client.model.league

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleeperMatchup(
    @JsonProperty("starters")
    val starters: List<String>,
    @JsonProperty("roster_id")
    val rosterId: Long,
    @JsonProperty("players")
    val players: List<String>,
    @JsonProperty("matchup_id")
    val matchupId: Long,
    @JsonProperty("points")
    val points: Float,
    @JsonProperty("custom_points")
    val customPoints: Float?
)
