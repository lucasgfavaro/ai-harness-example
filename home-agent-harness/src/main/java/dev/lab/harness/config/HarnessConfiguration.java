package dev.lab.harness.config;

import dev.lab.harness.agent.AgentService;
import dev.lab.harness.agent.ModelGateway;
import dev.lab.harness.agent.OpenAiModelGateway;
import dev.lab.harness.agent.SkillLoader;
import dev.lab.harness.tool.HttpLightTools;
import dev.lab.harness.tool.ToolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HarnessConfiguration {

	@Bean
	ModelGateway openAiModelGateway(@Value("${openai.model}") String model) {
		return new OpenAiModelGateway(model);
	}

	@Bean
	ToolExecutor httpLightTools(@Value("${home.light.base-url}") String baseUrl) {
		return new HttpLightTools(baseUrl);
	}

	@Bean
	AgentService agentService(ModelGateway model, ToolExecutor tools,
			@Value("${agent.max-turns}") int maxTurns) {
		String skill = SkillLoader.load("skills/garden-light.md");
		return new AgentService(model, tools, skill, maxTurns);
	}
}
