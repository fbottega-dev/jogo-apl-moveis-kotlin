package com.example.jogodecliques

import org.junit.Assert.*
import org.junit.Test

class GameSessionTest {
    @Test fun winsExactlyAtTarget() {
        val game = GameSession(2).start().click()
        assertEquals(GamePhase.PLAYING, game.phase)
        assertEquals(0.5f, game.progress, 0.001f)
        assertEquals(GamePhase.WON, game.click().phase)
        assertEquals(2, game.click().click().clicks)
    }
    @Test fun ignoresClicksBeforeStartAndAfterAbandoning() {
        val ready = GameSession(10)
        assertEquals(ready, ready.click())
        val abandoned = ready.start().click().abandon()
        assertEquals(abandoned, abandoned.click())
        assertEquals(GamePhase.ABANDONED, abandoned.phase)
    }
    @Test fun oneClickRoundAndNewRoundAreIndependent() {
        assertEquals(GamePhase.WON, GameSession(1).start().click().phase)
        assertEquals(0, GameSession(50).clicks)
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsInvalidTarget() { GameSession(0) }
    @Test(expected = IllegalArgumentException::class) fun rejectsClicksAboveTarget() { GameSession(4, 5) }
    @Test(expected = IllegalArgumentException::class) fun rejectsPrematureWin() { GameSession(4, 1, GamePhase.WON) }
    @Test fun winningCannotBeAbandonedOrStartedAgain() {
        val won = GameSession(1).start().click()
        assertEquals(won, won.abandon())
        assertEquals(won, won.start())
    }
}
