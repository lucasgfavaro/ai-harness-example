package dev.lab.harness.agent;

import java.util.List;

/** Boundary between the runtime and an LLM (or the didactic dry-run planner). */
public interface ModelGateway {
	ModelTurn respond(String instructions, String userRequest, List<ConversationItem> loopMemory);
}
