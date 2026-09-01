package dev.lab.homeagent.thermostat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ThermostatController.class)
@Import(Thermostat.class)
class ThermostatControllerTest {

	@Autowired
	MockMvc mvc;

	@Test
	void canQueryAndSetTarget() throws Exception {
		mvc.perform(get("/api/thermostat"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.targetTemperature").value(20.0))
				.andExpect(jsonPath("$.mode").value("OFF"));

		mvc.perform(post("/api/thermostat/target").param("temperature", "22.0"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.targetTemperature").value(22.0));

		mvc.perform(post("/api/thermostat/mode").param("mode", "HEATING"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.mode").value("HEATING"));
	}
}

