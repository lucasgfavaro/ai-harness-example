package dev.lab.harness.agent;

import dev.lab.harness.tool.ToolExecutor;
import java.util.ArrayList;
import java.util.List;

/**
 * Runtime of the agent. The loopMemory list is born and dies inside run(): there is
 * no database, persisted conversation id, knowledge base, or global memory.
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
					throw new IllegalStateException("The model returned neither text nor tool calls");
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

		throw new IllegalStateException("The agent loop exceeded the limit of " + maxTurns + " turns");
	}
}
