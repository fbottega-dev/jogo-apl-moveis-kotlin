package com.example.jogodecliques

import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class DifficultyTest {
    @Test fun levelsCoverAllTargetsWithoutOverlap() {
        val ranges = Difficulty.entries.flatMap { it.targets.toList() }
        assertEquals((1..50).toList(), ranges)
        assertEquals(10, Difficulty.EASY.targets.last)
        assertEquals(11, Difficulty.NORMAL.targets.first)
        assertEquals(30, Difficulty.NORMAL.targets.last)
        assertEquals(31, Difficulty.HARD.targets.first)
    }

    @Test fun generatedRoundsStayInsideSelectedDifficulty() {
        for (difficulty in Difficulty.entries) {
            val random = Random(42)
            val rounds = List(200) { difficulty.newSession(random) }
            assertTrue(rounds.all { it.target in difficulty.targets })
            assertTrue(rounds.map { it.target }.distinct().size > 1)
            assertTrue(rounds.all { it.clicks == 0 && it.phase == GamePhase.READY })
        }
    }

    @Test fun startingAnotherLevelDoesNotChangePreviousRound() {
        val playing = Difficulty.HARD.newSession(Random(1)).start().click()
        val next = Difficulty.EASY.newSession(Random(2))
        assertEquals(GamePhase.PLAYING, playing.phase)
        assertEquals(1, playing.clicks)
        assertTrue(playing.target in 31..50)
        assertTrue(next.target in 1..10)
        assertEquals(GamePhase.READY, next.phase)
    }
}
