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

/** Adapter for the official OpenAI SDK. The runtime does not depend on SDK types. */
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
					// fromEnv reads OPENAI_API_KEY; there are never secrets in the repository.
					client = OpenAIOkHttpClient.fromEnv();
				}
			}
		}
		return client;
	}

	private List<FunctionTool> tools() {
		return List.of(
				tool("get_light_state", "Reads whether the garden light is on or off.", Map.of(), List.of()),
				tool("turn_light_on", "Turns on the garden light.", Map.of(), List.of()),
				tool("turn_light_off", "Turns off the garden light.", Map.of(), List.of()),

				tool("get_blinds_state", "Reads the current blinds position as a percentage.", Map.of(), List.of()),
				tool("open_blinds", "Fully opens the blinds (100%).", Map.of(), List.of()),
				tool("close_blinds", "Fully closes the blinds (0%).", Map.of(), List.of()),
				tool("set_blinds_position", "Sets the blinds position between 0 and 100.",
						Map.of("percent", Map.of("type", "integer", "description", "Opening percentage, 0 to 100")),
						List.of("percent")),

				tool("get_lock_state", "Reads whether the door is locked or unlocked.", Map.of(), List.of()),
				tool("lock_door", "Locks the front door.", Map.of(), List.of()),
				tool("unlock_door", "Unlocks the front door.", Map.of(), List.of()),

				tool("get_thermostat_state", "Reads the thermostat's current temperature, target temperature, and mode.", Map.of(), List.of()),
				tool("set_thermostat_target", "Sets the target temperature in degrees Celsius.",
						Map.of("temperature", Map.of("type", "number", "description", "Target temperature in degrees Celsius")),
						List.of("temperature")),
				tool("set_thermostat_mode", "Changes the thermostat's operating mode.",
						Map.of("mode", Map.of("type", "string", "description", "Mode: OFF, HEATING, COOLING, or IDLE")),
						List.of("mode")),

				tool("get_home_summary", "Returns the consolidated state of every device in the home.", Map.of(), List.of()));
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
