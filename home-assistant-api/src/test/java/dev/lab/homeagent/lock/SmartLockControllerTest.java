package dev.lab.homeagent.lock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SmartLockController.class)
@Import(SmartLock.class)
class SmartLockControllerTest {

	@Autowired
	MockMvc mvc;

	@Test
	void canLockAndUnlock() throws Exception {
		mvc.perform(get("/api/lock"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.locked").value(true));

		mvc.perform(post("/api/lock/unlock"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.locked").value(false));

		mvc.perform(post("/api/lock/lock"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.locked").value(true));
	}
}

