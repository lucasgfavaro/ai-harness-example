export interface LightState {
  on: boolean;
}

export interface BlindsState {
  positionPercent: number;
}

export interface LockState {
  locked: boolean;
}

export type ThermostatMode = 'OFF' | 'HEATING' | 'COOLING' | 'IDLE';

export interface ThermostatState {
  currentTemperature: number;
  targetTemperature: number;
  mode: ThermostatMode;
}

export interface HomeSummary {
  gardenLight: LightState;
  blinds: BlindsState;
  lock: LockState;
  thermostat: ThermostatState;
}

