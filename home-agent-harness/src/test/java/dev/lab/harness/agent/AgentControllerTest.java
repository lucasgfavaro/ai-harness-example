package dev.lab.harness.agent;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AgentControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	void dryRunEndpointNeedsNeitherApiKeyNorNetwork() throws Exception {
		mvc.perform(post("/agent")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"request":"apaga la luz del jardin","dryRun":true}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dryRun").value(true))
				.andExpect(jsonPath("$.answer").value(org.hamcrest.Matchers.containsString("sin LLM ni red")));
	}

	@Test
	void exposesAgentEndpointInOpenApiWithoutCallingExternalServices() throws Exception {
		mvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.paths['/agent'].post").exists())
				.andExpect(jsonPath("$.components.schemas.AgentRequest.properties.request").exists())
				.andExpect(jsonPath("$.components.schemas.AgentRequest.properties.dryRun.default").value(true));
	}
}
