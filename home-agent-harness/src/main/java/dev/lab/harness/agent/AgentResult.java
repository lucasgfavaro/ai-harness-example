package dev.lab.harness.agent;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Final result and didactic trace of the agent loop")
public record AgentResult(
		@Schema(description = "Final answer from the model or the dry-run planner (in Spanish, since it is user-facing demo output)",
				example = "DRY-RUN (sin LLM ni red). El loop terminó con la luz simulada apagada.") String answer,
		@Schema(description = "Indicates that neither OpenAI nor the real HTTP tools were used", example = "true") boolean dryRun,
		@ArraySchema(schema = @Schema(description = "An observable step of the loop (in Spanish, since it is user-facing demo output)",
				example = "Tool turn_light_off -> {\"on\":false,\"simulated\":true}")) List<String> steps) {
	public AgentResult {
		steps = List.copyOf(steps);
	}
}
