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
@Tag(name = "Blinds", description = "Controls the blinds position (0=closed, 100=open)")
public class BlindsController {

	private final Blinds blinds;

	public BlindsController(Blinds blinds) {
		this.blinds = blinds;
	}

	@GetMapping
	@Operation(summary = "Current state", description = "Returns the current blinds position as a percentage")
	public BlindsState status() {
		return blinds.status();
	}

	@PostMapping("/open")
	@Operation(summary = "Open fully", description = "Raises the blinds to 100%")
	public BlindsState open() {
		return blinds.open();
	}

	@PostMapping("/close")
	@Operation(summary = "Close fully", description = "Lowers the blinds to 0%")
	public BlindsState close() {
		return blinds.close();
	}

	@PostMapping("/position")
	@Operation(summary = "Set position", description = "Sets the blinds position between 0 and 100")
	public BlindsState setPosition(@RequestParam int percent) {
		return blinds.setPosition(percent);
	}
}
