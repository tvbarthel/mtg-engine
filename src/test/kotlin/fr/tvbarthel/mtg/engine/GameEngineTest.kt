package fr.tvbarthel.mtg.engine

import fr.tvbarthel.mtg.engine.opening.OpeningStage
import fr.tvbarthel.mtg.engine.playing.PlayingStage
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK

/**
 * Ensure that [GameEngine] behavior if the expected one and won't break in the future.
 */
class GameEngineTest : StringSpec() {

    @RelaxedMockK
    lateinit var openingStage: OpeningStage

    @RelaxedMockK
    lateinit var playingStage: PlayingStage

    init {
        MockKAnnotations.init(this)

        "given most simple config when simulate then right result" {
            // given
            val config = mockk<GameConfig> {
                every { state } returns mockk {
                    every { turn } returns 0
                }
                every { agents } returns emptyMap()
            }

            // when
            val result = GameEngine(openingStage, playingStage).simulate(config)

            // then
            result.config shouldBe config
            verifyOrder {
                openingStage.proceed(any(), any())
                playingStage.proceed(any(), any())
            }
        }

        "given post opening state when simulate then opening not replayed" {
            // given
            val config = mockk<GameConfig> {
                every { state } returns mockk {
                    every { turn } returns 1
                }
                every { agents } returns emptyMap()
            }

            // when
            val result = GameEngine(openingStage, playingStage).simulate(config)

            // then
            result.config shouldBe config
            verifyOrder {
                openingStage.proceed(any(), any()) wasNot Called
                playingStage.proceed(any(), any())
            }
        }
    }
}