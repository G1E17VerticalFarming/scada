/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

//import scada.persistence.ProductionBlock;
import shared.ProductionBlock;
import shared.GrowthProfile;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import scada.persistence.FileHandler;

/**
 * @author chris
 */
public class Scada implements IScada {
    
    private ReadWriteProductionBlock readWriteProductionBlock;
    
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

    protected Scada() {
        this.readWriteProductionBlock = FileHandler.getInstance();
        // Is here to prevent instantiation
        
        // Local variables
        this.pbMap = new HashMap<>();
        this.gpMap = new HashMap<>();
        this.initiateTimedAutomationTask(10000);
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
    public void writePLCFile(ArrayList<ProductionBlock> plcList) throws IOException {
        this.readWriteProductionBlock.writePLCFile(plcList);
    }

    @Override
    public ArrayList<ProductionBlock> readPLCFile() throws IOException, ClassNotFoundException {
        ArrayList<ProductionBlock> list = new ArrayList<>(this.readWriteProductionBlock.readPLCFile());
        return list;
    }

    @Override
    public void removePLC(int plcToRemove) throws IOException, ClassNotFoundException {
        ArrayList<ProductionBlock> list = new ArrayList<>(this.readWriteProductionBlock.readPLCFile());

        Iterator<ProductionBlock> it = list.iterator();
        while (it.hasNext()) {
            ProductionBlock plc = it.next();
            if (plc.getId() == plcToRemove) {
                System.out.println("Removing ID: " + plcToRemove);
                it.remove();
                this.readWriteProductionBlock.writePLCFile(list);
            }
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
        if(this.continueAutomation) {
            this.updateProductionBlockMap();
            
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime midnight = now.truncatedTo(ChronoUnit.DAYS);
            Duration duration = Duration.between(midnight, now);
            final long secondsSinceMidnight = duration.getSeconds();

            for(ProductionBlock pb : this.pbMap.values()) {
                //this.updateLightLevel(pb);
                AutomationProcess ap = new AutomationProcess(pb, this.gpMap.get(pb.getGrowthConfigId()), secondsSinceMidnight);
                if(!ap.doUpdates()) {
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
        ProductionBlock[] pbArr = {new ProductionBlock(0, "localhost", 12345, "n1"), new ProductionBlock(1, "localhost", 23456, "n2")};
        
        for(int i = 0; i < pbArr.length; i++) {
            this.pbMap.put(pbArr[i].getId(), pbArr[i]);
        }
    }
}
