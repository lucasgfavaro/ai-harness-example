package dev.lab.homeagent.thermostat;

public record ThermostatState(double currentTemperature, double targetTemperature, ThermostatMode mode) {
}

