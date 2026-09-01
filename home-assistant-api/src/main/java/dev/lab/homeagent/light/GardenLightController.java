package dev.lab.homeagent.light;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/light")
@Tag(name = "Luz del jardín", description = "Controla el encendido y apagado de la luz exterior")
public class GardenLightController {

	private final GardenLight light;

	public GardenLightController(GardenLight light) {
		this.light = light;
	}

	@GetMapping
	@Operation(summary = "Estado actual", description = "Devuelve si la luz está encendida o apagada")
	public LightState status() {
		return light.status();
	}

	@PostMapping("/on")
	@Operation(summary = "Encender", description = "Enciende la luz del jardín")
	public LightState turnOn() {
		return light.turnOn();
	}

	@PostMapping("/off")
	@Operation(summary = "Apagar", description = "Apaga la luz del jardín")
	public LightState turnOff() {
		return light.turnOff();
	}
}
