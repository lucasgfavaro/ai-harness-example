package dev.lab.harness.agent;

import java.util.List;

public record ModelTurn(String text, List<ToolCall> toolCalls) {

	public ModelTurn {
		text = text == null ? "" : text;
		toolCalls = List.copyOf(toolCalls);
	}
}
