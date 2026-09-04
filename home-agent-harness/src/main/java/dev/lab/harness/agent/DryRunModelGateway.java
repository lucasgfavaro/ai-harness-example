package dev.lab.harness.agent;

import java.util.List;
import java.util.Locale;

/** Deterministic demo rule. It is not an LLM and does not call OpenAI. */
public final class DryRunModelGateway implements ModelGateway {

	@Override
	public ModelTurn respond(String instructions, String userRequest, List<ConversationItem> memory) {
		if (memory.isEmpty()) {
			String normalized = userRequest.toLowerCase(Locale.ROOT);
			String tool = normalized.contains("apag") ? "turn_light_off"
					: normalized.contains("encend") || normalized.contains("prend") ? "turn_light_on"
					: "get_light_state";
			return call(tool, "dry-1");
		}

		ConversationItem.RequestedTool lastCall = lastRequested(memory);
		long returnedTools = memory.stream().filter(ConversationItem.ReturnedTool.class::isInstance).count();
		if (returnedTools == 1 && !lastCall.call().name().equals("get_light_state")) {
			return call("get_light_state", "dry-2");
		}

		String lastOutput = memory.stream()
				.filter(ConversationItem.ReturnedTool.class::isInstance)
				.map(ConversationItem.ReturnedTool.class::cast)
				.reduce((first, second) -> second)
				.orElseThrow()
				.output();
		return new ModelTurn("DRY-RUN (sin LLM ni red). El loop terminaria con el estado simulado: " + lastOutput,
				List.of());
	}

	private ModelTurn call(String name, String id) {
		return new ModelTurn("", List.of(new ToolCall(id, name, "{}")));
	}

	private ConversationItem.RequestedTool lastRequested(List<ConversationItem> memory) {
		return memory.stream()
				.filter(ConversationItem.RequestedTool.class::isInstance)
				.map(ConversationItem.RequestedTool.class::cast)
				.reduce((first, second) -> second)
				.orElseThrow();
	}
}
