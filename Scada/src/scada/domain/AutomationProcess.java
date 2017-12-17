package scada.domain;

import PLCCommunication.PLC;
import PLCCommunication.UDPConnection;
import shared.GrowthProfile;
import shared.Light;
import shared.ProductionBlock;

import java.util.TreeMap;

/**
 * Class handling the automation process for PLC's
 */
public class AutomationProcess {
    
    private final ProductionBlock pb;
    private final GrowthProfile gp;
    private long secondsSinceMidnight;
    private PLC plcComm;
    
    /**
     * Constructor for this automation process.
     * Each PLC has its own automation process.
     * @param pb Production Block to perform updates for
     * @param gp GrowthProfile to try and match with given Production Block
     * @param secondsSinceMidnight Seconds since midnight
     */
    public AutomationProcess(ProductionBlock pb, GrowthProfile gp, long secondsSinceMidnight) {
        this.pb = pb;
        this.gp = gp;
        this.secondsSinceMidnight = secondsSinceMidnight;
        this.plcComm = new PLC(new UDPConnection(this.pb.getPort(), this.pb.getIpaddress()));
    }
    
    /**
     * Main method which performs updates on the PLC.
     * @return True on succesful run updates
     */
    public boolean doUpdates() {
        if(this.pb == null || this.gp == null) {
            return false;
        }
        
        this.updateProductionBlockAttributes();
        this.updateLightLevel();
        this.updateWaterLevel();
        this.updateMoistureLevel();
        this.updateTemperature();
        return true;
    }
    
    /**
     * Internal method to update a Production Blocks internal attributes based on the ones read from actual PLC.
     */
    private void updateProductionBlockAttributes() {
        this.pb.setWaterLevel((int) this.plcComm.ReadWaterLevel());
        this.pb.setMoisture((int) this.plcComm.ReadMoist());
        this.pb.setTemp1(this.plcComm.ReadTemp1());
    }
    
    /**
     * Internal method to update a Production Blocks light levels based on the GrowthProfile gp
     */
    private void updateLightLevel() {
        TreeMap<Integer, Light> sortedLight = new TreeMap<>();
        for(Light light : this.gp.getLightSequence()) {
            sortedLight.put(light.getId(), light);
        }
        long tempSeconds = this.secondsSinceMidnight;
        for(Light light : sortedLight.values()) {
            tempSeconds -= light.getRunTimeUnix();
            if(tempSeconds < 0) {
                switch(light.getType()) {
                    case 0:
                        this.plcComm.SetBlueLight(0);
                        this.plcComm.SetRedLight(0);
                        break;
                    case 1:
                        this.plcComm.SetBlueLight(light.getPowerLevel());
                        this.plcComm.SetRedLight(0);
                        break;
                    case 2:
                        this.plcComm.SetBlueLight(0);
                        this.plcComm.SetRedLight(light.getPowerLevel());
                        break;
                    case 3:
                        this.plcComm.SetBlueLight(light.getPowerLevel());
                        this.plcComm.SetRedLight(light.getPowerLevel());
                        break;
                }
                break;
            }
        }
    }
    
    /**
     * Internal method to update a Production Blocks water level based on the GrowthProfile gp
     */
    private void updateWaterLevel() {
        if(this.pb.getWaterLevel() < this.gp.getWaterLevel()) {
            this.plcComm.AddWater(2); // Add 2 seconds of water!
        }
    }
    
    /**
     * Internal method to update a Production Blocks moisture level based on the GrowthProfile gp
     */
    private void updateMoistureLevel() {
        this.plcComm.SetMoisture(this.gp.getMoisture());
    }
    
    /**
     * Internal method to update a Production Blocks temperature based on the GrowthProfile gp
     */
    private void updateTemperature() {
        if(this.secondsSinceMidnight > 28800 && this.secondsSinceMidnight < 72000) {
            this.plcComm.SetTemperature(this.gp.getTemperature());
        } else {
            this.plcComm.SetTemperature(this.gp.getNightTemperature());
        }
    }
    
    
    
}
