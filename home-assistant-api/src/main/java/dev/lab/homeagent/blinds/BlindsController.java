package dev.lab.homeagent.blinds;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blinds")
@Tag(name = "Persianas", description = "Controla la posición de las persianas (0=cerradas, 100=abiertas)")
public class BlindsController {

	private final Blinds blinds;

	public BlindsController(Blinds blinds) {
		this.blinds = blinds;
	}

	@GetMapping
	@Operation(summary = "Estado actual", description = "Devuelve la posición actual de las persianas en porcentaje")
	public BlindsState status() {
		return blinds.status();
	}

	@PostMapping("/open")
	@Operation(summary = "Abrir completamente", description = "Sube las persianas al 100%")
	public BlindsState open() {
		return blinds.open();
	}

	@PostMapping("/close")
	@Operation(summary = "Cerrar completamente", description = "Baja las persianas al 0%")
	public BlindsState close() {
		return blinds.close();
	}

	@PostMapping("/position")
	@Operation(summary = "Fijar posición", description = "Establece la posición de las persianas entre 0 y 100")
	public BlindsState setPosition(@RequestParam int percent) {
		return blinds.setPosition(percent);
	}
}
