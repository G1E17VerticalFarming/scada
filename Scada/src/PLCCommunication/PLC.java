/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PLCCommunication;

import API.IGreenhouse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * API to communicate to the PLC
 *
 * @author Steffen Skov
 */
public class PLC implements IGreenhouse, ICommands, Serializable {
    private static final long serialVersionUID = 1L;
    transient private UDPConnection conn;
    transient private Message mess;
    private String ipaddress;
    private int portnumber;
    transient private int status;
    private String fileName = "Scada/src/resources/PLClist.dat";


    /**
     * Create greenhouse API
     */
    public PLC() {

    }

    /**
     * Create greenhouse API
     *
     * @param c connection
     */
    public PLC(UDPConnection c) {
        this.conn = c;
    }


    /**
     * Setpoint for temperature inside PLC
     * CMD: 1
     *
     * @param temp : temperature in celsius (0 < T > 30)
     * @return true if processed
     */

    public boolean SetTemperature(int temp) {
        int kelvin = temp + 273;
        mess = new Message(TEMP_SETPOINT);
        if (kelvin >= 273 && kelvin <= 303) // 0 - 30 grader celcius
        {
            System.out.println("Set temperatur setpoint to " + kelvin);
            mess.setData(kelvin - 273);
            conn.addMessage(mess);
            return conn.send();
        }
        return false;
    }

    /**
     * Setpoint for moisture inside PLC
     * CMD:2
     *
     * @param moist in % ( 10 > M > 90 )
     * @return true if processed
     */
    public boolean SetMoisture(int moist) {
        mess = new Message(MOIST_SETPOINT);
        if (moist > 10 && moist < 90) {
            mess.setData(moist);
            conn.addMessage(mess);
            return conn.send();
        }
        return false;
    }

    /**
     * Setpoint for red light inside PLC
     * CMD:3
     *
     * @param level in percent
     * @return true if processed
     */
    public boolean SetRedLight(int level) {
        System.out.println("Set red light to " + level);
        mess = new Message(REDLIGHT_SETPOINT);
        if (level >= 0 && level <= 100) {
            mess.setData(level);
            conn.addMessage(mess);
            return conn.send();
        }
        return false;
    }

    /**
     * Setpoint for blue light inside PLC
     * CMD: 4
     *
     * @param level in percent
     * @return true if processed
     */
    public boolean SetBlueLight(int level) {
        mess = new Message(BLUELIGHT_SETPOINT);
        if (level >= 0 && level <= 100) {
            mess.setData(level);
            conn.addMessage(mess);
            return conn.send();
        }
        return false;
    }

    /**
     * Add water for some seconds. Pump is stopped if height of water is
     * exceeded
     * CMD: 6
     *
     * @param sec : Second to turn on the pump {0 <= sec < 120}
     * @return true if processed
     */
    public boolean AddWater(int sec) {
        if (sec >= 0 && sec < 120) {
            mess = new Message(ADDWATER);
            mess.setData(sec);
            conn.addMessage(mess);
            return conn.send();
        }
        return false;
    }

    /**
     * NOT IMPLEMENTED
     * Add Fertiliser for some seconds. Pump is stopped if height of water is
     * exceeded
     * CMD: 7
     *
     * @param sec : Secord to turn on the pump
     * @return true if processed
     */
    public boolean AddFertiliser(int sec) {
        return true;
    }

    /**
     * NOT IMPLEMENTED
     * Add CO2 for some seconds. Pump is stopped if height of water is
     * exceeded
     * CMD: 8
     *
     * @param sec : Secord to turn on the valve
     * @return true if processed
     */
    public boolean AddCO2(int sec) {
        return true;
    }

