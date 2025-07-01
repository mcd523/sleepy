package client.model.league

import client.model.player.SleeperPlayer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleeperRoster(
    @JsonProperty("starters")
    val starters: List<String>,
    @JsonProperty("roster_id")
    val rosterId: Int,
    @JsonProperty("players")
    val players: List<String> = listOf(),
    @JsonProperty("owner_id")
    val ownerId: Long?,
    @JsonProperty("league_id")
    val leagueId: Long,

    val fullStarters: List<SleeperPlayer> = listOf(),
    val fullPlayers: List<SleeperPlayer> = listOf()
)
