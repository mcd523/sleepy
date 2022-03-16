package client.model.league

import client.model.draft.SleeperPick
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SleeperTransaction(
    @JsonProperty("type")
    val type: String,
    @JsonProperty("transaction_id")
    val transactionId: String,
    @JsonProperty("status_updated")
    val updatedTime: Long,
    @JsonProperty("status")
    val status: String,
    @JsonProperty("roster_ids")
    val rosterIds: List<Int>,
    @JsonProperty("metadata")
    val metadata: Any?,
    @JsonProperty("leg")
    val week: Int,
    @JsonProperty("drops")
    val drops: Any?,
    @JsonProperty("draft_picks")
    val draftPicks: List<SleeperPick>,
    @JsonProperty("creator")
    val creator: String,
    @JsonProperty("created")
    val createdTime: Long,
    @JsonProperty("consenter_ids")
    val consenterIds: List<Int>,
    @JsonProperty("adds")
    val adds: Any?,
    @JsonProperty("waiver_budget")
    val waiverBudget: List<FAABTransaction>
)
