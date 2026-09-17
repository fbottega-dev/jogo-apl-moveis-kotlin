package com.example.jogodecliques

import kotlin.random.Random

enum class Difficulty(val targets: IntRange) {
    EASY(1..10),
    NORMAL(11..30),
    HARD(31..50);

    fun newSession(random: Random = Random.Default): GameSession =
        GameSession(random.nextInt(targets.first, targets.last + 1))
}
