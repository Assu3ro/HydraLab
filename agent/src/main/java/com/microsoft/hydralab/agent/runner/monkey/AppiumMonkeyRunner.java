// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.hydralab.agent.runner.monkey;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.img.gif.AnimatedGifEncoder;
import com.microsoft.hydralab.agent.runner.TestTaskRunCallback;
import com.microsoft.hydralab.agent.runner.appium.AppiumRunner;
import com.microsoft.hydralab.common.entity.common.AndroidTestUnit;
import com.microsoft.hydralab.common.entity.common.TestRun;
import com.microsoft.hydralab.common.entity.common.TestTask;
import com.microsoft.hydralab.common.management.AgentManagementService;
import com.microsoft.hydralab.common.management.device.TestDevice;
import com.microsoft.hydralab.performance.PerformanceTestManagementService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AppiumMonkeyRunner extends AppiumRunner {
    private final AnimatedGifEncoder e = new AnimatedGifEncoder();

    public AppiumMonkeyRunner(AgentManagementService agentManagementService,
                              TestTaskRunCallback testTaskRunCallback,
                              PerformanceTestManagementService performanceTestManagementService) {
        super(agentManagementService, testTaskRunCallback, performanceTestManagementService);
    }

    @Override
    protected File runAndGetGif(File appiumJarFile, String appiumCommand, TestDevice testDevice, TestTask testTask,
                                TestRun testRun, File deviceTestResultFolder, Logger reportLogger) {
        String pkgName = testTask.getPkgName();

        long recordingStartTimeMillis = System.currentTimeMillis();

        testDevice.startScreenRecorder(deviceTestResultFolder, testTask.getTimeOutSecond(), reportLogger);
        testDevice.startLogCollector(pkgName, testRun, reportLogger);
        testRun.setTotalCount(1);

        AndroidTestUnit ongoingMonkeyTest = new AndroidTestUnit();
        ongoingMonkeyTest.setNumtests(testRun.getTotalCount());
        ongoingMonkeyTest.setStartTimeMillis(System.currentTimeMillis());
        ongoingMonkeyTest.setRelStartTimeInVideo(ongoingMonkeyTest.getStartTimeMillis() - recordingStartTimeMillis);
        ongoingMonkeyTest.setCurrentIndexNum(1);
        ongoingMonkeyTest.setTestName("Appium_Monkey_Test");
        ongoingMonkeyTest.setTestedClass(pkgName);
        ongoingMonkeyTest.setDeviceTestResultId(testRun.getId());
        ongoingMonkeyTest.setTestTaskId(testRun.getTestTaskId());

        reportLogger.info(ongoingMonkeyTest.getTitle());

        performanceTestManagementService.testRunStarted();

        testRun.addNewTimeTag(1 + ". " + ongoingMonkeyTest.getTitle(),
                System.currentTimeMillis() - recordingStartTimeMillis);
        testDevice.setRunningTestName(ongoingMonkeyTest.getTitle());
        File gifFile = new File(testRun.getResultFolder(), pkgName + ".gif");
        e.start(gifFile.getAbsolutePath());
        e.setDelay(1000);
        e.setRepeat(0);
        testDevice.updateScreenshotImageAsyncDelay(TimeUnit.SECONDS.toMillis(5),
                (imagePNGFile -> {
                    if (imagePNGFile == null || !e.isStarted()) {
                        return;
                    }
                    try {
                        e.addFrame(ImgUtil.toBufferedImage(ImgUtil.scale(ImageIO.read(imagePNGFile), 0.3f)));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }), reportLogger);
        testRun.setTestStartTimeMillis(System.currentTimeMillis());

        performanceTestManagementService.testStarted(ongoingMonkeyTest.getTitle());

        testDevice.runAppiumMonkey(pkgName, testTask.getMaxStepCount(), reportLogger);

        testDevice.stopScreenRecorder();
        testDevice.stopLogCollector();

        // Success status
        if (StringUtils.isNotEmpty(testRun.getCrashStack())) {
            // Fail
            ongoingMonkeyTest.setStatusCode(AndroidTestUnit.StatusCodes.FAILURE);
            ongoingMonkeyTest.setSuccess(false);
            ongoingMonkeyTest.setStack(e.toString());
            testRun.setSuccess(false);
            performanceTestManagementService.testFailure(ongoingMonkeyTest.getTitle());
            testRun.addNewTimeTagBeforeLast(ongoingMonkeyTest.getTitle() + ".fail",
                    System.currentTimeMillis() - recordingStartTimeMillis);
            testRun.oneMoreFailure();
        } else {
            // Pass
            ongoingMonkeyTest.setStatusCode(AndroidTestUnit.StatusCodes.OK);
            ongoingMonkeyTest.setSuccess(true);
            testRun.setSuccess(true);
            performanceTestManagementService.testSuccess(ongoingMonkeyTest.getTitle());
        }

        // Test finish
        reportLogger.info(ongoingMonkeyTest.getTitle() + ".end");
        performanceTestManagementService.testRunFinished();
        ongoingMonkeyTest.setEndTimeMillis(System.currentTimeMillis());
        testDevice.setRunningTestName(null);
        testRun.addNewTestUnit(ongoingMonkeyTest);
        testRun.addNewTimeTag(ongoingMonkeyTest.getTitle() + ".end",
                System.currentTimeMillis() - recordingStartTimeMillis);
        testRun.onTestEnded();
        return gifFile;
    }
}
