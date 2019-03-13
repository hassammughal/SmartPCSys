package com.example.samsung.smartpcsys.dispatcher;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.samsung.smartpcsys.communicationmanager.CommunicationManager;
import com.example.samsung.smartpcsys.packets.TIMPacket;
import com.example.samsung.smartpcsys.resourcepool.Tasks;
import com.example.samsung.smartpcsys.utils.SngltonClass;
import com.example.samsung.smartpcsys.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import bsh.EvalError;
import bsh.Interpreter;

public class TaskManager {
    private String TAG = "TaskManager";
    private Interpreter interpreter = new Interpreter();
    private CommunicationManager communicationManager = new CommunicationManager();
    static final int MESSAGE_READ = 1;


    public void createPacket(Tasks task, String filePath, String hostAddress) {
        File source = new File(filePath);
        //utils.readBytesFromFile(source);
        Log.e(TAG, "FilePath: " + filePath + " Filename: " + source.getName());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(source);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int fileSize = (int) source.length();
        Log.e(TAG, "File Size: " + fileSize);
        byte[] buf = new byte[fileSize];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                bos.write(buf, 0, readNum); //no doubt here is 0
                //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                Log.e(TAG, "read " + readNum + " bytes,");
            }
        } catch (IOException ex) {
            Log.e(TAG, "IOException:" + ex);
        }
        byte[] bytes = bos.toByteArray();
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(hostAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        TIMPacket timPacket = new TIMPacket(4, task.getTaskID(), bytes, inetAddress);
        communicationManager.sendPacket(timPacket);
    }

    public String taskandDataPacket(Tasks task, String hostAddress) {

        String taskFileLocation = null;
        String json = Utils.readFromFile(task.getTaskID());
        try {
            JSONObject jsonObject = new JSONObject(json);

            int taskId = jsonObject.getInt("taskID");
            String sourceAddress = jsonObject.getString("sourceAddress");
            String priority = jsonObject.getString("priority");
            String status = jsonObject.getString("status");
            Log.e(TAG, "Values in JSON: taskID: " + taskId + ", SourceAddress: " + sourceAddress + ", priority: " + priority + ", status: " + status);

            JSONArray jsonArray = jsonObject.getJSONArray("TaskFileDetails");
            String taskFileName, taskFileType, taskFileSize;
            int loc;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTaskObject = jsonArray.getJSONObject(i);
                taskFileName = jsonTaskObject.getString("taskFileName");
                taskFileType = jsonTaskObject.getString("taskFileType");
                taskFileSize = jsonTaskObject.getString("taskFileSize");
                taskFileLocation = jsonTaskObject.getString("taskFileLocation");
                loc = jsonTaskObject.getInt("linesOfCode");

                Log.e(TAG, "Values in JSONTASKARRAYLIST: TaskFileName: " + taskFileName + ", TaskFileType: " + taskFileType + ", TaskFileSize: " + taskFileSize +
                        ", TaskFileLocation: " + taskFileLocation + ", LineOfCode: " + loc);
            }

            JSONArray jsonDataArray = jsonObject.getJSONArray("DataFileDetails");
            String dataFileName, dataFileType, dataFileSize, dataFileLocation;
            for (int j = 0; j < jsonDataArray.length(); j++) {
                JSONObject jsonDataObject = jsonDataArray.getJSONObject(j);
                dataFileName = jsonDataObject.getString("dataFileName");
                dataFileType = jsonDataObject.getString("dataFileType");
                dataFileSize = jsonDataObject.getString("dataFileSize");
                dataFileLocation = jsonDataObject.getString("dataFileLocation");

                Log.e(TAG, "Values in JSONDATAARRAYLIST: DataFileName: " + dataFileName + ", DataFileType: " + dataFileType + ", DataFileSize: " + dataFileSize +
                        ", DataFileLocation: " + dataFileLocation);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return taskFileLocation;
    }

    public void onTIMPRcv(String hostAddress) {

        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        Log.e(TAG, "Text/File Received!");
                        File desti = new File(Environment.getExternalStorageDirectory() + "/SmartPCSys/Receive/Fibonacci.java");
                        if (!desti.exists()) {
                            try {
                                desti.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e(TAG, "FileName: " + desti.getName() + " File Path: " + desti.getAbsolutePath());
                        byte[] readBuff = (byte[]) msg.obj;
                        String tempMsg = new String(readBuff, 0, msg.arg1);
                        Log.e(TAG, "Message: " + tempMsg);
                        try {
                            interpreter.set("Context", SngltonClass.get().getApplicationContext());
                            interpreter.eval(tempMsg);

                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(desti);
                                fos.write(readBuff);
                                fos.flush();
                                fos.close();

                                Utils.writeBytesToFile(desti, readBuff);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } catch (EvalError evalError) {
                            evalError.printStackTrace();
                            Toast.makeText(SngltonClass.get().getApplicationContext(), "error: " + evalError.toString(), Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                return true;
            }
        });
    }

}
