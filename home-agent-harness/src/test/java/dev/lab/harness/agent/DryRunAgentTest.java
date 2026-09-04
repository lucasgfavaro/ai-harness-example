package dev.lab.harness.agent;

import static org.assertj.core.api.Assertions.assertThat;

import dev.lab.harness.tool.DryRunHomeTools;
import org.junit.jupiter.api.Test;

class DryRunAgentTest {

	@Test
	void demonstratesActionVerificationWithoutLlmOrNetwork() {
		AgentLoop loop = new AgentLoop(
				new DryRunModelGateway(), new DryRunHomeTools(), "skill", 5, true);

		AgentResult result = loop.run("encende la luz del jardin");

		assertThat(result.dryRun()).isTrue();
		assertThat(result.answer()).contains("sin LLM ni red", "\"on\":true");
		assertThat(result.steps()).anyMatch(step -> step.contains("turn_light_on"));
		assertThat(result.steps()).anyMatch(step -> step.contains("get_light_state"));
		assertThat(result.steps()).noneMatch(step -> step.contains("la LLM"));
	}
}
