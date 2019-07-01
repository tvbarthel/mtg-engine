package fr.tvbarthel.mtg.experimentation

import java.rmi.UnexpectedException
import java.util.*
import kotlin.math.min

/**
 * Very first game loop implementation: very naive approach that centralises all the logic into the game loop itself.
 */
class FirstNaiveGameLoop : GameLoop() {

    private val attackActions = mutableListOf<AttackAction>()
    private val blockActions = mutableListOf<BlockAction>()
    private val cleanupActions = mutableListOf<CleanupAction>()
    private val loreCounterMap = mutableMapOf<Player, MutableMap<SagaCard, Int>>()

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
        handleStepStart(stepContext, activePlayer, opponentPlayer)

        val actionStack = Stack<ActionContext>()
        while (true) {
            val player1Action = activePlayer.getAction(turn, step)
            val player1Passes = player1Action == null || player1Action is PassAction
            if (!player1Passes) {
                println("\t Player $activePlayer adds to stack -> $player1Action")
                val actionContext = ActionContext(player1Action!!, activePlayer, opponentPlayer)
                actionStack.push(actionContext)
            } else {
                println("\t Player $activePlayer passes.")
            }

            val player2Action = opponentPlayer.getAction(turn, step)
            val player2Passes = player2Action == null || player2Action is PassAction
            if (!player2Passes) {
                println("\t Player $opponentPlayer adds to stack -> $player2Action")
                val actionContext = ActionContext(player2Action!!, opponentPlayer, activePlayer)
                actionStack.push(actionContext)
            } else {
                println("\t Player $opponentPlayer passes.")
            }

            if ((player1Passes || player2Passes) && actionStack.isNotEmpty()) {
                while (actionStack.isNotEmpty()) {
                    val actionContext = actionStack.pop()
                    handlePlayerAction(
                        stepContext,
                        actionContext.activePlayer,
                        actionContext.opponent,
                        actionContext.action
                    )
                }
            }

            if (player1Passes && player2Passes) {
                break
            }
        }

