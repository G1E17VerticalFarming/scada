package scada.domain;

import PLCCommunication.PLC;
import PLCCommunication.UDPConnection;
import scada.domain.interfaces.IScada;
import scada.domain.interfaces.ReadWriteGrowthProfile;
import scada.domain.interfaces.ReadWriteLog;
import scada.domain.interfaces.ReadWriteProductionBlock;
import scada.persistence.FileHandler;
import shared.GrowthProfile;
import shared.Log;
import shared.ProductionBlock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import scada.api.ApiSendController;
import scada.domain.interfaces.IScadaApiSend;

/**
 * Class used to create a domain layer object of the SCADA. This class ties GUI and Persistence-layer together.
 */
public class Scada implements IScada {

    private ReadWriteProductionBlock readWriteProductionBlock;
    private ReadWriteLog readWriteLog;
    private ReadWriteGrowthProfile readWriteGrowthProfile;

    private final IScadaApiSend apiSend;

    private static Scada instance = null;
    
    /**
     * Singleton design pattern
     * @return The only instance of Scada
     */
    public static Scada getInstance() {
        if (instance == null) {
            instance = new Scada();
        }
        return instance;
    }

    private ArrayList<ProductionBlock> pbList;
    private ArrayList<ProductionBlock> pbUpdateList;
    private ArrayList<ProductionBlock> pbDeleteList;
    private HashMap<Integer, GrowthProfile> gpMap;
    private HashMap<Integer, GrowthProfile> manualGPMap;
    private ArrayList<Log> logList;
    private boolean continueAutomation = true;
    private int debugCount = 0;
    private SimpleDateFormat ft = new SimpleDateFormat("dd/MM-yyyy HH:mm:ss");

    /**
     * Singleton instantiation of the class, so there can only ever be one instance.
     */
    protected Scada() {
        this.readWriteProductionBlock = FileHandler.getInstance();
        this.readWriteLog = FileHandler.getInstance();
        this.readWriteGrowthProfile = FileHandler.getInstance();
        this.apiSend = ApiSendController.getInstance();
        this.pbUpdateList = new ArrayList<>();
        this.pbDeleteList = new ArrayList<>();
        this.gpMap = new HashMap<>();
        this.manualGPMap = new HashMap<>();
        this.logList = new ArrayList<>();
        this.initiateTimedAutomationTask(20000);

        this.updateProductionBlockMap();
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

    /**
     * Internal method to write the PLC list to file
     */
    private synchronized void savePLC() {
        try {
            this.readWriteProductionBlock.savePLC(this.pbList);
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
    }

    /**
     * Internal method to read the PLC list
     * @return List of Production Block objects
     */
    private ArrayList<ProductionBlock> readPLCList() {
        try {
            return (ArrayList<ProductionBlock>) readWriteProductionBlock.readPLCFile();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex);
            return new ArrayList<>();
        }
    }

    /**
     * Internal method to read GrowthProfile list
     * @return List of GrowthProfile objects
     */
    private ArrayList<GrowthProfile> readGbList() {
        try {
            return (ArrayList<GrowthProfile>) this.readWriteGrowthProfile.readGrowthProfileFile();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex);
            return new ArrayList<>();
        }
    }

