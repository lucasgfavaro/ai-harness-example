package dev.lab.harness.agent;

public sealed interface ConversationItem permits ConversationItem.RequestedTool, ConversationItem.ReturnedTool {

	record RequestedTool(ToolCall call) implements ConversationItem {}

	record ReturnedTool(String callId, String output) implements ConversationItem {}
}
