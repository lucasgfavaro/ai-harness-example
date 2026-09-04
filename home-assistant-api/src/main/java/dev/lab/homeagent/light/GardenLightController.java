package dev.lab.homeagent.light;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/light")
@Tag(name = "Garden Light", description = "Controls turning the outdoor light on and off")
public class GardenLightController {

	private final GardenLight light;

	public GardenLightController(GardenLight light) {
		this.light = light;
	}

	@GetMapping
	@Operation(summary = "Current state", description = "Returns whether the light is on or off")
	public LightState status() {
		return light.status();
	}

	@PostMapping("/on")
	@Operation(summary = "Turn on", description = "Turns on the garden light")
	public LightState turnOn() {
		return light.turnOn();
	}

	@PostMapping("/off")
	@Operation(summary = "Turn off", description = "Turns off the garden light")
	public LightState turnOff() {
		return light.turnOff();
	}
}
