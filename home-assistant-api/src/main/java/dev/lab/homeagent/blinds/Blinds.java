package dev.lab.homeagent.blinds;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class Blinds {

	// position in percent: 0 = closed, 100 = open
	private final AtomicInteger position = new AtomicInteger(0);

	public BlindsState status() {
		return new BlindsState(position.get());
	}

	public BlindsState open() {
		position.set(100);
		return status();
	}

	public BlindsState close() {
		position.set(0);
		return status();
	}

	public BlindsState setPosition(int percent) {
		int clamped = Math.max(0, Math.min(100, percent));
		position.set(clamped);
		return status();
	}
}

