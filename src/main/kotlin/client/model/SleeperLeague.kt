package client.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleeperLeague(
    @JsonProperty("total_rosters")
    val leagueSize: Int,
    @JsonProperty("status")
    val status: String,
    @JsonProperty("sport")
    val sport: String,
    @JsonProperty("season")
    val season: String,
    @JsonProperty("name")
    val leagueName: String,
    @JsonProperty("league_id")
    val leagueId: Long,
    @JsonProperty("bracket_id")
    val bracketId: Long
)
