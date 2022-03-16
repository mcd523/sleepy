package client.model.player

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleeperPlayer(
    @JsonProperty("hashtag")
    val hashtag: String,
    @JsonProperty("depth_chart_position")
    val depthChartPosition: Long,
    @JsonProperty("status")
    val status: String,
    @JsonProperty("sport")
    val sport: String,
    @JsonProperty("fantasy_positions")
    val positions: List<String>,
    @JsonProperty("number")
    val number: Long,
    @JsonProperty("search_last_name")
    val searchLastName: String,
    @JsonProperty("injury_start_date")
    val injuryStartDate: String?,
    @JsonProperty("weight")
    val weight: String,
    @JsonProperty("position")
    val position: String,
    @JsonProperty("practice_participation")
    val practiceParticipation: Any?,
    @JsonProperty("sportradar_id")
    val sportRadarId: String,
    @JsonProperty("team")
    val team: String,
    @JsonProperty("last_name")
    val lastName: String,
    @JsonProperty("college")
    val college: String,
    @JsonProperty("fantasy_data_id")
    val fantasyDataId: Long,
    @JsonProperty("injury_status")
    val injuryStatus: Any?,
    @JsonProperty("player_id")
    val playerId: String,
    @JsonProperty("height")
    val height: String,
    @JsonProperty("search_full_name")
    val searchFullName: String,
    @JsonProperty("age")
    val age: Long,
    @JsonProperty("stats_id")
    val statsId: String,
    @JsonProperty("birth_country")
    val birthCountry: String,
    @JsonProperty("espn_id")
    val espnId: String,
    @JsonProperty("search_rank")
    val searchRank: Long,
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("depth_chart_order")
    val depthChartOrder: Long,
    @JsonProperty("rotowire_id")
    val rotowireId: Long?,
    @JsonProperty("rotoworld_id")
    val rotoworldId: Long?,
    @JsonProperty("search_first_name")
    val searchFirstName: String,
    @JsonProperty("yahoo_id")
    val yahooId: Long?,
)
