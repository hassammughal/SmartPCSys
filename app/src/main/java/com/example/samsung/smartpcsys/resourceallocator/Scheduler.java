package com.example.samsung.smartpcsys.resourceallocator;

import android.util.Log;

import com.example.samsung.smartpcsys.dispatcher.TaskManager;
import com.example.samsung.smartpcsys.resourcepool.Node;
import com.example.samsung.smartpcsys.resourcepool.Tasks;
import com.example.samsung.smartpcsys.taskqueue.TaskQueue;

import java.util.ArrayList;

public class Scheduler {
    private String TAG = "Scheduler";

    private static int getIndex(int taskID) {
        int index = 0;
        for (int i = 1; i < TaskQueue.taskQueue.size(); i++) {
            if (TaskQueue.taskQueue.get(i).getTaskID() == taskID) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void Schedule(Tasks task, String fileLocation, ArrayList<Node> nodes) {

        if (task.getPriority() == 1) {
            String nodeIP = getNodewithMaxSpecs(nodes);
            TaskManager taskManager = new TaskManager();
            taskManager.createPacket(task, fileLocation, nodeIP);
        }
    }

    private String getNodewithMaxSpecs(ArrayList<Node> nodes) {

        String nodeIP = null;
        if (nodes.size() != 0) {
            for (int i = 0; i < nodes.size(); i++) {
                for (int j = i + 1; j < nodes.size(); j++) {
                    String[] ram = nodes.get(i).getTotalRAM().split("\\s+");
                    String[] rams = nodes.get(j).getTotalRAM().split("\\s+");
                    double ram1 = Double.parseDouble(ram[0]);
                    double ram2 = Double.parseDouble(rams[0]);
                    Log.e(TAG, "Value of ram1: " + ram1 + ", Value of ram2: " + ram2);
                    if (ram1 > ram2) { //nodes.get(i).getTotalCPUSpeed() >= nodes.get(j).getTotalCPUSpeed() &&
                        nodeIP = nodes.get(i).getIpAddress();
                        Log.e(TAG, "NodeIP: " + nodeIP + ", with RAM: " + ram1);
                        return nodeIP;
                    } else if (ram2 > ram1) {  //nodes.get(j).getTotalCPUSpeed() >= nodes.get(i).getTotalCPUSpeed() &&
                        nodeIP = nodes.get(j).getIpAddress();
                        Log.e(TAG, "NodeIP: " + nodeIP + ", with RAM: " + ram2);
                        return nodeIP;
                    }
                }
            }
        }
        return null;
    }
}
