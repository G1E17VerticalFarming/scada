package scada.persistence;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;

public class ProductionBlock implements Serializable {

    private int status;
    private int batchId;
    private Date estimatedDone;
    private int growthConfigId;
    private int plantType;
    private String name;
    private String ipaddress;
    private int port;
    private int id;
    private double temp1;
    private double temp2;
    private int fanspeed;
    private double moisture;
    private String eta;
    private String lastOK;
    private String lastCheck;
    
    private String path;


    public ProductionBlock() throws FileNotFoundException {

        String filename = "Scada/src/resources/PLClist.dat"; //File containing the PLC's as objects
        String filename1 = "/src/resources/PLClist.dat"; //File containing the PLC's as objects
        String filename2 = "src/resources/PLClist.dat"; //File containing the PLC's as objects
        
        File fileTest = new File("Scada");
        File fileTest1 = new File("/src");
        File fileTest2 = new File("src");
        if(fileTest.exists()) {
            this.path = filename;
        } else if(fileTest1.exists()) {
            this.path = filename1;
        } else if(fileTest2.exists()) {
            this.path = filename2;
        } else {
            throw new FileNotFoundException("There was no valid paths found!");
        }
    }

    public ProductionBlock(int ID, String ip, int port, String name) throws FileNotFoundException {
        this();
        this.setIpaddress(ip);
        this.setPort(port);
        this.setId(ID);
        this.setName(name);
    }

    public String toString() {
        return "ID: " + getId() + "\t\tIP: " + getIpaddress() + "\tPort: " + getPort() + "\tName: " + getName();
    }

    public ArrayList readPLCFile() throws IOException, ClassNotFoundException {
        FileInputStream fi = null;
        ObjectInputStream in = null;
        
        ArrayList<ProductionBlock> list = new ArrayList<>();
        
        try {
            fi = new FileInputStream(new File(this.path));
            if (fi.getChannel().size() > 0) {
                in = new ObjectInputStream(fi); //Initiates the files content into ObjectInputStream
            }
            if (fi.getChannel().size() == 0) {
                System.out.println("No PLC objects found.");
            }
        } catch (FileNotFoundException e) {
            System.out.println(this.path + " blev ikke fundet.");
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
                exc.printStackTrace(); // for example
                break;
            }
        }

        return list;
    }

    public void writePLCFile(ArrayList<ProductionBlock> plcList) throws IOException {
        FileOutputStream f = new FileOutputStream(new File(this.path));
        ObjectOutputStream out = new ObjectOutputStream(f);
        for (ProductionBlock plc : plcList) {
            out.writeObject(plc);
        }
        out.close();
        f.close();
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public Date getEstimatedDone() {
        return estimatedDone;
    }

    public void setEstimatedDone(Date estimatedDone) {
        this.estimatedDone = estimatedDone;
    }

    public int getGrowthConfigId() {
        return growthConfigId;
    }

    public void setGrowthConfigId(int growthConfigId) {
        this.growthConfigId = growthConfigId;
    }

    public int getPlantType() {
        return plantType;
    }

    public void setPlantType(int plantType) {
        this.plantType = plantType;
    }

    public double getTemp1() {
        return temp1;
    }

    public void setTemp1(double temp1) {
        this.temp1 = temp1;
    }

    public double getTemp2() {
        return temp2;
    }

    public void setTemp2(double temp2) {
        this.temp2 = temp2;
    }

    public int getFanspeed() {
        return fanspeed;
    }

    public void setFanspeed(int fanspeed) {
        this.fanspeed = fanspeed;
    }

    public double getMoisture() {
        return moisture;
    }

    public void setMoisture(double moisture) {
        this.moisture = moisture;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getLastOK() {
        return lastOK;
    }

    public void setLastOK(String lastOK) {
        this.lastOK = lastOK;
    }

    public String getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(String lastCheck) {
        this.lastCheck = lastCheck;
    }
}
