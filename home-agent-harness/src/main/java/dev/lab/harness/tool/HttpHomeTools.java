package dev.lab.harness.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lab.harness.agent.ToolCall;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Tools reales: HTTP Java hacia el simulador Spring Boot de la casa, para todos los dispositivos. */
public final class HttpHomeTools implements ToolExecutor {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final RestClient client;

	public HttpHomeTools(String baseUrl) {
		this.client = RestClient.create(baseUrl);
	}

	@Override
	public String execute(ToolCall call) {
		try {
			return switch (call.name()) {
				case "get_light_state" -> lightJson(client.get().uri("/light").retrieve().body(LightState.class));
				case "turn_light_on" -> lightJson(client.post().uri("/light/on").retrieve().body(LightState.class));
				case "turn_light_off" -> lightJson(client.post().uri("/light/off").retrieve().body(LightState.class));

				case "get_blinds_state" -> blindsJson(client.get().uri("/blinds").retrieve().body(BlindsState.class));
				case "open_blinds" -> blindsJson(client.post().uri("/blinds/open").retrieve().body(BlindsState.class));
				case "close_blinds" -> blindsJson(client.post().uri("/blinds/close").retrieve().body(BlindsState.class));
				case "set_blinds_position" -> {
					int percent = Integer.parseInt(param(call, "percent"));
					yield blindsJson(client.post().uri(uri -> uri.path("/blinds/position")
							.queryParam("percent", percent).build())
							.retrieve().body(BlindsState.class));
				}

				case "get_lock_state" -> lockJson(client.get().uri("/lock").retrieve().body(LockState.class));
				case "lock_door" -> lockJson(client.post().uri("/lock/lock").retrieve().body(LockState.class));
				case "unlock_door" -> lockJson(client.post().uri("/lock/unlock").retrieve().body(LockState.class));

				case "get_thermostat_state" ->
						thermostatJson(client.get().uri("/thermostat").retrieve().body(ThermostatState.class));
				case "set_thermostat_target" -> {
					double temperature = Double.parseDouble(param(call, "temperature"));
					yield thermostatJson(client.post().uri(uri -> uri.path("/thermostat/target")
							.queryParam("temperature", temperature).build())
							.retrieve().body(ThermostatState.class));
				}
				case "set_thermostat_mode" -> {
					String mode = param(call, "mode");
					yield thermostatJson(client.post().uri(uri -> uri.path("/thermostat/mode")
							.queryParam("mode", mode).build())
							.retrieve().body(ThermostatState.class));
				}

				case "get_home_summary" -> client.get().uri("/home/summary").retrieve().body(String.class);

				default -> throw new IllegalArgumentException("Tool desconocida: " + call.name());
			};
		} catch (RestClientException | IllegalArgumentException exception) {
			return "{\"ok\":false,\"error\":\"" + safe(exception.getMessage()) + "\"}";
		}
	}

	private String lightJson(LightState state) {
		return "{\"on\":" + state.on() + "}";
	}

	private String blindsJson(BlindsState state) {
		return "{\"positionPercent\":" + state.positionPercent() + "}";
	}

	private String lockJson(LockState state) {
		return "{\"locked\":" + state.locked() + "}";
	}

	private String thermostatJson(ThermostatState state) {
		return "{\"currentTemperature\":" + state.currentTemperature()
				+ ",\"targetTemperature\":" + state.targetTemperature()
				+ ",\"mode\":\"" + state.mode() + "\"}";
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

	private String safe(String message) {
		return message == null ? "error sin detalle" : message.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private record LightState(boolean on) {}
	private record BlindsState(int positionPercent) {}
	private record LockState(boolean locked) {}
	private record ThermostatState(double currentTemperature, double targetTemperature, String mode) {}
}

