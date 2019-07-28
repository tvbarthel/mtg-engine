import fr.tvbarthel.mtg.engine.*
import fr.tvbarthel.mtg.engine.agent.ConsoleAgent
import fr.tvbarthel.mtg.engine.agent.RandomAgent
import fr.tvbarthel.mtg.engine.card.white.Plains
import kotlin.random.Random

fun main() {
    val engine = GameEngine()

    val seed = Random.nextLong()
    val player1 = Player(1).apply { repeat(60) { library.add(Plains()) } }
    val player2 = Player(2).apply { repeat(60) { library.add(Plains()) } }
    val state = GameState(seed, listOf(player1, player2))

    val config = GameConfig(state, mapOf(1 to RandomAgent(), 2 to ConsoleAgent()))
    val result = engine.simulate(config)

    println("$result")
}