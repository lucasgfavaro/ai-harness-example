package dev.lab.homeagent.thermostat;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

@Service
public class Thermostat {

	private final AtomicReference<Double> targetTemperature = new AtomicReference<>(20.0);
	private final AtomicReference<Double> currentTemperature = new AtomicReference<>(18.0);
	private final AtomicReference<ThermostatMode> mode = new AtomicReference<>(ThermostatMode.OFF);

	public ThermostatState status() {
		return new ThermostatState(currentTemperature.get(), targetTemperature.get(), mode.get());
	}

	public ThermostatState setTarget(double temperature) {
		targetTemperature.set(temperature);
		updateMode();
		return status();
	}

	public ThermostatState setMode(ThermostatMode newMode) {
		mode.set(newMode);
		return status();
	}

	// Simulates a current temperature reading
	public ThermostatState updateCurrentTemperature(double temperature) {
		currentTemperature.set(temperature);
		updateMode();
		return status();
	}

	private void updateMode() {
		if (mode.get() == ThermostatMode.OFF) return;
		double current = currentTemperature.get();
		double target = targetTemperature.get();
		if (current < target - 0.5) {
			mode.set(ThermostatMode.HEATING);
		} else if (current > target + 0.5) {
			mode.set(ThermostatMode.COOLING);
		} else {
			mode.set(ThermostatMode.IDLE);
		}
	}
}

