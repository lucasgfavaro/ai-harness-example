package dev.lab.harness.agent;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Resultado final y traza didáctica del agent loop")
public record AgentResult(
		@Schema(description = "Respuesta final del modelo o del planificador dry-run",
				example = "DRY-RUN (sin LLM ni red). El loop terminó con la luz simulada apagada.") String answer,
		@Schema(description = "Indica que no se usaron OpenAI ni las tools HTTP reales", example = "true") boolean dryRun,
		@ArraySchema(schema = @Schema(description = "Un paso observable del loop",
				example = "Tool turn_light_off -> {\"on\":false,\"simulated\":true}")) List<String> steps) {
	public AgentResult {
		steps = List.copyOf(steps);
	}
}
