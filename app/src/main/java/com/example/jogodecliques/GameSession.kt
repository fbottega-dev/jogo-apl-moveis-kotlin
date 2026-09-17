package com.example.jogodecliques

enum class GamePhase { READY, PLAYING, WON, ABANDONED }

/** Immutable rules, independent of Android. A round ends exactly at its target. */
data class GameSession(val target: Int, val clicks: Int = 0, val phase: GamePhase = GamePhase.READY) {
    init {
        require(target in 1..50) { "Target must be between 1 and 50" }
        require(clicks in 0..target) { "Clicks must fit the round" }
        require(phase != GamePhase.WON || clicks == target)
    }
    val progress: Float get() = clicks.toFloat() / target
    fun start(): GameSession = if (phase == GamePhase.READY) copy(phase = GamePhase.PLAYING) else this
    fun click(): GameSession {
        if (phase != GamePhase.PLAYING) return this
        val next = clicks + 1
        return copy(clicks = next, phase = if (next == target) GamePhase.WON else GamePhase.PLAYING)
    }
    fun abandon(): GameSession = if (phase == GamePhase.PLAYING) copy(phase = GamePhase.ABANDONED) else this
}
