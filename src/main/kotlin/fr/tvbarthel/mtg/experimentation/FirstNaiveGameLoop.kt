package fr.tvbarthel.mtg.experimentation

class FirstNaiveGameLoop(player1: Player, player2: Player) : GameLoop(player1, player2) {

    private val attackActions = mutableListOf<AttackAction>()
    private val blockActions = mutableListOf<BlockAction>()

    override fun playTurn(turn: Int) {
        playStep(turn, Step.BeginningPhaseUntapStep)
        playStep(turn, Step.BeginningPhaseUpKeepStep)
        playStep(turn, Step.BeginningPhaseDrawStep)

        playStep(turn, Step.FirstMainPhaseStep)

        playStep(turn, Step.CombatPhaseBeginningStep)
        playStep(turn, Step.CombatPhaseDeclareAttackersStep)
        playStep(turn, Step.CombatPhaseDeclareBlockersStep)
        playStep(turn, Step.CombatPhaseDamageStep)
        playStep(turn, Step.CombatPhaseEndStep)

        playStep(turn, Step.SecondMainPhaseStep)

        playStep(turn, Step.EndingPhaseEndStep)
        playStep(turn, Step.EndingPhaseCleanupStep)
    }

    private fun playStep(turn: Int, step: Step) {
        println("\nPlaying step for turn $turn $step ---->")
        handleStepStart(step)

        while (true) {
            val player1Action = player1.getAction(turn, step)
            println("\t Player $player1 Action -> $player1Action")
            handlePlayerAction(player1, player2, player1Action)

            val player2Action = player2.getAction(turn, step)
            println("\t Player $player2 Action -> $player2Action")
            handlePlayerAction(player2, player1, player2Action)

            if (player1Action == null && player2Action == null) {
                break
            }
        }
        handleStepEnd(step)
        println("Playing step for turn $turn $step <----")
    }

    private fun handlePlayerAction(player: Player, opponent: Player, action: Action?) {
        if (action == null) {
            return
        }

        if (action is DeclareAttackersAction) {
            attackActions.addAll(action.attackActions)
        }

        if (action is DeclareBlockersAction) {
            blockActions.addAll(action.blockActions)
        }

        if (action is PlayLandAction) {
            player.board.add(action.landCard)
        }

        if (action is SpawnCreatureAction) {
            player.board.add(action.creatureCard)
        }
    }

    private fun handleStepStart(step: Step) {
        when (step) {
            Step.CombatPhaseDamageStep -> {
                val blockerMap =
                    blockActions.map { blockAction -> blockAction.blockedCreature.id to blockAction }.toMap()

                for (attackAction in attackActions) {
                    val blockerAction = blockerMap[attackAction.creatureCard.id]

                    if (blockerAction != null) {
                        println("\t ${attackAction.creatureCard} blocked by ${blockerAction.blockingCreatures}")
                        // TODO apply damages to creatures
                    } else {
                        println("\t ${attackAction.creatureCard} deals ${attackAction.creatureCard.power} to ${attackAction.target}")
                        attackAction.target.life -= attackAction.creatureCard.power
                    }
                }
            }
            else -> {

            }
        }
    }

    private fun handleStepEnd(step: Step) {
        when (step) {
            Step.EndingPhaseCleanupStep -> {
                println("\t cleaning attack actions")
                attackActions.clear()

                println("\t cleaning block actions")
                blockActions.clear()
            }
            else -> {
            }
        }
    }

}