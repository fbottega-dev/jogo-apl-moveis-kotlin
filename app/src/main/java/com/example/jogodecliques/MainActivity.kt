package com.example.jogodecliques

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jogodecliques.ui.theme.JogoDeCliquesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { JogoDeCliquesTheme { Surface(Modifier.fillMaxSize()) { GameScreen() } } }
    }
}

private val SessionSaver = listSaver<GameSession, Int>(
    save = { listOf(it.target, it.clicks, it.phase.ordinal) },
    restore = { GameSession(it[0], it[1], GamePhase.entries[it[2]]) }
)

@Composable
fun GameScreen() {
    var difficulty by rememberSaveable { mutableStateOf(Difficulty.NORMAL) }
    var game by rememberSaveable(stateSaver = SessionSaver) { mutableStateOf(difficulty.newSession()) }
    var wins by rememberSaveable { mutableIntStateOf(0) }
    val image = when (game.phase) {
        GamePhase.WON -> R.drawable.imagem_conquista
        GamePhase.ABANDONED -> R.drawable.imagem_desistencia
        else -> when {
            game.progress >= 0.66f -> R.drawable.imagem_final
            game.progress >= 0.33f -> R.drawable.imagem_mediana
            else -> R.drawable.imagem_inicial
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState()).padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
        Text(stringResource(R.string.wins, wins), color = MaterialTheme.colorScheme.primary)
        if (game.phase == GamePhase.READY) {
            Text(stringResource(R.string.choose_difficulty), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Difficulty.entries.forEach { option ->
                    FilterChip(
                        selected = difficulty == option,
                        onClick = { difficulty = option; game = option.newSession() },
                        label = { Text(stringResource(option.labelResource())) }
                    )
                }
            }
        }
        Text(
            stringResource(R.string.difficulty_range, stringResource(difficulty.labelResource()), difficulty.targets.first, difficulty.targets.last),
            style = MaterialTheme.typography.bodyMedium
        )
        Image(painterResource(image), contentDescription = null, modifier = Modifier.size(220.dp))
        Text(stringResource(when(game.phase) {
            GamePhase.READY -> R.string.ready
            GamePhase.PLAYING -> R.string.playing
            GamePhase.WON -> R.string.won
            GamePhase.ABANDONED -> R.string.abandoned
        }), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        if (game.phase != GamePhase.READY) {
            LinearProgressIndicator(progress = game.progress, modifier = Modifier.fillMaxWidth())
            Text(stringResource(R.string.click_count, game.clicks, game.target))
        }
        when (game.phase) {
            GamePhase.READY -> Button(onClick = { game = game.start() }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.start))
            }
            GamePhase.PLAYING -> {
                Button(onClick = {
                    game = game.click()
                    if (game.phase == GamePhase.WON) wins++
                }, modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)) { Text(stringResource(R.string.click)) }
                TextButton(onClick = { game = game.abandon() }) { Text(stringResource(R.string.give_up)) }
            }
            else -> Button(onClick = { game = difficulty.newSession() }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.play_again))
            }
        }
    }
}

private fun Difficulty.labelResource(): Int = when (this) {
    Difficulty.EASY -> R.string.difficulty_easy
    Difficulty.NORMAL -> R.string.difficulty_normal
    Difficulty.HARD -> R.string.difficulty_hard
}
