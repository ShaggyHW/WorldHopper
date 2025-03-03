package net.botwithus

import net.botwithus.internal.scripts.ScriptDefinition
import net.botwithus.rs3.game.Client
import net.botwithus.rs3.game.hud.interfaces.Interfaces
import net.botwithus.rs3.game.login.LoginManager
import net.botwithus.rs3.game.login.World
import net.botwithus.rs3.game.minimenu.MiniMenu
import net.botwithus.rs3.game.minimenu.actions.ComponentAction
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery
import net.botwithus.rs3.game.queries.builders.worlds.WorldQuery
import net.botwithus.rs3.script.Execution
import net.botwithus.rs3.script.LoopingScript
import net.botwithus.rs3.script.ScriptConsole
import net.botwithus.rs3.script.config.ScriptConfig
import net.botwithus.rs3.util.Regex
import java.util.Random

class WorldHopper(name: String, scriptConfig: ScriptConfig, scriptDefinition: ScriptDefinition): LoopingScript(name, scriptConfig, scriptDefinition) {

	private val random: Random = Random()
	var botState: BotState = BotState.IDLE
	var someBoolean: Boolean = true
	var isNpc = false
	var isSceneObject = false
	var npcName = "Undead"

	val membersWorlds: List<Int> = listOf(1, 4, 5, 6, 9, 10, 12, 14, 16, 21, 22, 23, 24, 25, 26, 27, 28, 31, 32, 35, 36, 37, 39, 40, 42, 44, 45, 46, 49, 50, 51, 53, 54, 56, 58, 59, 60, 62, 63, 64, 65,
										  67, 68, 69, 70, 71, 72, 73, 74, 76, 77, 78, 79, 82, 83, 85, 87, 88, 89, 91, 92, 96, 98, 99, 100, 103, 104, 105, 106, 116, 117, 119, 123, 124, 138, 139, 140,
										  252, 257, 258, 259)

	enum class BotState {
		//define your bot states here
		IDLE,
		RUNNING,
		HOPPING
		//etc..
	}

	override fun initialize(): Boolean {
		super.initialize()
		// Set the script graphics context to our custom one
		this.sgc = WorldHopperGraphicsContext(this, console)
		return true
	}

	override fun onLoop() {
		val player = Client.getLocalPlayer()
		if (Client.getGameState() != Client.GameState.LOGGED_IN || player == null || botState == BotState.IDLE) {
			Execution.delay(random.nextLong(600, 1200))
			return
		}
		when (botState) {
			BotState.RUNNING -> {
				ScriptConsole.println(npcName)
				if (isNpc) {
					val pattern = Regex.getPatternForContainsString(npcName)
					val npc = NpcQuery.newQuery().name(pattern).results().first()
					if (npc == null) {
						ScriptConsole.println("NPC NOT FOUND")
						botState = BotState.HOPPING
					}
					return
				}
				if (isSceneObject) {
					val pattern = Regex.getPatternForContainsString(npcName)
					val sceneObject = SceneObjectQuery.newQuery().name(pattern).results().first()
					if (sceneObject == null) {
						ScriptConsole.println("SCENE OBJECT NOT FOUND")
						botState = BotState.HOPPING
					}
					return
				}

			}

			BotState.IDLE    -> {
				println("We're idle!")

			}

			BotState.HOPPING -> {
				botState = BotState.RUNNING
				hopToNextWorld()

			}
		}
		Execution.delay(random.nextLong(600, 1200))
		return
	}

	private fun hopToNextWorld() {

		var availableWorlds: MutableList<World> = ArrayList()

		if (Client.isMember()) {
			availableWorlds = WorldQuery.newQuery().members().population(0, 1300).results().stream().toList()

		}

		if (availableWorlds.isNotEmpty()) {
			ScriptConsole.println("Filtered worlds meeting criteria:")


			for (world in availableWorlds) {
				ScriptConsole.println("World ID: " + world.id + ", Population: " + world.population + ", Ping: " + world.ping)
			}

			availableWorlds = availableWorlds.filter { membersWorlds.contains(it.id) }.toMutableList()
			val randomWorld = availableWorlds[random.nextInt(availableWorlds.size)]

			ScriptConsole.println("Selected world to hop: " + randomWorld.id + " with " + randomWorld.population + " players and ping: " + randomWorld.ping)

			hopWorld(randomWorld.id)
		} else {
			ScriptConsole.println("No worlds available that meet the player count, ping, and membership criteria.")
		}
	}

	fun hopWorld(targetWorld: Int) {
		val delaySmall = (600 + random.nextInt(1200)).toLong()
		val player = Client.getLocalPlayer()

		ScriptConsole.println("Opening world selection interface.")
		MiniMenu.interact(ComponentAction.COMPONENT.type, 1, 7, 93782016)
		Execution.delay(delaySmall)
		ScriptConsole.println("Navigating to the world list.")
		MiniMenu.interact(ComponentAction.COMPONENT.type, 1, -1, 93913153)
		Execution.delay(delaySmall)

		val interfaceId = 1587
		ScriptConsole.println("Logging available worlds from interface ID $interfaceId")

		if (Execution.delayUntil(5000) { Interfaces.isOpen(interfaceId) }) {
			ScriptConsole.println("Attempting to interact with world: $targetWorld")
			MiniMenu.interact(ComponentAction.COMPONENT.type, 1, targetWorld, 104005640)
			ScriptConsole.println("World hop executed.")
			Execution.delayUntil(15000) { LoginManager.getLoginStatus() == 1 }
		}
	}


}