// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.
package com.microsoft.hydralab.common.entity.common;

import com.microsoft.hydralab.common.entity.agent.MobileDevice;
import com.microsoft.hydralab.common.management.device.TestDeviceManager;
import com.microsoft.hydralab.common.management.listener.MobileDeviceState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString
public class DeviceInfo extends MobileDevice {
    public static final String ONLINE = MobileDeviceState.ONLINE.toString();
    public static final String OFFLINE = MobileDeviceState.OFFLINE.toString();
    // modified only in Center, sync with Agent
    public static final String TESTING = MobileDeviceState.TESTING.toString();
    // modified only in Agent, sync with Center
    public static final String UNSTABLE = MobileDeviceState.UNSTABLE.toString();
    private final transient Map<Thread, String> currentCommand = new HashMap<>();
    private final transient Map<Thread, Process> currentProcess = new HashMap<>();
    private final transient Map<Thread, TestTask> currentTask = new HashMap<>();
    private final transient Object lock = new Object();
    private final transient TestDeviceManager testDeviceManager;
    private String status;
    private String imageRelPath;
    private String pcImageRelPath;
    private String screenshotImageUrl;
    private String brand;
    private String abiList;
    private String recordVideoPath;
    private String pcScreenshotImageUrl;
    private String deviceId;
    private String runningTaskId;
    private String runningTestName;
    private String agentId;
    private Set<String> deviceGroup = new HashSet<>();
    private boolean supportScreenRecording = true;
    private long screenshotUpdateTimeMilli;
    private transient File screenshotImageFile;
    private transient File pcScreenshotImageFile;
    private transient boolean adbTimeout = false;
    private String type;

    public DeviceInfo() {
        this.testDeviceManager = null;
    }

    public DeviceInfo(TestDeviceManager testDeviceManager) {
        this.testDeviceManager = testDeviceManager;
    }

    public void setStatus(String status) {
        this.status = status;
        if (!isAlive()) {
            killAll();
        }
    }

    public String getCurrentCommandStr() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Thread, String> entry : currentCommand.entrySet()) {
            stringBuilder.append(entry.getKey().getName()).append(":\n").append(entry.getValue()).append("\n");
        }
        return stringBuilder.toString();
    }

    public void addCurrentCommand(String currentCommand) {
        //requireAlive(currentCommand);
        this.currentCommand.put(Thread.currentThread(), currentCommand);
    }

    public synchronized void finishCommand() {
        currentCommand.remove(Thread.currentThread());
        currentProcess.remove(Thread.currentThread());
    }

    private void requireAlive(Object extraMsgObj) {
        if (!isAlive()) {
            throw new RuntimeException("device status: " + status + ", not right " + this + "\n" + extraMsgObj);
        }
    }

    public void addCurrentProcess(Process currentProcess) {
        requireAlive(currentProcess);
        this.currentProcess.put(Thread.currentThread(), currentProcess);
    }

    public void addCurrentTask(TestTask testTask) {
        this.currentTask.put(Thread.currentThread(), testTask);
        this.status = DeviceInfo.TESTING;
        this.runningTaskId = testTask.getId();
    }

    public void finishTask() {
        this.currentTask.remove(Thread.currentThread());
        this.status = DeviceInfo.ONLINE;
        this.runningTaskId = null;
    }

    public void reset() {
        this.status = DeviceInfo.ONLINE;
        this.runningTaskId = null;
        killAll();
    }

    public boolean isAlive() {
        return DeviceInfo.TESTING.equals(status) || DeviceInfo.ONLINE.equals(status) || DeviceInfo.UNSTABLE.equals(status);
    }

    public boolean isTesting() {
        return DeviceInfo.TESTING.equals(status);
    }

    public boolean isOnline() {
        return DeviceInfo.ONLINE.equals(status);
    }

    public boolean isOffline() {
        return !isAlive() && !isUnstable();
    }

    public boolean isUnstable() {
        return DeviceInfo.UNSTABLE.equals(status);
    }

    public synchronized void killAll() {
        Set<Map.Entry<Thread, Process>> entries = currentProcess.entrySet();
        Set<Thread> threads = new HashSet<>(currentTask.keySet());
        ArrayList<Map.Entry<Thread, Process>> list = new ArrayList<>(entries);
        currentProcess.clear();
        currentCommand.clear();
        currentTask.clear();
        for (Map.Entry<Thread, Process> entry : list) {
            Process pro = entry.getValue();
            if (pro != null) {
                pro.destroyForcibly();
            }
        }
        for (Thread temp : threads) {
            temp.interrupt();
        }
    }
}
