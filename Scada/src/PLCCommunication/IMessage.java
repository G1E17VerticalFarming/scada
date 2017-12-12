package PLCCommunication;

/**
 * Protocol structure
 *
 * @author Steffen Skov
 */
public interface IMessage {
    // direction defination
    byte TOPLC = 0;
    byte FROMPLC = 1;

    //Protocol
    int COMMAND = 0;
    int DIRECTION = 1;
    int SERIAL_NO = 2;
    int SIZE = 3;  // Size of data
    // Timestamp to sec. precision
    int YEAR = 4;
    int MONTH = 5;
    int DAY = 6;
    int HOUR = 7;
    int MINUTE = 8;
    int SECOND = 9;
    // Data
    int DATA_START = 10;
    int MAX_DATA = 100 + DATA_START;


}
