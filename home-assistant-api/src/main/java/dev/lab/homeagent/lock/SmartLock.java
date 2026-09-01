package dev.lab.homeagent.lock;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

@Service
public class SmartLock {

	private final AtomicBoolean locked = new AtomicBoolean(true);

	public LockState status() {
		return new LockState(locked.get());
	}

	public LockState lock() {
		locked.set(true);
		return status();
	}

	public LockState unlock() {
		locked.set(false);
		return status();
	}
}

