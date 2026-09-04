package dev.lab.harness.agent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/agent")
@CrossOrigin(origins = {"http://localhost:4200"})
@Tag(name = "Agent", description = "Runs the home agent loop")
public class AgentController {

	private final AgentService service;

	public AgentController(AgentService service) {
		this.service = service;
	}

	@PostMapping
	@Operation(
			summary = "Ask the agent to perform an action",
			description = "With dryRun=true it shows the loop without OpenAI or network calls. With dryRun=false it uses the LLM and the simulator's HTTP tools.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "The loop finished with a final answer",
				content = @Content(schema = @Schema(implementation = AgentResult.class))),
		@ApiResponse(responseCode = "400", description = "The request field is missing or blank", content = @Content),
		@ApiResponse(responseCode = "500", description = "The LLM or a tool failed, or the turn limit was reached", content = @Content)
	})
	public AgentResult run(@RequestBody AgentRequest body) {
		if (body.request() == null || body.request().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
		}
		return service.run(body.request(), body.dryRun());
	}

	@Schema(name = "AgentRequest", description = "A single-run request; no memory is kept between requests")
	public record AgentRequest(
			@Schema(description = "Request in natural language", example = "turn off the garden light") String request,
			@Schema(description = "Simulates the loop without an LLM or HTTP calls", example = "true", defaultValue = "true") boolean dryRun) {}
}
