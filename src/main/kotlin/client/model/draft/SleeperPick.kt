package client.model.draft

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleeperPick(
    @JsonProperty("player_id")
    val playerId: String,
    @JsonProperty("picked_by")
    val pickedBy: String,
    @JsonProperty("roster_id")
    val rosterId: String,
    @JsonProperty("round")
    val round: Long,
    @JsonProperty("draft_slot")
    val draftSlot: Long,
    @JsonProperty("pick_no")
    val pickNumber: Long,
    @JsonProperty("metadata")
    val metadata: Any?,
    @JsonProperty("is_keeper")
    val isKeeper: Any?,
    @JsonProperty("draft_id")
    val draftId: String
)
