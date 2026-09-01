package dev.lab.harness.agent;

import java.util.List;

/** Limite entre el runtime y una LLM (o el planificador didactico de dry-run). */
public interface ModelGateway {
	ModelTurn respond(String instructions, String userRequest, List<ConversationItem> loopMemory);
}
