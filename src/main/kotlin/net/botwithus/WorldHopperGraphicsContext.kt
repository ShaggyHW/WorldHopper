package net.botwithus

import net.botwithus.rs3.imgui.ImGui
import net.botwithus.rs3.imgui.ImGuiWindowFlag
import net.botwithus.rs3.script.ScriptConsole
import net.botwithus.rs3.script.ScriptGraphicsContext

class WorldHopperGraphicsContext(
		private val script: WorldHopper,
		console: ScriptConsole
): ScriptGraphicsContext(console) {

	override fun drawSettings() {
		super.drawSettings()
		if (ImGui.Begin("My script", ImGuiWindowFlag.None.value)) {
			if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.value)) {
				if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.value)) {
					ImGui.Text("Welcome to my script!")
					ImGui.Text("My scripts state is: " + script.botState)
					ImGui.EndTabItem()
				}
				script.isNpc = ImGui.Checkbox("IS NPC", script.isNpc)
				script.isSceneObject = ImGui.Checkbox("IS SCENEOBJECT", script.isSceneObject)
				script.npcName = ImGui.InputText("NPC Name", script.npcName)
				ImGui.Separator()
				if (script.botState == WorldHopper.BotState.IDLE) {
					if (ImGui.Button("Start Hopping")) {
						script.botState = WorldHopper.BotState.RUNNING
					}
				} else {
					if (ImGui.Button("Stop Hopping")) {
						script.botState = WorldHopper.BotState.IDLE
					}
				}
				ImGui.EndTabBar()
			}
			ImGui.End()
		}
	}

	override fun drawOverlay() {
		super.drawOverlay()
	}

}