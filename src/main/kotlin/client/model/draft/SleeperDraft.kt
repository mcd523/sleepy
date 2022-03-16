package client.model.draft

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleeperDraft(
    @JsonProperty("type")
    val type: String,
    @JsonProperty("status")
    val status: String,
    @JsonProperty("start_time")
    val startTime: Long,
    @JsonProperty("sport")
    val sport: String,
    @JsonProperty("settings")
    val settings: Any?,
    @JsonProperty("season_type")
    val seasonType: String,
    @JsonProperty("season")
    val season: String,
    @JsonProperty("metadata")
    val metadata: Any?,
    @JsonProperty("league_id")
    val leagueId: String,
    @JsonProperty("last_picked")
    val last_picked: Long,
    @JsonProperty("last_message_time")
    val lastMessageTime: Long,
    @JsonProperty("last_message_id")
    val lastMessageId: String,
    @JsonProperty("draft_order")
    val draftOrder: Any?,
    @JsonProperty("draft_id")
    val draftId: String,
    @JsonProperty("creators")
    val creators: Any?,
    @JsonProperty("created")
    val created: Long,
)
