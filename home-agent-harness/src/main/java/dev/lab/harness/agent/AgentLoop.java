package dev.lab.harness.agent;

import dev.lab.harness.tool.ToolExecutor;
import java.util.ArrayList;
import java.util.List;

/**
 * Runtime del agente. La lista loopMemory nace y muere dentro de run(): no hay
 * base de datos, conversation id persistido, knowledge base ni memoria global.
 */
public final class AgentLoop {

	private final ModelGateway model;
	private final ToolExecutor tools;
	private final String instructions;
	private final int maxTurns;
	private final boolean dryRun;

	public AgentLoop(ModelGateway model, ToolExecutor tools, String instructions, int maxTurns, boolean dryRun) {
		this.model = model;
		this.tools = tools;
		this.instructions = instructions;
		this.maxTurns = maxTurns;
		this.dryRun = dryRun;
	}

	public AgentResult run(String request) {
		List<ConversationItem> loopMemory = new ArrayList<>();
		List<String> steps = new ArrayList<>();

		for (int turnNumber = 1; turnNumber <= maxTurns; turnNumber++) {
			ModelTurn turn = model.respond(instructions, request, List.copyOf(loopMemory));
			String actor = dryRun ? "el planificador determinista" : "la LLM";
			steps.add("Turno " + turnNumber + ": " +
					(turn.toolCalls().isEmpty() ? "respuesta final" : actor + " pidio " + turn.toolCalls().size() + " tool(s)"));

			if (turn.toolCalls().isEmpty()) {
				if (turn.text().isBlank()) {
					throw new IllegalStateException("El modelo no devolvio texto ni tool calls");
				}
				return new AgentResult(turn.text(), dryRun, steps);
			}

			for (ToolCall call : turn.toolCalls()) {
				loopMemory.add(new ConversationItem.RequestedTool(call));
				String output = tools.execute(call);
				loopMemory.add(new ConversationItem.ReturnedTool(call.callId(), output));
				steps.add("Tool " + call.name() + " -> " + output);
			}
		}

		throw new IllegalStateException("El agent loop supero el limite de " + maxTurns + " turnos");
	}
}
