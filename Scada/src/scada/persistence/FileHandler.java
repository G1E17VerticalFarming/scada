/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.persistence;

import java.awt.BorderLayout;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import scada.domain.ReadWriteProductionBlock;

/**
 *
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
    public List<ProductionBlock> readPLCFile() throws IOException, ClassNotFoundException {
        if(this.PLCListPath.isEmpty()) {
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
            }
        }

        return list;
    }

    @Override
    public void writePLCFile(ArrayList<ProductionBlock> plcList) throws IOException {
        if(this.PLCListPath.isEmpty()) {
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
    
    private String findPLCListPath() throws FileNotFoundException {
        String filename = "Scada/src/resources/PLClist.dat"; //File containing the PLC's as objects
        String filename1 = "/src/resources/PLClist.dat"; //File containing the PLC's as objects
        String filename2 = "src/resources/PLClist.dat"; //File containing the PLC's as objects
        
        File fileTest = new File("Scada");
        File fileTest1 = new File("/src");
        File fileTest2 = new File("src");
        if(fileTest.exists()) {
            return filename;
        } else if(fileTest1.exists()) {
            return filename1;
        } else if(fileTest2.exists()) {
            return filename2;
        } else {
            throw new FileNotFoundException("There was no valid paths found!");
        }
    }
    
}
