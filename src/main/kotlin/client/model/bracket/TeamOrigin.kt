package client.model.bracket

import com.fasterxml.jackson.annotation.JsonProperty

data class TeamOrigin(
    @JsonProperty("w")
    val winnerFromMatchup: Int?,
    @JsonProperty("l")
    val loserFromMatchup: Int?
)