    /**
     * Internal method to begin automation of PLC's
     * @param time Time interval to run the automation tasks
     */
    private void initiateTimedAutomationTask(long time) {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doAutomation();
            }
        }, time, time);
    }

    /**
     * Internal method to perform automation of PLC's
     */
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
                GrowthProfile selectedGp = this.getProductionBlockGrowthProfile(pb);

                AutomationProcess ap = new AutomationProcess(pb, selectedGp, secondsSinceMidnight);
                System.out.println("PB: " + pb);
                System.out.println("GP: " + selectedGp);
                if (!ap.doUpdates()) {
                    System.out.println("ProductionBlock id " + pb.getId() + ": Failed to do updates!");
                } else {
                    System.out.println("Did updates!");
                }
            }
        }
    }

    /**
     * Internal method to send the list of waiting PLC updates to MES system.
     * If MES is still offline, wait with emptying the internal map.
     * If connection is alive (tested through ping method) it will update each
     * individual PLC through MES.
     * Does the same with the map holding waiting PLC deletions.
     */
    private synchronized void updateProductionBlockMap() {
        ArrayList<ProductionBlock> pbList;

        if (this.apiSend.ping()) {
            if(!this.pbUpdateList.isEmpty()) {
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
                this.pbDeleteList.parallelStream().forEach((ProductionBlock pb) -> this.apiSend.deleteProductionBlock(pb));
                this.pbDeleteList.clear();
            }
            pbList = new ArrayList<>(Arrays.asList(this.apiSend.getAllProductionBlocks()));
        } else {
            pbList = this.readPLCList();
        }

        this.pbList = pbList;
        this.savePLC();
        this.updateGrowthProfileMap();
    }

    /**
     * Internal method to update the internal GrowthProfile map with the
     * Growth Profile objects required by the PLC's locally.
     */
    private synchronized void updateGrowthProfileMap() {
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
        for(ProductionBlock pb : this.pbList) {
            if(!this.gpMap.containsKey(pb.getGrowthConfigId())) {
                if(pb.getGrowthConfigId() == 0) {
                    continue;
                }
                GrowthProfile gp = this.apiSend.getSpecificGrowthProfile(pb.getGrowthConfigId());
                this.gpMap.put(gp.getId(), gp);
            }
        }
        this.saveGrowthProfiles();
    }

    /**
     * Method to add a GrowthProfile to the internal GrowthProfile map manually
     * Will run through the map and find the highest available unique id.
     * @param gp GrowthProfile object to add
     * @return the highest available unique ID in the map
     */
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

    /**
     * Internal method to write internal growth profile map to json encoded file
     */
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

    /**
     * Method to add a Log object to the internal log map
     * @param log 
     */
    public void addLog(Log log) {
        this.logList.add(log);
        this.saveLogs();
    }

    /**
     * Method to clear local list of Log objects and save them
     */
    public void clearLogList() {
        this.logList.clear();
        this.saveLogs();
    }

    /**
     * Internal method to write internal log map to json encoded file
     */
    private void saveLogs() {
        try {
            this.readWriteLog.writeLogFile(this.logList);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Method to get a GrowthProfile object from a given Production Block object
     * @param pb Production Object to fetch GrowthProfile for
     * @return GrowthProfile object
     */
    public GrowthProfile getProductionBlockGrowthProfile(ProductionBlock pb) {
        if (pb.getManualGrowthConfigId() == 0) {
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
            System.out.println("COMES HERE?!");
            return this.gpMap.get(pb.getManualGrowthConfigId());
        }
    }

    /**
     * Method to check status of each PLC connected to SCADA
     */
    public synchronized void checkStatus() {
        this.pbList.parallelStream().forEach((ProductionBlock plc) -> {
            Date dNow = new Date();
            System.out.println("Checking status of " + plc.getIpaddress() + ":" + plc.getPort());

            PLC plccomm = new PLC(new UDPConnection(plc.getPort(), plc.getIpaddress()));

            double temp1 = plccomm.ReadTemp1();

            if (temp1 == -1) {
                plc.setTemp1(-1);
                plc.setTemp2(-1);
                plc.setMoisture(-1);
                plc.setStatus("No connection");
                plc.setFanspeed(-1);
            } else if (temp1 == -2) {
                plc.setTemp1(-2);
                plc.setTemp2(-2);
                plc.setMoisture(-2);
                plc.setStatus("Data error");
                plc.setFanspeed(-2);
            } else {
                plc.setTemp1(temp1);
                plc.setTemp2(plccomm.ReadTemp2());
                plc.setMoisture(plccomm.ReadMoist());
                plc.setStatus("OK");
                plc.setLastOK("" + ft.format(dNow));
            }
            plc.setLastCheck("" + ft.format(dNow));
        });
        System.out.println("You checked the status of the PLC's ");
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