    /**
     * Read tempature inside the PLC
     * CMD:9
     *
     * @return Temperature in kelvin
     */
    public double ReadTemp1() {
        System.out.println("Read greenhouse temperatur ");
        mess = new Message(READ_GREENHOUSE_TEMP);
        double temp = -1;
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            if (mess.getResultData() != null)
                temp = (double) (mess.getResultData())[0];
            else
                temp = -2; // return a dummy value
        }
        System.out.println("Temperature is: " + temp + " celcius");
        return temp;
    }

    /**
     * Read tempature outside the PLC
     * CMD: 10
     *
     * @return Temperature in kelvin
     */
    public double ReadTemp2() {
        System.out.println("Read outdoor temperature");
        mess = new Message(READ_OUTDOOR_TEMP);
        double temp2 = -1;
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            if (mess.getResultData() != null)
                temp2 = (double) (mess.getResultData())[0];
            else
                temp2 = -2; // return a dummy value
        }
        System.out.println("Temperature is: " + temp2);
        return temp2;

    }

    /**
     * Read relative moisture inside the PLC
     * CMD: 11
     *
     * @return Moisture in %
     */
    public double ReadMoist() {
        System.out.println("Read moisture ");
        mess = new Message(READ_MOISTURE);
        double moist = -1.0;
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            if (mess.getResultData() != null)
                moist = (double) (mess.getResultData())[0];
            else
                moist = -2.0; // return a dummy value
            // In real world moist will never be so low
        }
        System.out.println("Moisture is: " + moist + " %");
        return moist;
    }

    /**
     * Read level of water in the PLC
     * CMD: 17
     *
     * @return Level in millimeter [0 < level < 250]
     */
    public double ReadWaterLevel() {
        System.out.println("Read water level ");
        mess = new Message(READ_WATER_LEVEL);
        double level = 0.0; // level
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            if (mess.getResultData() != null)
                level = (mess.getResultData())[0] * 10.0;
            else
                level = 1000.0; // return a dummy value
        }
        System.out.println("Water level is: " + level);
        return level;
    }

    /**
     * NOT IMPLEMENTED IN THE GREENHOUSE
     * Read higths of the plants
     * CMD: 12
     *
     * @return Higths (cm?)
     */
    public double ReadPlantHeight() {
        System.out.println("Read height of plants");
        mess = new Message(READ_PLANT_HEIGHT);
        double level = 0.0; // level
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            if (mess.getResultData() != null)
                level = (mess.getResultData())[0];
            else
                level = 1000.0; // return a dummy value
        }
        System.out.println("Plant height is: " + level);
        return level;
    }

    /**
     * Read all alarms one bits pr. alarm.
     * CMD: 13
     *
     * @return Alarms as BitSet
     */
    public BitSet ReadErrors() {
        System.out.println("Get all alarms ");
        mess = new Message(READ_ALL_ALARMS);
        BitSet alarms = new BitSet(32);

        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            alarms = fillBitSet(mess.getResultData());
        }
        System.out.println("Alarm state is: " + alarms);
        return alarms;
    }


    private BitSet fillBitSet(byte[] al) {
        BitSet alarms = new BitSet(32);
        if (al != null && al.length == 4) {
            for (int i = 0; i < 4; i++)
                for (int b = 0; b < 8; b++) {
                    int ib = (al[i] >> b) & 0x1;
                    Boolean bit;
                    bit = ib == 1;
                    alarms.set(i * 8 + b, bit);
                }
        }
        System.out.println("Alarms in set state: " + alarms);
        return alarms;
    }

    /**
     * Reset one alarm
     * CMD: 14
     *
     * @return Done
     */
    public boolean ResetError(int errorNum) {
        mess = new Message(RESET_ALARMS);
        errorNum--;
        if (errorNum >= 0 && errorNum < 64) // 0 - 30 grader celcius
        {
            System.out.println("Reset alarm " + errorNum + 1);
            mess.setData(errorNum);
            conn.addMessage(mess);
            return conn.send();
        }
        return false;
    }


    /**
     * Get all values as a byte array
     * CMD: 15
     *
     * @return All values
     */
    public byte[] GetStatus() {
        byte[] result = new byte[100];
        System.out.println("Get status ");
        mess = new Message(GET_STATUS);
        mess.setData(); //None data
        conn.addMessage(mess);
        if (conn.send()) {
            result = mess.getResultData();
        }
        System.out.println("State is: " + Arrays.toString(result));
        return result;
    }


    /**
     * Set Fane speed
     * CMD: 16
     *
     * @param speed : {OFF (0), LOW (1), HIGH(2)};
     * @return Done
     */
    public boolean SetFanSpeed(int speed) {
        System.out.println("Set fan speed " + speed);
        mess = new Message(SET_FAN_SPEED);
        if (speed >= 0 && speed <= 2) {
            mess.setData(speed);
            conn.addMessage(mess);
            return conn.send();
        }
        return false;

    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public int getPortnumber() {
        return portnumber;
    }

    public void setPortnumber(int portnumber) {
        this.portnumber = portnumber;
    }

    protected List<PLC> get() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.fileName))) {
            return (List<PLC>) ois.readObject();
        } catch (IOException e) {
            // Ignored
        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String toString() {
        return "IP:" + ipaddress + "\nPort: " + portnumber;
    }
}
