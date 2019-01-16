package com.example.samsung.smartpcsys.resourcepool;

import com.example.samsung.smartpcsys.utils.TimestampConverter;

import java.io.Serializable;
import java.util.Date;

//@Entity(tableName = "routing_table")
public class RoutingTable implements Serializable{
//    @PrimaryKey (autoGenerate = true)
    private String sourceAddress;
    private String hostAddress;
    private String insertTime;

//    public RoutingTable(int id, String modelNo, String sourceAddress, String destAddress, String insertTime) {
//        this.id = id;
//        this.modelNo = modelNo;
//        this.sourceAddress = sourceAddress;
//        this.destAddress = destAddress;
//        this.insertTime = insertTime;
//    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }


}
