package dev.lab.homeagent.light;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

@Service
public class GardenLight {

	private final AtomicBoolean on = new AtomicBoolean(false);

	public LightState status() {
		return new LightState(on.get());
	}

	public LightState turnOn() {
		on.set(true);
		return status();
	}

	public LightState turnOff() {
		on.set(false);
		return status();
	}
}

