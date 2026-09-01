package dev.lab.harness.tool;

import dev.lab.harness.agent.ToolCall;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Las tools reales: HTTP Java hacia el simulador Spring Boot de la casa. */
public final class HttpLightTools implements ToolExecutor {

	private final RestClient client;

	public HttpLightTools(String baseUrl) {
		this.client = RestClient.create(baseUrl);
	}

	@Override
	public String execute(ToolCall call) {
		try {
			LightState state = switch (call.name()) {
				case "get_light_state" -> client.get().retrieve().body(LightState.class);
				case "turn_light_on" -> client.post().uri("/on").retrieve().body(LightState.class);
				case "turn_light_off" -> client.post().uri("/off").retrieve().body(LightState.class);
				default -> throw new IllegalArgumentException("Tool desconocida: " + call.name());
			};
			return "{\"on\":" + state.on() + "}";
		} catch (RestClientException | IllegalArgumentException exception) {
			return "{\"ok\":false,\"error\":\"" + safe(exception.getMessage()) + "\"}";
		}
	}

	private String safe(String message) {
		return message == null ? "error sin detalle" : message.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private record LightState(boolean on) {}
}
