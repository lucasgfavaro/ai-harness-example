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
@Tag(name = "Termostato", description = "Controla la temperatura objetivo y el modo de operación del termostato")
public class ThermostatController {

	private final Thermostat thermostat;

	public ThermostatController(Thermostat thermostat) {
		this.thermostat = thermostat;
	}

	@GetMapping
	@Operation(summary = "Estado actual", description = "Devuelve temperatura actual, objetivo y modo de operación")
	public ThermostatState status() {
		return thermostat.status();
	}

	@PostMapping("/target")
	@Operation(summary = "Cambiar temperatura objetivo", description = "Establece la temperatura deseada en grados Celsius")
	public ThermostatState setTarget(@RequestParam double temperature) {
		return thermostat.setTarget(temperature);
	}

	@PostMapping("/mode")
	@Operation(summary = "Cambiar modo", description = "Cambia el modo de operación: OFF, HEATING, COOLING o IDLE")
	public ThermostatState setMode(@RequestParam ThermostatMode mode) {
		return thermostat.setMode(mode);
	}

	@PostMapping("/current")
	@Operation(summary = "Actualizar temperatura actual", description = "Informa la temperatura ambiente medida actualmente")
	public ThermostatState updateCurrentTemperature(@RequestParam double temperature) {
		return thermostat.updateCurrentTemperature(temperature);
	}
}
