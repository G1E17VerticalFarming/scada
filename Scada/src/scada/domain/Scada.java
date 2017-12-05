/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

//import scada.persistence.ProductionBlock;

import scada.persistence.FileHandler;
import shared.GrowthProfile;
import shared.ProductionBlock;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        readWriteProductionBlock = FileHandler.getInstance();
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
        if (this.continueAutomation) {
            this.updateProductionBlockMap();

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime midnight = now.truncatedTo(ChronoUnit.DAYS);
            Duration duration = Duration.between(midnight, now);
            final long secondsSinceMidnight = duration.getSeconds();

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
        System.out.println("Updating pbMap");
        ProductionBlock[] pbArr = {new ProductionBlock(0, "localhost", 12345, "n1"), new ProductionBlock(1, "localhost", 23456, "n2")};

        for (int i = 0; i < pbArr.length; i++) {
            this.pbMap.put(pbArr[i].getId(), pbArr[i]);
        }
    }
}
