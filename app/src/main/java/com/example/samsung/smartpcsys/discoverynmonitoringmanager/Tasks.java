package com.example.samsung.smartpcsys.discoverynmonitoringmanager;

public class Tasks {
    private int id;
    private String type;
    private double instructionsSize;
    private double codeSize;
    private double inDataSize;
    private double outDataSize;
    private int nodeID;
    private boolean status;
    private double progress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getInstructionsSize() {
        return instructionsSize;
    }

    public void setInstructionsSize(double instructionsSize) {
        this.instructionsSize = instructionsSize;
    }

    public double getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(double codeSize) {
        this.codeSize = codeSize;
    }

    public double getInDataSize() {
        return inDataSize;
    }

    public void setInDataSize(double inDataSize) {
        this.inDataSize = inDataSize;
    }

    public double getOutDataSize() {
        return outDataSize;
    }

    public void setOutDataSize(double outDataSize) {
        this.outDataSize = outDataSize;
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
