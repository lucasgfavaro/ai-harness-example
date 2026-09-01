package dev.lab.harness.agent;

import static org.assertj.core.api.Assertions.assertThat;

import dev.lab.harness.tool.ToolExecutor;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgentLoopTest {

	@Test
	void executesRequestedToolAndReturnsItsResultToTheModel() {
		List<List<ConversationItem>> memoriesSeen = new ArrayList<>();
		ModelGateway fakeModel = (instructions, request, memory) -> {
			memoriesSeen.add(memory);
			if (memory.isEmpty()) {
				return new ModelTurn("", List.of(new ToolCall("call-1", "turn_light_off", "{}")));
			}
			return new ModelTurn("La luz quedo apagada.", List.of());
		};
		ToolExecutor fakeTool = call -> "{\"on\":false}";

		AgentResult result = new AgentLoop(fakeModel, fakeTool, "skill de prueba", 4, false)
				.run("apaga la luz");

		assertThat(result.answer()).isEqualTo("La luz quedo apagada.");
		assertThat(memoriesSeen).hasSize(2);
		assertThat(memoriesSeen.get(0)).isEmpty();
		assertThat(memoriesSeen.get(1)).containsExactly(
				new ConversationItem.RequestedTool(new ToolCall("call-1", "turn_light_off", "{}")),
				new ConversationItem.ReturnedTool("call-1", "{\"on\":false}"));
	}

	@Test
	void memoryStartsEmptyAgainForEveryRequest() {
		List<Integer> initialMemorySizes = new ArrayList<>();
		ModelGateway fakeModel = (instructions, request, memory) -> {
			initialMemorySizes.add(memory.size());
			return new ModelTurn("ok", List.of());
		};
		AgentLoop loop = new AgentLoop(fakeModel, call -> "unused", "skill", 2, false);

		loop.run("uno");
		loop.run("dos");

		assertThat(initialMemorySizes).containsExactly(0, 0);
	}
}
