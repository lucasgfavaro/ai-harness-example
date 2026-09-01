package dev.lab.homeagent.lock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lock")
@Tag(name = "Cerradura inteligente", description = "Controla el bloqueo y desbloqueo de la puerta principal")
public class SmartLockController {

	private final SmartLock lock;

	public SmartLockController(SmartLock lock) {
		this.lock = lock;
	}

	@GetMapping
	@Operation(summary = "Estado actual", description = "Devuelve si la puerta está bloqueada o desbloqueada")
	public LockState status() {
		return lock.status();
	}

	@PostMapping("/lock")
	@Operation(summary = "Bloquear", description = "Bloquea la puerta principal")
	public LockState lock() {
		return lock.lock();
	}

	@PostMapping("/unlock")
	@Operation(summary = "Desbloquear", description = "Desbloquea la puerta principal")
	public LockState unlock() {
		return lock.unlock();
	}
}
