/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.persistence;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import scada.domain.interfaces.ReadWriteGrowthProfile;
import scada.domain.interfaces.ReadWriteLog;

import scada.domain.interfaces.ReadWriteProductionBlock;
import shared.GrowthProfile;
import shared.Log;
import shared.ProductionBlock;

/**
 * @author DanielToft
 */
public class FileHandler implements ReadWriteProductionBlock, ReadWriteLog, ReadWriteGrowthProfile {
    
    private String resourcesDir;
    private static FileHandler instance = null;

    private FileHandler() {
        try {
            this.resourcesDir = this.findPLCListPath();
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
    public List<ProductionBlock> readPLCFile() throws IOException, ClassNotFoundException {
        if(this.resourcesDir.isEmpty()) {
            System.out.println("Error: could not read file as there is found no valid path!");
            return null;
        }

        FileInputStream fi = null;
        ObjectInputStream in = null;

        ArrayList<ProductionBlock> list = new ArrayList<>();
        String path = this.resourcesDir + "/PLClist.dat";
        try {
            fi = new FileInputStream(new File(path));
            if (fi.getChannel().size() > 0) {
                in = new ObjectInputStream(fi); //Initiates the files content into ObjectInputStream
            }
            if (fi.getChannel().size() == 0) {
                System.out.println("No PLC objects found.");
            }
        } catch (FileNotFoundException e) {
            System.out.println(path + " blev ikke fundet.");
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
        if (this.resourcesDir.isEmpty()) {
            System.out.println("Error: could not write PLClist file as there is found no valid path!");
            return;
        }

        FileOutputStream f = new FileOutputStream(new File(this.resourcesDir + "/PLClist.dat"));
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
        if (this.resourcesDir.isEmpty()) {
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
        String filename = "Scada/src/resources"; //File containing the PLC's as objects
        String filename1 = "/src/resources"; //File containing the PLC's as objects
        String filename2 = "src/resources"; //File containing the PLC's as objects
        String filename3 = "scada/Scada/src/resources"; //File containing the PLC's as objects
        
        File fileTest = new File(filename);
        File fileTest1 = new File(filename1);
        File fileTest2 = new File(filename2);
        File fileTest3 = new File(filename3);
        if(fileTest.exists()) {
            return filename;
        } else if (fileTest1.exists()) {
            return filename1;
        } else if (fileTest2.exists()) {
            return filename2;
        } else if(fileTest3.exists()) {
            return filename3;
        }else {
            throw new FileNotFoundException("There was no valid paths found!");
        }
    }
    
    @Override
    public List<Log> readLogFile() throws IOException, ClassNotFoundException {
        if(this.resourcesDir.isEmpty()) {
            System.out.println("Error: could not write file as there is found no valid path!");
            return null;
        }
        
        Object obj = JsonEncoder.getJSON(this.resourcesDir + "/logs.json", Log[].class);
        if(obj == null) {
            return null;
        }
        
        Log[] logs = (Log[])obj;
        return new ArrayList<>(Arrays.asList(logs));
    }

    @Override
    public void writeLogFile(List<Log> logList) throws IOException {
        if(this.resourcesDir.isEmpty()) {
            System.out.println("Error: could not write file as there is found no valid path!");
            return;
        }
        
        Log[] logs = logList.toArray(new Log[0]);
        JsonEncoder.stringifyObject(this.resourcesDir + "/logs.json", logs);
    }

    @Override
    public List<GrowthProfile> readGrowthProfileFile() throws IOException, ClassNotFoundException {
        if(this.resourcesDir.isEmpty()) {
            System.out.println("Error: could not write file as there is found no valid path!");
            return null;
        }
        
        Object obj = JsonEncoder.getJSON(this.resourcesDir + "/growthprofiles.json", GrowthProfile[].class);
        if(obj == null) {
            return null;
        }
        
        GrowthProfile[] gps = (GrowthProfile[])obj;
        return new ArrayList<>(Arrays.asList(gps));
    }

    @Override
    public void writeGrowthProfileFile(List<GrowthProfile> gpList) throws IOException {
        if(this.resourcesDir.isEmpty()) {
            System.out.println("Error: could not write file as there is found no valid path!");
            return;
        }
        
        GrowthProfile[] gps = gpList.toArray(new GrowthProfile[0]);
        JsonEncoder.stringifyObject(this.resourcesDir + "/growthprofiles.json", gps);
    }
}
