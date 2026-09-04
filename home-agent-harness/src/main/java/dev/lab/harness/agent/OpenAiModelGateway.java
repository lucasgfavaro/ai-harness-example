package dev.lab.harness.agent;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.responses.FunctionTool;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseFunctionToolCall;
import com.openai.models.responses.ResponseInputItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Adaptador del SDK oficial de OpenAI. El runtime no depende de tipos del SDK. */
public final class OpenAiModelGateway implements ModelGateway {

	private final String model;
	private volatile OpenAIClient client;

	public OpenAiModelGateway(String model) {
		this.model = model;
	}

	@Override
	public ModelTurn respond(String instructions, String userRequest, List<ConversationItem> loopMemory) {
		List<ResponseInputItem> input = new ArrayList<>();
		input.add(ResponseInputItem.ofMessage(ResponseInputItem.Message.builder()
				.role(ResponseInputItem.Message.Role.USER)
				.addInputTextContent(userRequest)
				.build()));

		for (ConversationItem item : loopMemory) {
			if (item instanceof ConversationItem.RequestedTool requested) {
				ToolCall call = requested.call();
				input.add(ResponseInputItem.ofFunctionCall(ResponseFunctionToolCall.builder()
						.callId(call.callId())
						.name(call.name())
						.arguments(call.arguments())
						.build()));
			} else if (item instanceof ConversationItem.ReturnedTool returned) {
				input.add(ResponseInputItem.ofFunctionCallOutput(ResponseInputItem.FunctionCallOutput.builder()
						.callId(returned.callId())
						.output(returned.output())
						.build()));
			}
		}

		ResponseCreateParams.Builder params = ResponseCreateParams.builder()
				.model(model)
				.instructions(instructions)
				.input(ResponseCreateParams.Input.ofResponse(input))
				.store(false)
				.maxOutputTokens(600);
		tools().forEach(params::addTool);

		Response response = client().responses().create(params.build());
		List<ToolCall> calls = response.output().stream()
				.filter(item -> item.isFunctionCall())
				.map(item -> item.asFunctionCall())
				.map(call -> new ToolCall(call.callId(), call.name(), call.arguments()))
				.toList();
		String text = response.output().stream()
				.flatMap(item -> item.message().stream())
				.flatMap(message -> message.content().stream())
				.flatMap(content -> content.outputText().stream())
				.map(output -> output.text())
				.reduce("", (left, right) -> left + right);
		return new ModelTurn(text, calls);
	}

	private OpenAIClient client() {
		if (client == null) {
			synchronized (this) {
				if (client == null) {
					// fromEnv lee OPENAI_API_KEY; nunca hay secretos en el repositorio.
					client = OpenAIOkHttpClient.fromEnv();
				}
			}
		}
		return client;
	}

	private List<FunctionTool> tools() {
		return List.of(
				tool("get_light_state", "Lee si la luz del jardin esta encendida o apagada.", Map.of(), List.of()),
				tool("turn_light_on", "Enciende la luz del jardin.", Map.of(), List.of()),
				tool("turn_light_off", "Apaga la luz del jardin.", Map.of(), List.of()),

				tool("get_blinds_state", "Lee la posicion actual de las persianas en porcentaje.", Map.of(), List.of()),
				tool("open_blinds", "Abre completamente las persianas (100%).", Map.of(), List.of()),
				tool("close_blinds", "Cierra completamente las persianas (0%).", Map.of(), List.of()),
				tool("set_blinds_position", "Establece la posicion de las persianas entre 0 y 100.",
						Map.of("percent", Map.of("type", "integer", "description", "Porcentaje de apertura, 0 a 100")),
						List.of("percent")),

				tool("get_lock_state", "Lee si la puerta esta bloqueada o desbloqueada.", Map.of(), List.of()),
				tool("lock_door", "Bloquea la puerta principal.", Map.of(), List.of()),
				tool("unlock_door", "Desbloquea la puerta principal.", Map.of(), List.of()),

				tool("get_thermostat_state", "Lee temperatura actual, objetivo y modo del termostato.", Map.of(), List.of()),
				tool("set_thermostat_target", "Establece la temperatura objetivo en grados Celsius.",
						Map.of("temperature", Map.of("type", "number", "description", "Temperatura objetivo en grados Celsius")),
						List.of("temperature")),
				tool("set_thermostat_mode", "Cambia el modo de operacion del termostato.",
						Map.of("mode", Map.of("type", "string", "description", "Modo: OFF, HEATING, COOLING o IDLE")),
						List.of("mode")),

				tool("get_home_summary", "Devuelve el estado consolidado de todos los dispositivos del hogar.", Map.of(), List.of()));
	}

	private FunctionTool tool(String name, String description, Map<String, Object> properties, List<String> required) {
		return FunctionTool.builder()
				.name(name)
				.description(description)
				.strict(false)
				.parameters(FunctionTool.Parameters.builder()
						.putAdditionalProperty("type", JsonValue.from("object"))
						.putAdditionalProperty("properties", JsonValue.from(properties))
						.putAdditionalProperty("required", JsonValue.from(required))
						.putAdditionalProperty("additionalProperties", JsonValue.from(false))
						.build())
				.build();
	}
}
