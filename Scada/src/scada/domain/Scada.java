/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

import scada.persistence.FileHandler;
import scada.persistence.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author chris
 */
public class Scada implements IScada {
    
    private ReadWriteProductionBlock readWriteProductionBlock;
    
    private static Scada instance = null;

    protected Scada() {
        this.readWriteProductionBlock = FileHandler.getInstance();
        // Is here to prevent instantiation
    }

    public static Scada getInstance() {
        if (instance == null) {
            instance = new Scada();
        }
        return instance;
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
        this.readWriteProductionBlock.writePLCToFile(plcList);
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
                this.readWriteProductionBlock.writePLCToFile(list);
            }
        }
    }
}
