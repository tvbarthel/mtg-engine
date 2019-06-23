package fr.tvbarthel.mtg.experimentation

/**
 * Very first game loop implementation: very naive approach that centralises all the logic into the game loop itself.
 */
class FirstNaiveGameLoop : GameLoop() {

    private val attackActions = mutableListOf<AttackAction>()
    private val blockActions = mutableListOf<BlockAction>()

    override fun playTurn(turnContext: TurnContext) {
        playStep(turnContext, Step.BeginningPhaseUntapStep)
        playStep(turnContext, Step.BeginningPhaseUpKeepStep)
        playStep(turnContext, Step.BeginningPhaseDrawStep)

        playStep(turnContext, Step.FirstMainPhaseStep)

        playStep(turnContext, Step.CombatPhaseBeginningStep)
        playStep(turnContext, Step.CombatPhaseDeclareAttackersStep)
        playStep(turnContext, Step.CombatPhaseDeclareBlockersStep)
        playStep(turnContext, Step.CombatPhaseDamageStep)
        playStep(turnContext, Step.CombatPhaseEndStep)

        playStep(turnContext, Step.SecondMainPhaseStep)

        playStep(turnContext, Step.EndingPhaseEndStep)
        playStep(turnContext, Step.EndingPhaseCleanupStep)
    }

    private fun playStep(turnContext: TurnContext, step: Step) {
        val turn = turnContext.turnIndex
        val activePlayer = turnContext.activePlayer
        val opponentPlayer = turnContext.opponentPlayer

        println("\nPlaying step for turn $turn $step ---->")
        handleStepStart(step, activePlayer, opponentPlayer)

        while (true) {
            val player1Action = activePlayer.getAction(turn, step)
            println("\t Player $activePlayer Action -> $player1Action")
            handlePlayerAction(activePlayer, opponentPlayer, player1Action)

            val player2Action = opponentPlayer.getAction(turn, step)
            println("\t Player $opponentPlayer Action -> $player2Action")
            handlePlayerAction(opponentPlayer, activePlayer, player2Action)

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

        if (action is CastCreatureAction) {
            player.board.add(action.creatureCard)
            handleCreatureEnterBattlefield(action.creatureCard, player, opponent)
        }

        if (action is CastEnchantmentAction) {
            player.board.add(action.enchantmentCard)
        }
    }

    private fun handleCreatureEnterBattlefield(
        creatureEnteringBattlefield: CreatureCard,
        player: Player,
        opponent: Player
    ) {
        for (card in player.board) {
            if (card is AjanisWelcome) {
                player.life += 1
                println("\t $card triggered! Player $player gained one life.")
            }

            if (card is BenalishMarshal) {
                creatureEnteringBattlefield.addPowerModifier(Modifier(card, 1))
                creatureEnteringBattlefield.addToughnessModifier(Modifier(card, 1))
                println("\t $card applied modifier to $creatureEnteringBattlefield")
            }

            if (creatureEnteringBattlefield is BenalishMarshal && card is CreatureCard) {
                card.addPowerModifier(Modifier(creatureEnteringBattlefield, 1))
                card.addToughnessModifier(Modifier(creatureEnteringBattlefield, 1))
                println("\t $creatureEnteringBattlefield applied modifier to $card")
            }
        }
    }

    private fun handleCreatureLeaveBattlefield(
        creatureLeavingBattlefield: CreatureCard,
        player: Player,
        opponent: Player
    ) {
        if (creatureLeavingBattlefield is BenalishMarshal) {
            for (cardOnBoard in player.board) {
                if (cardOnBoard is CreatureCard) {
                    cardOnBoard.removePowerModifiers(creatureLeavingBattlefield)
                    cardOnBoard.removeToughnessModifiers(creatureLeavingBattlefield)
                }
            }
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

    private fun resolveCombatDamages(attackingPlayer: Player, defendingPlayer: Player) {
        val blockerMap =
            blockActions.map { blockAction -> blockAction.blockedCreature.id to blockAction }.toMap()

        for (attackAction in attackActions) {
            val blockerAction = blockerMap[attackAction.creatureCard.id]

            if (blockerAction != null) {
                resolveBlockAction(blockerAction, attackingPlayer, defendingPlayer)
            } else {
                println("\t ${attackAction.creatureCard} deals ${attackAction.creatureCard.getCurrentPower()} to ${attackAction.target}")
                attackAction.target.life -= attackAction.creatureCard.getCurrentPower()
            }
        }
    }

    private fun resolveBlockAction(blockAction: BlockAction, attackingPlayer: Player, defendingPlayer: Player) {
        val blockedCreature = blockAction.blockedCreature
        val blockingCreatures = blockAction.blockingCreatures.toMutableList()
        println("\t $blockedCreature blocked by $blockingCreatures")

        var remainingDamagesToDealToBlockingCreatures = blockedCreature.getCurrentPower()
        var damagesDealtToBlockedCreature = 0

        while (remainingDamagesToDealToBlockingCreatures > 0 && blockingCreatures.isNotEmpty()) {
            val blockingCreature = blockingCreatures.removeAt(0)
            damagesDealtToBlockedCreature += blockingCreature.getCurrentPower()
            val damagesDealtToBlockingCreature =
                Math.min(remainingDamagesToDealToBlockingCreatures, blockingCreature.getCurrentToughness())
            remainingDamagesToDealToBlockingCreatures -= damagesDealtToBlockingCreature

            if (damagesDealtToBlockingCreature >= blockingCreature.getCurrentToughness()) {
                defendingPlayer.board.remove(blockingCreature)
                println("\t Blocking creature $blockingCreature dies.")
                handleCreatureLeaveBattlefield(blockingCreature, defendingPlayer, attackingPlayer)
            }

            if (damagesDealtToBlockedCreature >= blockedCreature.getCurrentToughness()) {
                attackingPlayer.board.remove(blockedCreature)
                println("\t Blocked creature $blockedCreature dies.")
                handleCreatureLeaveBattlefield(blockedCreature, attackingPlayer, defendingPlayer)
                break
            }
        }
    }

}