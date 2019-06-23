package fr.tvbarthel.mtg.engine

import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk

/**
 * Ensure that [GameConfig] behavior is the expected one and won't break in the future.
 */
class GameConfigTest : StringSpec() {

    init {

        "given agent for every player when isValid then true"{
            // given
            val mockedSeed = 1234890L
            val mockedState = mockk<GameState> {
                every { players } returns listOf(
                    mockk { every { id } returns 1 },
                    mockk { every { id } returns 2 }
                )
            }
            val mockedAgents = mapOf<Int, Agent>(1 to mockk(), 2 to mockk())

            // when
            val valid = GameConfig(mockedState, mockedAgents, mockedSeed).isValid()

            //then
            assert(valid)
        }

        "given missing agent for a player when isValid then false"{
            // given
            val mockedSeed = 1234890L
            val mockedState = mockk<GameState> {
                every { players } returns listOf(
                    mockk { every { id } returns 1 },
                    mockk { every { id } returns 2 }
                )
            }
            val mockedAgents = mapOf<Int, Agent>(1 to mockk())

            // when
            val valid = GameConfig(mockedState, mockedAgents, mockedSeed).isValid()

            //then
            assert(valid.not())
        }
    }

}