        handleStepEnd(step)
        println("Playing step for turn $turn $step <----")
    }

    private fun handlePlayerAction(context: StepContext, player: Player, opponent: Player, action: Action) {
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
            handleEnchantementCasted(context, action.enchantmentCard, player, opponent)
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

        if (creatureEnteringBattlefield is GhituLavarunner) {
            val numberOfInstantInGraveyard = player.graveyard.count { card -> card is InstantCard }
            if (numberOfInstantInGraveyard >= 2) {
                activateBonuses(creatureEnteringBattlefield)
            }
        }
    }

    private fun handleCreatureLeaveBattlefield(
        creatureLeavingBattlefield: CreatureCard,
        creatureOwner: Player,
        opponent: Player
    ) {
        if (creatureLeavingBattlefield is BenalishMarshal) {
            for (cardOnBoard in creatureOwner.board) {
                if (cardOnBoard is CreatureCard) {
                    cardOnBoard.power.removeModifiers(creatureLeavingBattlefield)
                    cardOnBoard.toughness.removeModifiers(creatureLeavingBattlefield)
                }
            }
        }

        creatureOwner.graveyard.add(creatureLeavingBattlefield)
    }

    private fun handleAbilityActivated(
        context: StepContext,
        ability: Ability,
        player: Player,
        opponent: Player
    ) {
        if (ability is DauntlessBodyguard.SacrificeToGiveIndestructibleAbility) {
            // Give indestructible until end of turn
            val modifier = BooleanValueModifier(ability.owner, true)
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
            } else if (instant.target is CreatureCard) {
                val damages = IntValueModifier(context.turnContext, -2)
                instant.target.toughness.addModifier(damages)

                val isKilled = !instant.target.isIndestructible()
                        && instant.target.toughness.getCurrentValue() <= 0

                if (isKilled) {
                    println("\t Shock killed ${instant.target}")
                    val owner = getOwner(context.turnContext, instant.target)
                    owner.board.remove(instant.target)
                    handleCreatureLeaveBattlefield(instant.target, opponent, player)
                }
            }
        }

        player.graveyard.add(instant)
        handleCardAddedToGraveyard(context, instant, player, opponent)
    }

    private fun handleEnchantementCasted(
        context: StepContext,
        enchantmentCard: EnchantmentCard,
        player: Player,
        opponent: Player
    ) {
        player.board.add(enchantmentCard)

        if (enchantmentCard is SagaCard) {
            increaseLoreCounter(context, enchantmentCard, player, opponent)
        }
    }

    private fun handleCardAddedToGraveyard(context: StepContext, card: Card, player: Player, opponent: Player) {
        if (card is InstantCard) {
            player.board
                .filterIsInstance<GhituLavarunner>()
                .forEach { ghituLavarunner ->
                    val activateGhituBonuses = player.graveyard.count { candidate -> candidate is InstantCard } >= 2
                    val hasAlreadyBonuses = ghituLavarunner.haste.hasModifier(ghituLavarunner)
                            && ghituLavarunner.power.hasModifier(ghituLavarunner)
                    if (!hasAlreadyBonuses && activateGhituBonuses) {
                        activateBonuses(ghituLavarunner)
                    }
                }

        }
    }

    private fun handleStepStart(context: StepContext, player: Player, opponent: Player) {
        when (context.step) {
            Step.BeginningPhaseUntapStep -> {
                val cleanDamages = RemoveDamages(context.turnContext)
                cleanupActions.add(cleanDamages)
            }
            Step.CombatPhaseDamageStep -> {
                resolveCombatDamages(context, player, opponent)
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

    private fun increaseLoreCounter(context: StepContext, sagaCard: SagaCard, player: Player, opponent: Player) {
        val playerLoreCounters = loreCounterMap.getOrPut(player) { mutableMapOf() }
        val oldLoreCounter = playerLoreCounters.getOrPut(sagaCard) { 0 }
        val newLoreCounter = oldLoreCounter + 1

        if (sagaCard is HistoryOfBenalia) {
            when (newLoreCounter) {
                1 -> {
                    val knightToken = KnightToken("${sagaCard.id}-1")
                    player.board.add(knightToken)
                    handleCreatureEnterBattlefield(context, knightToken, player, opponent)
                }
                else -> {
                    throw IllegalArgumentException("Invalid lore counter $newLoreCounter")
                }
            }
        }

        if (newLoreCounter >= 3) {
            player.board.remove(sagaCard)
        }
    }

    private fun resolveCombatDamages(context: StepContext, attackingPlayer: Player, defendingPlayer: Player) {
        val blockerMap =
            blockActions.map { blockAction -> blockAction.blockedCreature.id to blockAction }.toMap()

        for (attackAction in attackActions) {
            val blockerAction = blockerMap[attackAction.creatureCard.id]

            if (blockerAction != null) {
                resolveBlockAction(context, blockerAction, attackingPlayer, defendingPlayer)
            } else {
                println("\t ${attackAction.creatureCard} deals ${attackAction.creatureCard.power.getCurrentValue()} to ${attackAction.target}")
                attackAction.target.life -= attackAction.creatureCard.power.getCurrentValue()
            }
        }
    }

    private fun resolveBlockAction(
        context: StepContext,
        blockAction: BlockAction,
        attackingPlayer: Player,
        defendingPlayer: Player
    ) {
        val blockedCreature = blockAction.blockedCreature
        val blockingCreatures = blockAction.blockingCreatures.toMutableList()
        println("\t $blockedCreature blocked by $blockingCreatures")

        var remainingDamagesToDealToBlockingCreatures = blockedCreature.power.getCurrentValue()
        var damagesDealtToBlockedCreature = 0

        while (remainingDamagesToDealToBlockingCreatures > 0 && blockingCreatures.isNotEmpty()) {
            val blockingCreature = blockingCreatures.removeAt(0)
            damagesDealtToBlockedCreature += blockingCreature.power.getCurrentValue()
            val damagesDealtToBlockingCreature =
                min(remainingDamagesToDealToBlockingCreatures, blockingCreature.toughness.getCurrentValue())
            remainingDamagesToDealToBlockingCreatures -= damagesDealtToBlockingCreature

            val damageToBlockedCreature = IntValueModifier(context.turnContext, -damagesDealtToBlockedCreature)
            val damageToBlockingCreature = IntValueModifier(context.turnContext, -damagesDealtToBlockingCreature)

            blockedCreature.toughness.addModifier(damageToBlockedCreature)
            blockingCreature.toughness.addModifier(damageToBlockingCreature)

            if (blockingCreature.toughness.getCurrentValue() <= 0) {
                if (blockingCreature.isIndestructible()) {
                    println("\t Blocking creature is indestructible. It won't die.")
                } else {
                    defendingPlayer.board.remove(blockingCreature)
                    println("\t Blocking creature $blockingCreature dies.")
                    handleCreatureLeaveBattlefield(blockingCreature, defendingPlayer, attackingPlayer)
                }
            }

            if (blockedCreature.toughness.getCurrentValue() <= 0) {
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

    private fun getOwner(turnContext: TurnContext, creatureCard: CreatureCard): Player {
        return when {
            turnContext.activePlayer.board.contains(creatureCard) -> turnContext.activePlayer
            turnContext.opponentPlayer.board.contains(creatureCard) -> turnContext.opponentPlayer
            else -> throw IllegalArgumentException("No owner found for creature $creatureCard")
        }
    }


    private fun activateBonuses(ghituLavarunner: GhituLavarunner) {
        val powerModifier = IntValueModifier(ghituLavarunner, 1)
        ghituLavarunner.power.addModifier(powerModifier)

        val hasteModifier = BooleanValueModifier(ghituLavarunner, true)
        ghituLavarunner.haste.addModifier(hasteModifier)
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

    private class RemoveDamages(val turnContext: TurnContext) : CleanupAction {
        override fun clean() {
            removeDamages(turnContext.activePlayer)
            removeDamages(turnContext.opponentPlayer)
        }

        private fun removeDamages(player: Player) {
            for (card in player.board) {
                if (card is CreatureCard) {
                    card.toughness.removeModifiers(turnContext)
                }
            }
        }
    }

    private class ActionContext(val action: Action, val activePlayer: Player, val opponent: Player)

}