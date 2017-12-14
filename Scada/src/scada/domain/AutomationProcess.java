/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

import shared.ProductionBlock;
import shared.GrowthProfile;
import PLCCommunication.PLC;
import PLCCommunication.UDPConnection;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;
import shared.Light;

/**
 *
 * @author DanielToft
 */
public class AutomationProcess {
    
    private final ProductionBlock pb;
    private final GrowthProfile gp;
    private long secondsSinceMidnight;
    private PLC plcComm;
    
    public AutomationProcess(ProductionBlock pb, GrowthProfile gp, long secondsSinceMidnight) {
        this.pb = pb;
        this.gp = gp;
        this.secondsSinceMidnight = secondsSinceMidnight;
        this.plcComm = new PLC(new UDPConnection(this.pb.getPort(), this.pb.getIpaddress()));
    }
    
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
    
    private void updateProductionBlockAttributes() {
        this.pb.setWaterLevel((int) this.plcComm.ReadWaterLevel());
        this.pb.setMoisture((int) this.plcComm.ReadMoist());
        this.pb.setTemp1(this.plcComm.ReadTemp1());
    }
    
    private void updateLightLevel() {
        TreeMap<Integer, Light> sortedLight = new TreeMap<>();
        for(Light light : this.gp.getLightSequence()) {
            sortedLight.put(light.getId(), light); // Make sure they're sorted!
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
                break; // Break out of sortedLight foreach loop!
            }
        }
    }
    
    private void updateWaterLevel() {
        if(this.pb.getWaterLevel() < this.gp.getWaterLevel()) {
            this.plcComm.AddWater(2); // Add 2 seconds of water!
        }
    }
    
    private void updateMoistureLevel() {
        this.plcComm.SetMoisture(this.gp.getMoisture());
    }
    
    private void updateTemperature() {
         // Assumption: Between 8 and 20 is the "day", this is because the light sequence is probably based on this too
        if(this.secondsSinceMidnight > 28800 && this.secondsSinceMidnight < 72000) {
            this.plcComm.SetTemperature(this.gp.getTemperature());
        } else {
            this.plcComm.SetTemperature(this.gp.getNightTemperature());
        }
    }
    
    
    
}
