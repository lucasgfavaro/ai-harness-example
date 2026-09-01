package dev.lab.homeagent.light;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GardenLightController.class)
@Import(GardenLight.class)
class GardenLightControllerTest {

	@Autowired
	MockMvc mvc;

	@Test
	void canQueryTurnOnAndTurnOff() throws Exception {
		mvc.perform(get("/api/light"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.on").value(false));

		mvc.perform(post("/api/light/on"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.on").value(true));

		mvc.perform(post("/api/light/off"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.on").value(false));
	}
}

