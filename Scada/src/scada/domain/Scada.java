/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

//import scada.persistence.ProductionBlock;
import PLCCommunication.PLC;
import PLCCommunication.UDPConnection;
import scada.domain.interfaces.IScada;
import scada.domain.interfaces.ReadWriteGrowthProfile;
import scada.domain.interfaces.ReadWriteLog;
import scada.domain.interfaces.ReadWriteProductionBlock;
import scada.persistence.FileHandler;
import shared.GrowthProfile;
import shared.Light;
import shared.Log;
import shared.ProductionBlock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import scada.api.ApiSendController;
import scada.domain.interfaces.IScadaApiSend;

/**
 * @author chris
 */
public class Scada implements IScada {

    private ReadWriteProductionBlock readWriteProductionBlock;
    private ReadWriteLog readWriteLog;
    private ReadWriteGrowthProfile readWriteGrowthProfile;

    private final IScadaApiSend apiSend;

    private static Scada instance = null;

    public static Scada getInstance() {
        if (instance == null) {
            instance = new Scada();
        }
        return instance;
    }

    //private HashMap<Integer, ProductionBlock> pbMap;
    private ArrayList<ProductionBlock> pbList;
    private ArrayList<ProductionBlock> pbUpdateList;
    private ArrayList<ProductionBlock> pbDeleteList;
    private HashMap<Integer, GrowthProfile> gpMap;
    private HashMap<Integer, GrowthProfile> manualGPMap;
    private ArrayList<Log> logList;
    private boolean continueAutomation = true;

    private int debugCount = 0;
    private SimpleDateFormat ft = new SimpleDateFormat("dd/MM-yyyy HH:mm:ss");

    protected Scada() {
        this.readWriteProductionBlock = FileHandler.getInstance();
        this.readWriteLog = FileHandler.getInstance();
        this.readWriteGrowthProfile = FileHandler.getInstance();

        this.apiSend = ApiSendController.getInstance();
        // Is here to prevent instantiation

        // Local variables
        //this.pbMap = new HashMap<>();
        this.pbUpdateList = new ArrayList<>();
        this.pbDeleteList = new ArrayList<>();
        this.gpMap = new HashMap<>();
        this.manualGPMap = new HashMap<>();
        this.logList = new ArrayList<>();
        this.initiateTimedAutomationTask(2000000);

        this.updateProductionBlockMap();
        this.updateGrowthProfileMap();

        //Debugs
        this.debugAdd();
    }

    @Override
    public synchronized void savePLC(ProductionBlock plc) {
        plc.setId(-1);
        this.pbUpdateList.add(plc);
        this.pbList.add(plc);
        this.savePLC();
    }
    
    @Override
    public synchronized void updatePLC(ProductionBlock plc) {
        this.pbUpdateList.add(plc);
    }

    @Override
    public synchronized void removePLC(ProductionBlock pb) {
        this.pbDeleteList.add(pb);
    }

    private synchronized void savePLC() {
        
        try {
            this.readWriteProductionBlock.savePLC(this.pbList);
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }

        /*if (this.apiSend.ping()) {
            this.pbList.parallelStream().forEach((ProductionBlock pb) -> {
                if(pb.getId() == -1) {
                    this.apiSend.saveProductionBlock(pb);
                } else if(pb.getId() == -2) {
                    this.apiSend.deleteProductionBlock(pb);
                } else if(pb.getId() == -3) {
                    
                }
            });
        }*/
    }

