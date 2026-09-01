package dev.lab.harness.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lab.harness.agent.ToolCall;

/** Simula todos los dispositivos en memoria, sin HTTP. Útil para tests y dry-run. */
public final class DryRunHomeTools implements ToolExecutor {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private boolean lightOn = false;
	private int blindsPosition = 50;
	private boolean locked = true;
	private double currentTemp = 20.0;
	private double targetTemp = 22.0;
	private String thermostatMode = "IDLE";

	@Override
	public String execute(ToolCall call) {
		return switch (call.name()) {
			case "get_light_state"   -> "{\"on\":" + lightOn + ",\"simulated\":true}";
			case "turn_light_on"     -> { lightOn = true;  yield "{\"on\":true,\"simulated\":true}"; }
			case "turn_light_off"    -> { lightOn = false; yield "{\"on\":false,\"simulated\":true}"; }

			case "get_blinds_state"    -> "{\"positionPercent\":" + blindsPosition + ",\"simulated\":true}";
			case "open_blinds"         -> { blindsPosition = 100; yield "{\"positionPercent\":100,\"simulated\":true}"; }
			case "close_blinds"        -> { blindsPosition = 0;   yield "{\"positionPercent\":0,\"simulated\":true}"; }
			case "set_blinds_position" -> {
				int p = Integer.parseInt(param(call, "percent"));
				blindsPosition = Math.max(0, Math.min(100, p));
				yield "{\"positionPercent\":" + blindsPosition + ",\"simulated\":true}";
			}

			case "get_lock_state" -> "{\"locked\":" + locked + ",\"simulated\":true}";
			case "lock_door"      -> { locked = true;  yield "{\"locked\":true,\"simulated\":true}"; }
			case "unlock_door"    -> { locked = false; yield "{\"locked\":false,\"simulated\":true}"; }

			case "get_thermostat_state" -> thermostatJson();
			case "set_thermostat_target" -> {
				targetTemp = Double.parseDouble(param(call, "temperature"));
				yield thermostatJson();
			}
			case "set_thermostat_mode" -> {
				thermostatMode = param(call, "mode");
				yield thermostatJson();
			}

			case "get_home_summary" -> summaryJson();

			default -> throw new IllegalArgumentException("Tool dry-run desconocida: " + call.name());
		};
	}

	private String thermostatJson() {
		return "{\"currentTemperature\":" + currentTemp
				+ ",\"targetTemperature\":" + targetTemp
				+ ",\"mode\":\"" + thermostatMode + "\""
				+ ",\"simulated\":true}";
	}

	private String summaryJson() {
		return "{\"gardenLight\":{\"on\":" + lightOn + "}"
				+ ",\"blinds\":{\"positionPercent\":" + blindsPosition + "}"
				+ ",\"lock\":{\"locked\":" + locked + "}"
				+ ",\"thermostat\":{\"currentTemperature\":" + currentTemp
				+ ",\"targetTemperature\":" + targetTemp
				+ ",\"mode\":\"" + thermostatMode + "\"}"
				+ ",\"simulated\":true}";
	}

	private static String param(ToolCall call, String key) {
		String args = call.arguments();
		if (args == null || args.isBlank()) throw new IllegalArgumentException("Falta parámetro: " + key);
		try {
			JsonNode node = MAPPER.readTree(args).get(key);
			if (node == null) throw new IllegalArgumentException("Falta parámetro: " + key);
			return node.asText();
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("JSON inválido: " + e.getMessage());
		}
	}
}

