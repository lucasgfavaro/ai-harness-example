package dev.lab.harness.agent;

import dev.lab.harness.tool.DryRunLightTools;
import dev.lab.harness.tool.ToolExecutor;

public final class AgentService {

	private final ModelGateway openAi;
	private final ToolExecutor httpTools;
	private final String skill;
	private final int maxTurns;

	public AgentService(ModelGateway openAi, ToolExecutor httpTools, String skill, int maxTurns) {
		this.openAi = openAi;
		this.httpTools = httpTools;
		this.skill = skill;
		this.maxTurns = maxTurns;
	}

	public AgentResult run(String request, boolean dryRun) {
		if (dryRun) {
			return new AgentLoop(new DryRunModelGateway(), new DryRunLightTools(), skill, maxTurns, true).run(request);
		}
		return new AgentLoop(openAi, httpTools, skill, maxTurns, false).run(request);
	}
}