    //@Override
    private ArrayList<ProductionBlock> readPLCList() {
        try {
            return (ArrayList<ProductionBlock>) readWriteProductionBlock.readPLCFile();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex);
            return new ArrayList<>();
        }
    }

    private ArrayList<GrowthProfile> readGbList() {
        try {
            return (ArrayList<GrowthProfile>) this.readWriteGrowthProfile.readGrowthProfileFile();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex);
            return new ArrayList<>();
        }
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
        if (this.continueAutomation) {
            this.updateProductionBlockMap();

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime midnight = now.truncatedTo(ChronoUnit.DAYS);
            Duration duration = Duration.between(midnight, now);
            long secondsSinceMidnight = duration.getSeconds();

            secondsSinceMidnight = 0;

            secondsSinceMidnight += (40000 * this.debugCount++);
            secondsSinceMidnight = secondsSinceMidnight % 86400;

            for (ProductionBlock pb : this.pbList) {
                if(pb.getGrowthConfigId() <= 0) {
                    continue;
                }
                //this.updateLightLevel(pb);
                GrowthProfile selectedGp = this.getProductionBlockGrowthProfile(pb);

                AutomationProcess ap = new AutomationProcess(pb, selectedGp, secondsSinceMidnight);
                if (!ap.doUpdates()) {
                    System.out.println("ProductionBlock id " + pb.getId() + ": Failed to do updates!");
                } else {
                    System.out.println("Did updates!");
                }
            }
        }
    }

    private synchronized void updateProductionBlockMap() {
        // Fetch list from MES, if not, just use what you have
        // pbArr = API.getBlaBlaBla();

        // For debug
        System.out.println("Updating pbMap");
        //ProductionBlock[] pbArr = {new ProductionBlock(0, "10.126.5.242", 5000, "n1")};
        //pbArr[0].setGrowthConfigId(1);
        ArrayList<ProductionBlock> pbList;

        if (this.apiSend.ping()) {
            if(!this.pbUpdateList.isEmpty()) {
                //If one or more of the updateProductionBlock calls fails, it will still delete the list. 
                // This will require some extra exception handling to make it robust.
                this.pbUpdateList.parallelStream().forEach((ProductionBlock pb) -> {
                    if(pb.getId() < 0) {
                        this.apiSend.saveProductionBlock(pb);
                    } else {
                        this.apiSend.updateProductionBlock(pb);
                    }
                });
                this.pbUpdateList.clear();
            }
            if(!this.pbDeleteList.isEmpty()) {
                //If one or more of the updateProductionBlock calls fails, it will still delete the list. 
                // This will require some extra exception handling to make it robust.
                this.pbDeleteList.parallelStream().forEach((ProductionBlock pb) -> this.apiSend.deleteProductionBlock(pb));
                this.pbDeleteList.clear();
            }
            pbList = new ArrayList<>(Arrays.asList(this.apiSend.getAllProductionBlocks()));
        } else {
            pbList = this.readPLCList();
        }

        System.out.println("TEST" + pbList.size());

        this.pbList = pbList;
        this.savePLC();
    }

    private synchronized void updateGrowthProfileMap() {
        // Fetch list from MES, if not, just use what you have
        // pbArr = API.getBlaBlaBla();

        // For debug
        System.out.println("Updating gbMap");
        List<GrowthProfile> gbList;

        gbList = this.readGbList();

        for (GrowthProfile gp : gbList) {
            if (gp.getId() >= 0) {
                this.gpMap.put(gp.getId(), gp);
            } else {
                this.manualGPMap.put(gp.getId(), gp);
            }
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
        for (Integer integer : this.manualGPMap.keySet()) {
            if (integer > highestId) {
                highestId = integer;
            }
        }
        gp.setId(highestId * -1);
        this.manualGPMap.put(highestId, gp);
        this.saveGrowthProfiles();
        return highestId;
    }

    private void saveGrowthProfiles() {
        Set<GrowthProfile> gpSet = new HashSet<>();
        gpSet.addAll(this.manualGPMap.values());
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

    public GrowthProfile getProductionBlockGrowthProfile(ProductionBlock pb) {
        //Probalby should check whether selectedGp is null
        if (pb.getManualGrowthConfigId() > 0) {
            if(!this.gpMap.containsKey(pb.getGrowthConfigId())) {
                GrowthProfile fetchedGp;
                if(this.apiSend.ping()) {
                    fetchedGp = this.apiSend.getSpecificGrowthProfile(pb.getGrowthConfigId());
                    this.gpMap.put(fetchedGp.getId(), fetchedGp);
                } else {
                    return null;
                }
            }
            return this.gpMap.get(pb.getGrowthConfigId());
        } else {
            return this.gpMap.get(pb.getManualGrowthConfigId());
        }
    }

    public synchronized void checkStatus() {

        //for (ProductionBlock plc : this.pbList) { // Add threads
        this.pbList.parallelStream().forEach((ProductionBlock plc) -> {
            Date dNow = new Date();
            System.out.println("Checking status of " + plc.getIpaddress() + ":" + plc.getPort());

            PLC plccomm = new PLC(new UDPConnection(plc.getPort(), plc.getIpaddress()));

            // Check status of PLC
            double temp1 = plccomm.ReadTemp1();

            if (temp1 == -1) { // No connection to PLC
                plc.setTemp1(-1);
                plc.setTemp2(-1);
                plc.setMoisture(-1);
                //currentPLC.setStatus("No connection");
                plc.setFanspeed(-1);
            } else if (temp1 == -2) { // Error in returned data
                plc.setTemp1(-2);
                plc.setTemp2(-2);
                plc.setMoisture(-2);
                //currentPLC.setStatus("Data error");
                plc.setFanspeed(-2);
            } else {
                plc.setTemp1(temp1);
                plc.setTemp2(plccomm.ReadTemp2());
                plc.setMoisture(plccomm.ReadMoist());
                //currentPLC.setStatus("OK");
                plc.setLastOK("" + ft.format(dNow));
            }
            plc.setLastCheck("" + ft.format(dNow));
        });
        /*try {
            savePLC(newStatusList);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        System.out.println("You checked the status of the PLC's ");

        //this.savePLC(); // We don't have to save all of this!
    }

    @Override
    public ArrayList<ProductionBlock> getPLCList() {
        this.updateProductionBlockMap();
        return this.pbList;
    }

    @Override
    public ArrayList<ProductionBlock> getUpdatedPLCList() {
        this.updateProductionBlockMap();
        this.checkStatus();
        return this.pbList;
    }
}
