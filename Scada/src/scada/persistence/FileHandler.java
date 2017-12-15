package scada.persistence;

import scada.domain.interfaces.ReadWriteGrowthProfile;
import scada.domain.interfaces.ReadWriteLog;
import scada.domain.interfaces.ReadWriteProductionBlock;
import shared.GrowthProfile;
import shared.Log;
import shared.ProductionBlock;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Class which handles writing to and reading from local files.
 */
public class FileHandler implements ReadWriteProductionBlock, ReadWriteLog, ReadWriteGrowthProfile {
    
    private String resourcesDir;
    private static FileHandler instance = null;

    /**
     * Private constructor
     */
    private FileHandler() {
        try {
            this.resourcesDir = this.findPLCListPath();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            System.out.println(" - Meaning no methods are possible in read/writing a production block!");
        }
    }

    /**
     * Singleton design pattern
     * @return The only instance of FileHandler
     */
    public static FileHandler getInstance() {
        if (instance == null) {
            instance = new FileHandler();
        }
        return instance;
    }


    @Override
    public List<ProductionBlock> readPLCFile() throws IOException {
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
                in = new ObjectInputStream(fi);
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
                System.out.println("Error: " + exc);
                break;
            } catch (EOFException exc) {
                System.out.println("Error: " + exc);
                in.close();
                in = null;
                break;
            } catch (IOException exc) {
                System.out.println("Error: " + exc);
                break;
            } catch (ClassNotFoundException e) {
                System.out.println(e);
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
    public void saveUpdatePLCList(ArrayList<ProductionBlock> pbList) throws IOException {
        if(this.resourcesDir.isEmpty()) {
            System.out.println("Error: could not write file as there is found no valid path!");
            return;
        }
        
        ProductionBlock[] pbArr = pbList.toArray(new ProductionBlock[0]);
        JsonEncoder.stringifyObject(this.resourcesDir + "/pbUpdateList.json", pbArr);
    }

    @Override
    public void saveDeletePLCList(ArrayList<ProductionBlock> pbList) throws IOException {
        if(this.resourcesDir.isEmpty()) {
            System.out.println("Error: could not write file as there is found no valid path!");
            return;
        }
        
        ProductionBlock[] pbArr = pbList.toArray(new ProductionBlock[0]);
        JsonEncoder.stringifyObject(this.resourcesDir + "/pbDeleteList.json", pbArr);
    }

    @Override
    public void removePLC(int plcToRemove) throws IOException {
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

    /**
     * Internal method to locate the PLC list, since the location was different
     * depending on the OS you run.
     * @return The filepath which contains the PLC list
     * @throws FileNotFoundException If the file cannot be found in any of the locations
     */
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
    public List<Log> readLogFile() throws IOException {
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
    public List<GrowthProfile> readGrowthProfileFile() throws IOException {
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
