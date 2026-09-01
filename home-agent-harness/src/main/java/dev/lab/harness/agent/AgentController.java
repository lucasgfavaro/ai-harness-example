package dev.lab.harness.agent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/agent")
@Tag(name = "Agent", description = "Ejecuta el agent loop de la luz del jardín")
public class AgentController {

	private final AgentService service;

	public AgentController(AgentService service) {
		this.service = service;
	}

	@PostMapping
	@Operation(
			summary = "Pedirle una acción al agente",
			description = "Con dryRun=true muestra el loop sin OpenAI ni red. Con dryRun=false usa la LLM y las tools HTTP del simulador.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "El loop terminó con una respuesta final",
				content = @Content(schema = @Schema(implementation = AgentResult.class))),
		@ApiResponse(responseCode = "400", description = "Falta el campo request o está vacío", content = @Content),
		@ApiResponse(responseCode = "500", description = "Falló la LLM, una tool o se alcanzó el límite de turnos", content = @Content)
	})
	public AgentResult run(@RequestBody AgentRequest body) {
		if (body.request() == null || body.request().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request es obligatorio");
		}
		return service.run(body.request(), body.dryRun());
	}

	@Schema(name = "AgentRequest", description = "Pedido de una sola ejecución; no se conserva memoria entre requests")
	public record AgentRequest(
			@Schema(description = "Pedido en lenguaje natural", example = "apagá la luz del jardín") String request,
			@Schema(description = "Simula el loop sin LLM ni llamadas HTTP", example = "true", defaultValue = "true") boolean dryRun) {}
}
