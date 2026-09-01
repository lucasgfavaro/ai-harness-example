package dev.lab.harness.tool;

import dev.lab.harness.agent.ToolCall;

public interface ToolExecutor {
	String execute(ToolCall call);
}
