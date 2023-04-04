package client.model.league.bracket

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlayoffMatchup(
    @JsonProperty("r")
    val round: Int,
    @JsonProperty("m")
    val matchupId: Int,
    @JsonProperty("t1")
    val team1: Int,
    @JsonProperty("t2")
    val team2: Int,
    @JsonProperty("w")
    val winnerRosterId: Int?,
    @JsonProperty("l")
    val loserRosterId: Int?,
    @JsonProperty("t1_from")
    val team1Origin: TeamOrigin?,
    @JsonProperty("t2_from")
    val team2Origin: TeamOrigin?
)
