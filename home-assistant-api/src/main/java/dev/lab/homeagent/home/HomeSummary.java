package dev.lab.homeagent.home;

import dev.lab.homeagent.blinds.BlindsState;
import dev.lab.homeagent.light.LightState;
import dev.lab.homeagent.lock.LockState;
import dev.lab.homeagent.thermostat.ThermostatState;

public record HomeSummary(
		LightState gardenLight,
		BlindsState blinds,
		LockState lock,
		ThermostatState thermostat
) {}

