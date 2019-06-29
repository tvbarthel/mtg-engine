package fr.tvbarthel.mtg.experimentation

import java.rmi.UnexpectedException

/**
 * Very first game loop implementation: very naive approach that centralises all the logic into the game loop itself.
 */
class FirstNaiveGameLoop : GameLoop() {

    private val attackActions = mutableListOf<AttackAction>()
    private val blockActions = mutableListOf<BlockAction>()
    private val cleanupActions = mutableListOf<CleanupAction>()

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
        val stepContext = StepContext(turnContext, step)

        println("\nPlaying step for turn $turn $step ---->")
        handleStepStart(step, activePlayer, opponentPlayer)

        while (true) {
            val player1Action = activePlayer.getAction(turn, step)
            println("\t Player $activePlayer Action -> $player1Action")
            handlePlayerAction(stepContext, activePlayer, opponentPlayer, player1Action)

            val player2Action = opponentPlayer.getAction(turn, step)
            println("\t Player $opponentPlayer Action -> $player2Action")
            handlePlayerAction(stepContext, opponentPlayer, activePlayer, player2Action)

            if (player1Action == null && player2Action == null) {
                break
            }
        }
        handleStepEnd(step)
        println("Playing step for turn $turn $step <----")
    }

    private fun handlePlayerAction(context: StepContext, player: Player, opponent: Player, action: Action?) {
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
            handleCreatureEnterBattlefield(context, action.creatureCard, player, opponent)
        }

        if (action is CastEnchantmentAction) {
            player.board.add(action.enchantmentCard)
        }

        if (action is CastInstantAction) {
            handleInstantCasted(context, action.instantCard, player, opponent)
        }

        if (action is ActivateAbilityAction) {
            handleAbilityActivated(context, action.ability, player, opponent)
        }
    }

    private fun handleCreatureEnterBattlefield(
        context: StepContext,
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
                creatureEnteringBattlefield.power.addModifier(card, 1)
                creatureEnteringBattlefield.toughness.addModifier(card, 1)
                println("\t $card applied modifier to $creatureEnteringBattlefield")
            }

            if (creatureEnteringBattlefield is BenalishMarshal && card is CreatureCard) {
                card.power.addModifier(creatureEnteringBattlefield, 1)
                card.toughness.addModifier(creatureEnteringBattlefield, 1)
                println("\t $creatureEnteringBattlefield applied modifier to $card")
            }
        }

        if (creatureEnteringBattlefield is DauntlessBodyguard) {
            val action = player.getAction(context.turnContext.turnIndex, context.step)
            if (action is PassAction) {
                // No-op
            } else if (action is SelectCreatureAction) {
                val owner = creatureEnteringBattlefield
                val target = action.selectedCreature
                val ability = DauntlessBodyguard.SacrificeToGiveIndestructibleAbility(owner, target)
                owner.abilities.add(ability)
            } else {
                throw UnexpectedException("Not a valid action after $creatureEnteringBattlefield enters battlefield")
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
                    cardOnBoard.power.removeModifiers(creatureLeavingBattlefield)
                    cardOnBoard.toughness.removeModifiers(creatureLeavingBattlefield)
                }
            }
        }
    }

    private fun handleAbilityActivated(
        context: StepContext,
        ability: Ability,
        player: Player,
        opponent: Player
    ) {
        if (ability is DauntlessBodyguard.SacrificeToGiveIndestructibleAbility) {
            // Give indestructible until end of turn
            val modifier = BooleanValueModifier(ability.target, true)
            ability.target.indestructible.addModifier(modifier)
            val cleanupAction = RemoveBooleanModifier(ability.target.indestructible, modifier)
            cleanupActions.add(cleanupAction)
            println("\t ${ability.target} gains indestructible until end of turns.")

            // Sacrifice dauntless bodyguard
            player.board.remove(ability.owner)
            handleCreatureLeaveBattlefield(ability.owner, player, opponent)
            println("\t ${ability.owner} is sacrificed.")
        }
    }

    private fun handleInstantCasted(
        context: StepContext,
        instant: InstantCard,
        player: Player,
        opponent: Player
    ) {
        if (instant is Shock) {
            if (instant.target is Player) {
                instant.target.life -= 2
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

                for (cleanupTarget in cleanupActions) {
                    cleanupTarget.clean()
                }
                cleanupActions.clear()

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
                println("\t ${attackAction.creatureCard} deals ${attackAction.creatureCard.power.getCurrentValue()} to ${attackAction.target}")
                attackAction.target.life -= attackAction.creatureCard.power.getCurrentValue()
            }
        }
    }

    private fun resolveBlockAction(blockAction: BlockAction, attackingPlayer: Player, defendingPlayer: Player) {
        val blockedCreature = blockAction.blockedCreature
        val blockingCreatures = blockAction.blockingCreatures.toMutableList()
        println("\t $blockedCreature blocked by $blockingCreatures")

        var remainingDamagesToDealToBlockingCreatures = blockedCreature.power.getCurrentValue()
        var damagesDealtToBlockedCreature = 0

        while (remainingDamagesToDealToBlockingCreatures > 0 && blockingCreatures.isNotEmpty()) {
            val blockingCreature = blockingCreatures.removeAt(0)
            damagesDealtToBlockedCreature += blockingCreature.power.getCurrentValue()
            val damagesDealtToBlockingCreature =
                Math.min(remainingDamagesToDealToBlockingCreatures, blockingCreature.toughness.getCurrentValue())
            remainingDamagesToDealToBlockingCreatures -= damagesDealtToBlockingCreature

            if (damagesDealtToBlockingCreature >= blockingCreature.toughness.getCurrentValue()) {
                if (blockingCreature.isIndestructible()) {
                    println("\t Blocking creature is indestructible. It won't die.")
                } else {
                    defendingPlayer.board.remove(blockingCreature)
                    println("\t Blocking creature $blockingCreature dies.")
                    handleCreatureLeaveBattlefield(blockingCreature, defendingPlayer, attackingPlayer)
                }
            }

            if (damagesDealtToBlockedCreature >= blockedCreature.toughness.getCurrentValue()) {
                if (blockedCreature.isIndestructible()) {
                    println("\t Blocked creature is indestructible. It won't die.")
                } else {
                    attackingPlayer.board.remove(blockedCreature)
                    println("\t Blocked creature $blockedCreature dies.")
                    handleCreatureLeaveBattlefield(blockedCreature, attackingPlayer, defendingPlayer)
                }
                break
            }
        }
    }

    private class StepContext(val turnContext: TurnContext, val step: Step)

    interface CleanupAction {
        fun clean()
    }

    private class RemoveBooleanModifier(val target: ModifiableBooleanValue, val modifier: BooleanValueModifier) :
        CleanupAction {
        override fun clean() {
            target.removeModifier(modifier)
        }
    }

}