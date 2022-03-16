package client.model.league

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleeperTradedPick(
    @JsonProperty("season")
    val season: String,
    @JsonProperty("round")
    val round: Long,
    @JsonProperty("roster_id")
    val rosterId: Long,
    @JsonProperty("previous_owner_id")
    val previousOwnerId: Long,
    @JsonProperty("owner_id")
    val ownerId: Long
)
