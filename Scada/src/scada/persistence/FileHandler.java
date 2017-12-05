/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.persistence;


import scada.domain.ReadWriteProductionBlock;
import shared.ProductionBlock;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author DanielToft
 */
public class FileHandler implements ReadWriteProductionBlock {

    String PLCListPath;

    private static FileHandler instance = null;

    private FileHandler() {
        try {
            this.PLCListPath = this.findPLCListPath();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            System.out.println(" - Meaning no methods are possible in read/writing a production block!");
        }
    }

    public static FileHandler getInstance() {
        if (instance == null) {
            instance = new FileHandler();
        }
        return instance;
    }


    @Override
    public List<ProductionBlock> readPLCFile() throws IOException {
        if (this.PLCListPath.isEmpty()) {
            System.out.println("Error: could not read PLClist file as there is found no valid path!");
            return null;
        }

        FileInputStream fi = null;
        ObjectInputStream in = null;

        ArrayList<ProductionBlock> list = new ArrayList<>();

        try {
            fi = new FileInputStream(new File(this.PLCListPath));
            if (fi.getChannel().size() > 0) {
                in = new ObjectInputStream(fi); //Initiates the files content into ObjectInputStream
            }
            if (fi.getChannel().size() == 0) {
                System.out.println("No PLC objects found.");
            }
        } catch (FileNotFoundException e) {
            System.out.println(this.PLCListPath + " blev ikke fundet.");
        }


        while (in != null) {
            try {
                ProductionBlock plc = (ProductionBlock) in.readObject();
                list.add(plc);
            } catch (SocketTimeoutException exc) {
                // you got the timeout
                break;
            } catch (EOFException exc) {
                in.close();
                in = null;
                break;
            } catch (IOException exc) {
                // some other I/O error: print it, log it, etc.
                System.out.println("Error: " + exc);
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    @Override
    public void savePLC(ArrayList<ProductionBlock> plcList) throws IOException {
        if (this.PLCListPath.isEmpty()) {
            System.out.println("Error: could not write PLClist file as there is found no valid path!");
            return;
        }

        FileOutputStream f = new FileOutputStream(new File(this.PLCListPath));
        ObjectOutputStream out = new ObjectOutputStream(f);
        for (ProductionBlock plc : plcList) {
            out.writeObject(plc);
        }
        out.close();
        f.close();
    }

    @Override
    public void savePLC(ProductionBlock plc) throws IOException, ClassNotFoundException {
        ArrayList<ProductionBlock> list = new ArrayList<>(readPLCFile());
        boolean found = false;
        int highestID = 0;

        for (ProductionBlock currentPLC : list) {
            if (currentPLC.getId() > highestID) {
                highestID = currentPLC.getId();
            }

            if (currentPLC.getId() == plc.getId()) { //IF PLC ALREADY EXISTS
                currentPLC.setName(plc.getName());
                currentPLC.setIpaddress(plc.getIpaddress());
                currentPLC.setPort(plc.getPort());
                found = true;
                break;
            }
        }
        if (!found) {
            plc.setId(highestID + 1);
            list.add(plc);
        }

        savePLC(list);
    }

    @Override
    public void removePLC(int plcToRemove) throws IOException, ClassNotFoundException {
        if (this.PLCListPath.isEmpty()) {
            System.out.println("Error: could not write PLClist file as there is found no valid path!");
            return;
        }

        ArrayList<ProductionBlock> list = new ArrayList<>(readPLCFile());

        Iterator<ProductionBlock> it = list.iterator();
        while (it.hasNext()) {
            ProductionBlock plc = it.next();
            if (plc.getId() == plcToRemove) {
                System.out.println("Removing ID: " + plcToRemove);
                it.remove();
                this.savePLC(list);
            }
        }
    }

    private String findPLCListPath() throws FileNotFoundException {
        String filename = "Scada/src/resources/PLClist.dat"; //File containing the PLC's as objects
        String filename1 = "/src/resources/PLClist.dat"; //File containing the PLC's as objects
        String filename2 = "src/resources/PLClist.dat"; //File containing the PLC's as objects

        File fileTest = new File("Scada");
        File fileTest1 = new File("/src");
        File fileTest2 = new File("src");
        if (fileTest.exists()) {
            return filename;
        } else if (fileTest1.exists()) {
            return filename1;
        } else if (fileTest2.exists()) {
            return filename2;
        } else {
            throw new FileNotFoundException("There was no valid paths found!");
        }
    }

}
