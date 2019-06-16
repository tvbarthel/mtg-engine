package fr.tvbarthel.mtg.engine

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * Ensure that [GameEngine] behavior if the expected one and won't break in the future.
 */
class GameEngineTest : StringSpec({

    "given most simple config when simulate then right result" {
        // given
        val config = GameConfig()

        // when
        val result = GameEngine().simulate(config)

        // then
        result.config shouldBe config
        result.turn shouldBe 0
    }
})