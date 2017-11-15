/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

import scada.persistence.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chris
 */
public class Scada implements IScada {

    private ProductionBlock prodBlock = new ProductionBlock();
    private static Scada scada = new Scada();

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
        prodBlock.writePLCFile(plcList);
    }

    @Override
    public ArrayList readPLCFile() throws IOException, ClassNotFoundException {
        ArrayList list;
        list = prodBlock.readPLCFile();
        return list;
    }

    @Override
    public void removePLC(ProductionBlock plcToRemove) throws IOException, ClassNotFoundException {
        ArrayList list;
        list = prodBlock.readPLCFile();
        if (list.contains(plcToRemove)) {
            System.out.println("PLC found");
        } else {
            System.out.println("PLC not found");
        }
    }

    public Scada getInstance() {
        return scada;
    }
}
