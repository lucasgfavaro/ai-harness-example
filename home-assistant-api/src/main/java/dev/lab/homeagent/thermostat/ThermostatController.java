package dev.lab.homeagent.thermostat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/thermostat")
@Tag(name = "Thermostat", description = "Controls the thermostat's target temperature and operating mode")
public class ThermostatController {

	private final Thermostat thermostat;

	public ThermostatController(Thermostat thermostat) {
		this.thermostat = thermostat;
	}

	@GetMapping
	@Operation(summary = "Current state", description = "Returns the current temperature, target temperature, and operating mode")
	public ThermostatState status() {
		return thermostat.status();
	}

	@PostMapping("/target")
	@Operation(summary = "Change target temperature", description = "Sets the desired temperature in degrees Celsius")
	public ThermostatState setTarget(@RequestParam double temperature) {
		return thermostat.setTarget(temperature);
	}

	@PostMapping("/mode")
	@Operation(summary = "Change mode", description = "Changes the operating mode: OFF, HEATING, COOLING, or IDLE")
	public ThermostatState setMode(@RequestParam ThermostatMode mode) {
		return thermostat.setMode(mode);
	}

	@PostMapping("/current")
	@Operation(summary = "Update current temperature", description = "Reports the currently measured ambient temperature")
	public ThermostatState updateCurrentTemperature(@RequestParam double temperature) {
		return thermostat.updateCurrentTemperature(temperature);
	}
}
