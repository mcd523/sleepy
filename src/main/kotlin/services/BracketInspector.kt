package services

import client.model.league.bracket.Bracket

object BracketInspector {
    // Return teamId of winning team from bracket
    fun getWinner(bracket: Bracket): Int {
        if (!isFinished(bracket)) throw RuntimeException("Bracket is not finished yet")
        val rounds = bracket.groupBy { it.round }
        val finalRound = rounds.toList().maxByOrNull { it.first }!!.second

        val championship = finalRound.find {
            it.team1Origin?.winnerFromMatchup != null && it.team2Origin?.winnerFromMatchup != null
        } ?: throw RuntimeException("No championship detected")

        return championship.winnerRosterId ?: throw RuntimeException("Empty championship winnerRosterId")
    }

    private fun isFinished(bracket: Bracket): Boolean {
        return bracket.all { it.winnerRosterId != null && it.loserRosterId != null }
    }

}
