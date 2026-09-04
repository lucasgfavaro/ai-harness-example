package dev.lab.homeagent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

	@Bean
	OpenAPI homeAssistantOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Home Assistant API")
						.description("API to control the simulated home devices: " +
								"garden light, blinds, smart lock, and thermostat.")
						.version("0.0.1"));
	}
}

