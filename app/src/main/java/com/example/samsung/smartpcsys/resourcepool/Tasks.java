package com.example.samsung.smartpcsys.resourcepool;

import java.util.ArrayList;

public class Tasks {
    private int taskID;
    private String sourceAddress;
    private int priority;
    private Status status;
    private ArrayList<TaskFiles> taskFilesList;
    private ArrayList<DataFiles> dataFilesList;


    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        CREATED, WAITING, ASSIGNED, CANCELED, COMPLETED
    }

    public ArrayList<TaskFiles> getTaskFilesList() {
        return taskFilesList;
    }

    public void setTaskFilesList(ArrayList<TaskFiles> taskFilesList) {
        this.taskFilesList = taskFilesList;
    }

    public static class TaskFiles {
        private String taskFileType;
        private String taskFileName;
        private int loc;
        private String taskFileSize;
        private String codeLocation;

        public String getTaskFileType() {
            return taskFileType;
        }

        public void setTaskFileType(String taskFileType) {
            this.taskFileType = taskFileType;
        }

        public int getLoc() {
            return loc;
        }

        public void setLoc(int loc) {
            this.loc = loc;
        }

        public String getTaskFileSize() {
            return taskFileSize;
        }

        public void setTaskFileSize(String taskFileSize) {
            this.taskFileSize = taskFileSize;
        }

        public String getTaskFileName() {
            return taskFileName;
        }

        public void setTaskFileName(String taskFileName) {
            this.taskFileName = taskFileName;
        }

        public String getCodeLocation() {
            return codeLocation;
        }

        public void setCodeLocation(String codeLocation) {
            this.codeLocation = codeLocation;
        }
    }

    public ArrayList<DataFiles> getDataFilesList() {
        return dataFilesList;
    }

    public void setDataFilesList(ArrayList<DataFiles> dataFilesList) {
        this.dataFilesList = dataFilesList;
    }

    public static class DataFiles {
        private String inDataSize;
        private String inDataFileName;
        private String inDataType;
        private String inDataLocation;

        public String getInDataSize() {
            return inDataSize;
        }

        public void setInDataSize(String inDataSize) {
            this.inDataSize = inDataSize;
        }

        public String getInDataType() {
            return inDataType;
        }

        public void setInDataType(String inDataType) {
            this.inDataType = inDataType;
        }

        public String getInDataLocation() {
            return inDataLocation;
        }

        public void setInDataLocation(String inDataLocation) {
            this.inDataLocation = inDataLocation;
        }

        public String getInDataFileName() {
            return inDataFileName;
        }

        public void setInDataFileName(String inDataFileName) {
            this.inDataFileName = inDataFileName;
        }
    }

//    @Override
//    public String toString(){
//        return "Task MetaData [taskID=" + id
//    }

}
//    private double instructionsSize;
//    private double outDataSize;
//    private double progress;