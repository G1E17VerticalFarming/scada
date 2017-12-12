package PLCCommunication;

/**
 * List of PLC commands
 *
 * @author Steffen Skov
 */
public interface ICommands {
    byte NO_CMD = 0;
    byte TEMP_SETPOINT = 1;
    byte MOIST_SETPOINT = 2;
    byte REDLIGHT_SETPOINT = 3;
    byte BLUELIGHT_SETPOINT = 4;
    byte START_WATER_PUMP = 5;//Not implemented
    byte ADDWATER = 6;
    byte ADDFERTILISER = 7; // Not implemented
    byte ADDCO2 = 8; // Not implemented
    byte READ_GREENHOUSE_TEMP = 9;
    byte READ_OUTDOOR_TEMP = 10;
    byte READ_MOISTURE = 11;
    byte READ_PLANT_HEIGHT = 12;
    byte READ_ALL_ALARMS = 13;
    byte RESET_ALARMS = 14;
    byte GET_STATUS = 15;
    byte SET_FAN_SPEED = 16;
    byte READ_WATER_LEVEL = 17;

    // Acknowledge/answer to commands
    // PLC add a bit to the command: command + 64 (~0x40) (~0100 0000)
    byte TEMP_SETPOINT_ACK = TEMP_SETPOINT + 0x40;
    byte MOIST_SETPOINT_ACK = 66;
    byte REDLIGHT_SETPOINT_ACK = 67;
    byte BLUELIGHT_SETPOINT_ACK = 68;
    byte START_WATER_PUMP_ACK = 69;
    byte ADDWATER_ACK = 70;
    byte ADDFERTILISER_ACK = 71;
    byte ADDCO2_ACK = 72;
    byte READ_GREENHOUSE_TEMP_ACK = 73;
    byte READ_OUTDOOR_TEMP_ACK = 74;
    byte READ_MOISTURE_ACK = 75;
    byte READ_PLANT_HEIGHT_ACK = 76;
    byte READ_ALL_ALARMS_ACK = 77;
    byte RESET_ALARMS_ACK = 78;
    byte GET_STATUS_ACK = 79;
    byte SET_FAN_SPEED_ACK = 80;
    byte READ_WATER_LEVEL_ACK = 81;


}
