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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private HashMap<Integer, GrowthProfile> manualGPMap;
    private ArrayList<Log> logList;
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
        this.manualGPMap = new HashMap<>();
        this.logList = new ArrayList<>();
        this.initiateTimedAutomationTask(20000);
        
        //Debugs
        this.debugAdd();
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

    @Override
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
                GrowthProfile selectedGp;
                if(pb.getManualGrowthConfigId() > 0) {
                    selectedGp = this.gpMap.get(pb.getGrowthConfigId());
                } else {
                    selectedGp = this.gpMap.get(pb.getManualGrowthConfigId());
                }
                //Probalby should check whether selectedGp is null
                AutomationProcess ap = new AutomationProcess(pb, selectedGp, secondsSinceMidnight);
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
        
        System.out.println("Updating pbMap");
        ProductionBlock[] pbArr = {new ProductionBlock(0, "10.126.5.242", 5000, "n1")};
        pbArr[0].setGrowthConfigId(1);
        
        for(int i = 0; i < pbArr.length; i++) {
            this.pbMap.put(pbArr[i].getId(), pbArr[i]);
        }
    }
    
    public void debugAdd() {
        // Fetch list from MES, if not, just use what you have
        // pbArr = API.getBlaBlaBla();

        // For debug
        List<GrowthProfile> gpList = new ArrayList<>();
        gpList.add(new GrowthProfile());
        gpList.get(0).setId(1);
        ArrayList<Light> lightList = new ArrayList<>();
        lightList.add(new Light());
        lightList.get(0).setId(0);
        lightList.get(0).setType(3);
        lightList.get(0).setPowerLevel(100);
        lightList.get(0).setRunTimeUnix(43200);
        lightList.add(new Light());
        lightList.get(1).setId(1);
        lightList.get(1).setType(1);
        lightList.get(1).setPowerLevel(100);
        lightList.get(1).setRunTimeUnix(33200);
        gpList.get(0).setLightSequence(lightList);
        gpList.get(0).setMoisture(50);
        gpList.get(0).setName("testing gp");
        gpList.get(0).setNightTemperature(15);
        gpList.get(0).setTemperature(25);
        gpList.get(0).setWaterLevel(20);
        
        this.addGrowthProfile(gpList.get(0));
        
        //GrowthProfile[] gpArr = gpList.toArray(new GrowthProfile[0]);
        
        /*for(int i = 0; i < gpArr.length; i++) {
            this.gpMap.put(gpArr[i].getId(), gpArr[i]);
        }
        
        this.saveGrowthProfiles();*/
        
        
        List<Log> logList1 = new ArrayList<>();
        logList1.add(new Log());
        logList1.get(0).setValue("testing cunts");
        logList1.get(0).setCmd(27);
        this.addLog(logList1.get(0));
        this.saveLogs();
    }
    
    public void addGrowthProfile(GrowthProfile gp) {
        this.gpMap.put(gp.getId(), gp);
        this.saveGrowthProfiles();
    }
    
    public int addManualGrowthProfile(GrowthProfile gp) {
        int highestId = 1;
        for(Integer integer : this.manualGPMap.keySet()) {
            if(integer > highestId) {
                highestId = integer;
            }
        }
        this.manualGPMap.put(highestId, gp);
        this.saveGrowthProfiles();
        return highestId;
    }
    
    private void saveGrowthProfiles() {
        Set<GrowthProfile> gpSet = new HashSet<>();
        gpSet.addAll(new HashSet<>(this.manualGPMap.values()));
        gpSet.addAll(this.gpMap.values());
        
        try {
            this.readWriteGrowthProfile.writeGrowthProfileFile(new ArrayList<>(gpSet));
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    public void addLog(Log log) {
        this.logList.add(log);
        this.saveLogs();
    }
    
    public void clearLogList() {
        this.logList.clear();
        this.saveLogs();
    }
    
    private void saveLogs() {
        try {
            this.readWriteLog.writeLogFile(this.logList);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
