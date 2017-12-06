/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

//import scada.persistence.ProductionBlock;
import scada.domain.interfaces.IScada;
import scada.domain.interfaces.ReadWriteProductionBlock;
import shared.ProductionBlock;
import shared.GrowthProfile;
import shared.Light;
import shared.Log;
import scada.persistence.FileHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import scada.domain.interfaces.ReadWriteGrowthProfile;
import scada.domain.interfaces.ReadWriteLog;

/**
 * @author chris
 */
public class Scada implements IScada {

    private ReadWriteProductionBlock readWriteProductionBlock;
    private ReadWriteLog readWriteLog;
    private ReadWriteGrowthProfile readWriteGrowthProfile;
    
    private static Scada instance = null;

    public static Scada getInstance() {
        if (instance == null) {
            instance = new Scada();
        }
        return instance;
    }

    private HashMap<Integer, ProductionBlock> pbMap;
    private HashMap<Integer, GrowthProfile> gpMap;
    private boolean continueAutomation = true;
    
    private int debugCount = 0;

    protected Scada() {
        this.readWriteProductionBlock = FileHandler.getInstance();
        this.readWriteLog = FileHandler.getInstance();
        this.readWriteGrowthProfile = FileHandler.getInstance();
        // Is here to prevent instantiation

        // Local variables
        this.pbMap = new HashMap<>();
        this.gpMap = new HashMap<>();
        this.initiateTimedAutomationTask(20000);
    }

    @Override
    public boolean ping(String testData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<shared.ProductionBlock> getProductionBlocks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setProduction(String productionBlock, String growthProfile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void savePLC(ArrayList<ProductionBlock> plcList) throws IOException {
        readWriteProductionBlock.savePLC(plcList);
    }

    public void savePLC(ProductionBlock plc) throws IOException, ClassNotFoundException {
        this.readWriteProductionBlock.savePLC(plc);
    }

    @Override
    public ArrayList<ProductionBlock> readPLCList() throws IOException, ClassNotFoundException {
        return (ArrayList<ProductionBlock>) readWriteProductionBlock.readPLCFile();
    }

    @Override
    public void removePLC(int plcToRemove) throws IOException, ClassNotFoundException {
        readWriteProductionBlock.removePLC(plcToRemove);
    }

    private void initiateTimedAutomationTask(long time) {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doAutomation();
            }
        }, time, time);
    }

    private void doAutomation() {
        System.out.println("Starting updates!");
        if(this.continueAutomation) {
            this.updateProductionBlockMap();

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime midnight = now.truncatedTo(ChronoUnit.DAYS);
            Duration duration = Duration.between(midnight, now);
            long secondsSinceMidnight = duration.getSeconds();
            
            secondsSinceMidnight = 0;
            
            secondsSinceMidnight += (40000 * this.debugCount++);
            secondsSinceMidnight = secondsSinceMidnight % 86400;

            for (ProductionBlock pb : this.pbMap.values()) {
                //this.updateLightLevel(pb);
                AutomationProcess ap = new AutomationProcess(pb, this.gpMap.get(pb.getGrowthConfigId()), secondsSinceMidnight);
                if (!ap.doUpdates()) {
                    System.out.println("ProductionBlock id " + pb.getId() + ": Failed to do updates!");
                } else {
                    System.out.println("Did updates!");
                }
            }
        }
    }

    private void updateProductionBlockMap() {
        // Fetch list from MES, if not, just use what you have
        // pbArr = API.getBlaBlaBla();

        // For debug
        this.gpMap.put(1, new GrowthProfile());
        this.gpMap.get(1).setId(1);
        ArrayList<Light> lightList = new ArrayList<>();
        lightList.add(new Light());
        lightList.get(0).setId(0);
        lightList.get(0).setType(3);
        lightList.get(0).setPowerLevel(100);
        lightList.get(0).setRunTimeUnix(43200);
        this.gpMap.get(1).setLightSequence(lightList);
        this.gpMap.get(1).setMoisture(50);
        this.gpMap.get(1).setName("testing gp");
        this.gpMap.get(1).setNightTemperature(15);
        this.gpMap.get(1).setTemperature(25);
        this.gpMap.get(1).setWaterLevel(20);
        
        try {
            this.readWriteGrowthProfile.writeGrowthProfileFile(new ArrayList<>(this.gpMap.values()));
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        List<Log> logList = new ArrayList<>();
        logList.add(new Log());
        logList.get(0).setValue("testing cunts");
        logList.get(0).setCmd(27);
        try {
            this.readWriteLog.writeLogFile(logList);
        } catch(IOException ex) {
            System.out.println(ex);
        }
        
        System.out.println("Updating pbMap");
        ProductionBlock[] pbArr = {new ProductionBlock(0, "10.126.5.242", 5000, "n1")};
        pbArr[0].setGrowthConfigId(1);
        
        for(int i = 0; i < pbArr.length; i++) {
            this.pbMap.put(pbArr[i].getId(), pbArr[i]);
        }
    }
}
