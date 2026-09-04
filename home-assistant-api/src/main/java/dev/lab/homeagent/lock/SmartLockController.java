package dev.lab.homeagent.lock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lock")
@Tag(name = "Smart Lock", description = "Controls locking and unlocking the front door")
public class SmartLockController {

	private final SmartLock lock;

	public SmartLockController(SmartLock lock) {
		this.lock = lock;
	}

	@GetMapping
	@Operation(summary = "Current state", description = "Returns whether the door is locked or unlocked")
	public LockState status() {
		return lock.status();
	}

	@PostMapping("/lock")
	@Operation(summary = "Lock", description = "Locks the front door")
	public LockState lock() {
		return lock.lock();
	}

	@PostMapping("/unlock")
	@Operation(summary = "Unlock", description = "Unlocks the front door")
	public LockState unlock() {
		return lock.unlock();
	}
}
