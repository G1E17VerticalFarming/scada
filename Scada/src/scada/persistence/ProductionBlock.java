package scada.persistence;

import java.io.Serializable;
import java.util.Date;

public class ProductionBlock implements Serializable {

    private String status;
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
    private String lastOK = "N/A";
    private String lastCheck;


    public ProductionBlock() {
        
    }

    public ProductionBlock(int ID, String ip, int port, String name) {
        this();
        this.setIpaddress(ip);
        this.setPort(port);
        this.setId(ID);
        this.setName(name);
    }

    public String toString() {
        return "ID: " + getId() + "\t\tIP: " + getIpaddress() + "\tPort: " + getPort() + "\tName: " + getName();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
