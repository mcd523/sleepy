package client.model.league

import com.fasterxml.jackson.annotation.JsonProperty

data class SleeperRoster(
    @JsonProperty("starters")
    val starters: List<String>,
    @JsonProperty("roster_id")
    val rosterId: Int,
    @JsonProperty("players")
    val players: List<String>,
    @JsonProperty("owner_id")
    val ownerId: String?,
    @JsonProperty("league_id")
    val leagueId: String
)
