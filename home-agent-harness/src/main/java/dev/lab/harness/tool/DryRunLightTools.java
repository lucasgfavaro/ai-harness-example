package dev.lab.harness.tool;

import dev.lab.harness.agent.ToolCall;

/** Simula la tool solo dentro de un request. No usa HTTP ni conserva estado. */
public final class DryRunLightTools implements ToolExecutor {

	private boolean on;

	@Override
	public String execute(ToolCall call) {
		on = switch (call.name()) {
			case "turn_light_on" -> true;
			case "turn_light_off" -> false;
			case "get_light_state" -> on;
			default -> throw new IllegalArgumentException("Tool dry-run desconocida: " + call.name());
		};
		return "{\"on\":" + on + ",\"simulated\":true}";
	}
}
