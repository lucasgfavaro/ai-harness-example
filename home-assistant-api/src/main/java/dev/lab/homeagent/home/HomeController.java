package dev.lab.homeagent.home;

import dev.lab.homeagent.blinds.Blinds;
import dev.lab.homeagent.light.GardenLight;
import dev.lab.homeagent.lock.SmartLock;
import dev.lab.homeagent.thermostat.Thermostat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@Tag(name = "Home Summary", description = "Consolidated state of every device in a single call")
public class HomeController {

	private final GardenLight light;
	private final Blinds blinds;
	private final SmartLock lock;
	private final Thermostat thermostat;

	public HomeController(GardenLight light, Blinds blinds, SmartLock lock, Thermostat thermostat) {
		this.light = light;
		this.blinds = blinds;
		this.lock = lock;
		this.thermostat = thermostat;
	}

	@GetMapping("/summary")
	@Operation(summary = "Full summary", description = "Returns the current state of the light, blinds, lock, and thermostat")
	public HomeSummary summary() {
		return new HomeSummary(
				light.status(),
				blinds.status(),
				lock.status(),
				thermostat.status()
		);
	}
}
