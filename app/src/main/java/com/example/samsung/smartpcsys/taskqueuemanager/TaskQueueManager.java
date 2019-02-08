package com.example.samsung.smartpcsys.taskqueuemanager;

import android.os.Handler;
import android.util.Log;

import com.example.samsung.smartpcsys.resourcepool.Tasks;
import com.example.samsung.smartpcsys.taskqueue.TaskQueue;

public class TaskQueueManager {
    private String TAG = "TaskQueueManager";
    public static TaskQueue taskQueue = new TaskQueue();

    public void insertTask(Tasks task) {
        TaskQueue.taskQueue.add(task);
        Log.e(TAG, "TaskQueue Size: " + TaskQueue.taskQueue.size());
    }

    public void removeTask() {

    }

    public void lookForQueuedTasks() {
        if (TaskQueue.taskQueue.size() != 0) {
            for (int i = 0; i < TaskQueue.taskQueue.size(); i++) {
                TaskQueue.taskQueue.get(i).getTaskID();

            }
        }
    }

    private void allocationRequest() {
        final Handler mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                lookForQueuedTasks();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
