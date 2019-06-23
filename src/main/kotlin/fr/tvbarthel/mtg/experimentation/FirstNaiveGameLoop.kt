package fr.tvbarthel.mtg.experimentation

/**
 * Very first game loop implementation: very naive approach that centralises all the logic into the game loop itself.
 */
class FirstNaiveGameLoop : GameLoop() {

    private val attackActions = mutableListOf<AttackAction>()
    private val blockActions = mutableListOf<BlockAction>()

    override fun playTurn(turn: Int, activePlayer: Player, opponent: Player) {
        playStep(turn, Step.BeginningPhaseUntapStep, activePlayer, opponent)
        playStep(turn, Step.BeginningPhaseUpKeepStep, activePlayer, opponent)
        playStep(turn, Step.BeginningPhaseDrawStep, activePlayer, opponent)

        playStep(turn, Step.FirstMainPhaseStep, activePlayer, opponent)

        playStep(turn, Step.CombatPhaseBeginningStep, activePlayer, opponent)
        playStep(turn, Step.CombatPhaseDeclareAttackersStep, activePlayer, opponent)
        playStep(turn, Step.CombatPhaseDeclareBlockersStep, activePlayer, opponent)
        playStep(turn, Step.CombatPhaseDamageStep, activePlayer, opponent)
        playStep(turn, Step.CombatPhaseEndStep, activePlayer, opponent)

        playStep(turn, Step.SecondMainPhaseStep, activePlayer, opponent)

        playStep(turn, Step.EndingPhaseEndStep, activePlayer, opponent)
        playStep(turn, Step.EndingPhaseCleanupStep, activePlayer, opponent)
    }

    private fun playStep(turn: Int, step: Step, activePlayer: Player, opponent: Player) {
        println("\nPlaying step for turn $turn $step ---->")
        handleStepStart(step, activePlayer, opponent)

        while (true) {
            val player1Action = activePlayer.getAction(turn, step)
            println("\t Player $activePlayer Action -> $player1Action")
            handlePlayerAction(activePlayer, opponent, player1Action)

            val player2Action = opponent.getAction(turn, step)
            println("\t Player $opponent Action -> $player2Action")
            handlePlayerAction(opponent, activePlayer, player2Action)

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

    private fun handleStepStart(step: Step, player: Player, opponent: Player) {
        when (step) {
            Step.CombatPhaseDamageStep -> {
                resolveCombatDamages(player, opponent)
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

    private fun resolveCombatDamages(player: Player, opponent: Player) {
        val blockerMap =
            blockActions.map { blockAction -> blockAction.blockedCreature.id to blockAction }.toMap()

        for (attackAction in attackActions) {
            val blockerAction = blockerMap[attackAction.creatureCard.id]

            if (blockerAction != null) {
                resolveBlockAction(blockerAction, player, opponent)
            } else {
                println("\t ${attackAction.creatureCard} deals ${attackAction.creatureCard.power} to ${attackAction.target}")
                attackAction.target.life -= attackAction.creatureCard.power
            }
        }
    }

    private fun resolveBlockAction(blockAction: BlockAction, player: Player, opponent: Player) {
        val blockedCreature = blockAction.blockedCreature
        val blockingCreatures = blockAction.blockingCreatures.toMutableList()
        println("\t $blockedCreature blocked by $blockingCreatures")

        var remainingDamagesToDealToBlockingCreatures = blockedCreature.power
        var damagesDealtToBlockedCreature = 0

        while (remainingDamagesToDealToBlockingCreatures > 0 && blockingCreatures.isNotEmpty()) {
            val blockingCreature = blockingCreatures.removeAt(0)
            damagesDealtToBlockedCreature += blockingCreature.power
            val damagesDealtToBlockingCreature =
                Math.min(remainingDamagesToDealToBlockingCreatures, blockingCreature.toughness)
            remainingDamagesToDealToBlockingCreatures -= damagesDealtToBlockingCreature

            if (damagesDealtToBlockingCreature >= blockingCreature.toughness) {
                opponent.board.remove(blockingCreature)
                println("\t Blocking creature $blockingCreature dies.")
            }

            if (damagesDealtToBlockedCreature >= blockedCreature.toughness) {
                player.board.remove(blockedCreature)
                println("\t Blocked creature $blockedCreature dies.")
                break
            }
        }
    }

}