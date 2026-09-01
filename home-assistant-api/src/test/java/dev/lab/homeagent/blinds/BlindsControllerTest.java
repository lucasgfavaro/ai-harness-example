package dev.lab.homeagent.blinds;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BlindsController.class)
@Import(Blinds.class)
class BlindsControllerTest {

	@Autowired
	MockMvc mvc;

	@Test
	void canOpenCloseAndSetPosition() throws Exception {
		mvc.perform(get("/api/blinds"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.positionPercent").value(0));

		mvc.perform(post("/api/blinds/open"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.positionPercent").value(100));

		mvc.perform(post("/api/blinds/position").param("percent", "50"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.positionPercent").value(50));

		mvc.perform(post("/api/blinds/close"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.positionPercent").value(0));
	}
}

