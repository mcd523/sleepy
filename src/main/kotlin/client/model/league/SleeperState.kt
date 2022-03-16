package client.model.league

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleeperState(
    @JsonProperty("week")
    val week: Long,
    @JsonProperty("season_type")
    val seasonType: String,
    @JsonProperty("season_start_date")
    val seasonStartDate: String,
    @JsonProperty("season")
    val season: String,
    @JsonProperty("previous_season")
    val previousSeason: String,
    @JsonProperty("leg")
    val leg: Long,
    @JsonProperty("league_season")
    val leagueSeason: String,
    @JsonProperty("league_create_season")
    val leagueCreateSeason: String,
    @JsonProperty("display_week")
    val displayWeek: Long
)
