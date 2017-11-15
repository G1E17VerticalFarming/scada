package scada.persistence;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;

public class ProductionBlock implements Serializable {

    private int status;
    private int batchId;
    private Date estimatedDone;
    private int growthConfigId;
    private int plantType;
    private String ipaddress;
    private int port;
    private int id;

    public ProductionBlock() {

    }

    public ProductionBlock(int ID, String ip, int port) {
        this.ipaddress = ip;
        this.port = port;
        this.id = ID;
    }

    public String toString() {
        return "ID: " + getId() + "\t\tIP: " + getIpaddress() + "\tPort: " + getPort();
    }

    public ArrayList readPLCFile() throws IOException, ClassNotFoundException {
        FileInputStream fi = null;
        ObjectInputStream in = null;
        String filename = "Scada/src/resources/PLClist.dat"; //File containing the PLC's as objects
        ArrayList<ProductionBlock> list = new ArrayList<>();

        try {
            fi = new FileInputStream(new File(filename));
            if (fi.getChannel().size() > 0) {
                in = new ObjectInputStream(fi); //Initiates the files content into ObjectInputStream
            }
            if (fi.getChannel().size() == 0) {
                System.out.println("No PLC objects found.");
            }
        } catch (FileNotFoundException e) {
            System.out.println(filename + " blev ikke fundet.");
        }


        while (in != null) {
            try {
                ProductionBlock plc = (ProductionBlock) in.readObject();
                System.out.println("Object found: " + plc.toString());
                list.add(plc);
            } catch (SocketTimeoutException exc) {
                // you got the timeout
            } catch (EOFException exc) {
                in.close();
                in = null;
            } catch (IOException exc) {
                // some other I/O error: print it, log it, etc.
                exc.printStackTrace(); // for example
            }
        }

        return list;
    }

    public void writePLCFile(ArrayList<ProductionBlock> plcList) throws IOException {
        FileOutputStream f = new FileOutputStream(new File("Scada/src/resources/PLClist.dat"));
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
}
