// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.hydralab.agent.runner;

import cn.hutool.core.lang.Assert;
import com.microsoft.hydralab.common.entity.common.DeviceAction;
import com.microsoft.hydralab.common.entity.common.TestRun;
import com.microsoft.hydralab.common.entity.common.TestTask;
import com.microsoft.hydralab.common.management.AgentManagementService;
import com.microsoft.hydralab.common.management.device.TestDevice;
import com.microsoft.hydralab.common.util.DateUtil;
import com.microsoft.hydralab.common.util.LogUtils;
import com.microsoft.hydralab.common.util.ThreadPoolUtil;
import com.microsoft.hydralab.common.util.ThreadUtils;
import com.microsoft.hydralab.performance.InspectionStrategy;
import com.microsoft.hydralab.performance.PerformanceTestManagementService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class TestRunner {
    protected final Logger log = LoggerFactory.getLogger(TestRunner.class);
    protected final AgentManagementService agentManagementService;
    protected final TestTaskRunCallback testTaskRunCallback;
    protected final PerformanceTestManagementService performanceTestManagementService;
    protected final XmlBuilder xmlBuilder = new XmlBuilder();
    protected final ActionExecutor actionExecutor = new ActionExecutor();

    public TestRunner(AgentManagementService agentManagementService, TestTaskRunCallback testTaskRunCallback,
                      PerformanceTestManagementService performanceTestManagementService) {
        this.agentManagementService = agentManagementService;
        this.testTaskRunCallback = testTaskRunCallback;
        this.performanceTestManagementService = performanceTestManagementService;
    }

    public void runTestOnDevice(TestTask testTask, TestDevice testDevice, Logger logger) {
        checkTestTaskCancel(testTask);
        logger.info("Start running tests {}, timeout {}s", testTask.getTestSuite(), testTask.getTimeOutSecond());

        TestRun testRun = createTestRun(testDevice, testTask, logger);
        checkTestTaskCancel(testTask);

        try {
            setUp(testDevice, testTask, testRun);
            checkTestTaskCancel(testTask);
            runByFutureTask(testDevice, testTask, testRun);
        } catch (Exception e) {
            testRun.getLogger().error(testDevice.getSerialNum() + ": " + e.getMessage(), e);
            saveErrorSummary(testRun, e);
        } finally {
            tearDown(testDevice, testTask, testRun);
        }
    }

    private void runByFutureTask(TestDevice testDevice, TestTask testTask, TestRun testRun) throws Exception {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            loadTestRunToCurrentThread(testRun);
            run(testDevice, testTask, testRun);
            return null;
        });
        ThreadPoolUtil.TEST_EXECUTOR.execute(futureTask);
        try {
            if (testTask.getTimeOutSecond() > 0) {
                futureTask.get(testTask.getTimeOutSecond(), TimeUnit.SECONDS);
            } else {
                futureTask.get();
            }
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            futureTask.cancel(true);
            stopTest(testDevice);
            throw e;
        }
    }

    /**
     * TODO Call {@link TestRunThreadContext#init(ITestRun)}
     * This method must be called in the test run execution thread.
     */
    private void loadTestRunToCurrentThread(TestRun testRun) {
        TestRunThreadContext.init(testRun);
    }

    private static void saveErrorSummary(TestRun testRun, Exception e) {
        String errorStr = e.getClass().getName() + ": " + e.getMessage();
        if (errorStr.length() > 255) {
            errorStr = errorStr.substring(0, 254);
        }
        testRun.setErrorInProcess(errorStr);
    }

    protected void checkTestTaskCancel(TestTask testTask) {
        Assert.isFalse(testTask.isCanceled(), "Task {} is canceled", testTask.getId());
    }

    protected TestRun createTestRun(TestDevice testDevice, TestTask testTask, Logger parentLogger) {
        TestRun testRun = new TestRun(testDevice.getSerialNum(), testDevice.getName(), testTask.getId());
        File testRunResultFolder = new File(testTask.getResourceDir(), testDevice.getSerialNum());
        parentLogger.info("DeviceTestResultFolder {}", testRunResultFolder);
        if (!testRunResultFolder.exists()) {
            if (!testRunResultFolder.mkdirs()) {
                throw new RuntimeException("testRunResultFolder.mkdirs() failed: " + testRunResultFolder);
            }
        }

        testRun.setResultFolder(testRunResultFolder);
        Logger loggerForTestRun = createLoggerForTestRun(testRun, testTask.getTestSuite(), parentLogger);
        testRun.setLogger(loggerForTestRun);
        testTask.addTestedDeviceResult(testRun);
        return testRun;
    }

    protected void setUp(TestDevice testDevice, TestTask testTask, TestRun testRun) throws Exception {
        testDevice.killAll();
        // this key will be used to recover device status when lost the connection between agent and master
        testDevice.addCurrentTask(testTask);
        loadTestRunToCurrentThread(testRun);
        /* set up device */
        testRun.getLogger().info("Start setup device");
        testDevice.testDeviceSetup(testRun.getLogger());
        testDevice.wakeUpDevice(testRun.getLogger());
        ThreadUtils.safeSleep(1000);
        checkTestTaskCancel(testTask);
        reInstallApp(testDevice, testTask, testRun.getLogger());
        reInstallTestApp(testDevice, testTask, testRun.getLogger());

        //execute actions
        if (testTask.getDeviceActions() != null) {
            testRun.getLogger().info("Start executing setUp actions.");
            List<Exception> exceptions = actionExecutor.doActions(testDevice, testRun.getLogger(),
                    testTask.getDeviceActions(), DeviceAction.When.SET_UP);
            Assert.isTrue(exceptions.size() == 0, () -> exceptions.get(0));
        }

        testRun.getLogger().info("Start granting all package needed permissions device");
        testDevice.grantAllTaskNeededPermissions(testTask, testRun.getLogger());

        checkTestTaskCancel(testTask);
        testDevice.getScreenShot(testRun.getLogger());

        if (performanceTestManagementService != null && testTask.getInspectionStrategies() != null) {
            for (InspectionStrategy strategy : testTask.getInspectionStrategies()) {
                performanceTestManagementService.inspectWithStrategy(strategy);
            }
        }
    }

    protected abstract void run(TestDevice testDevice, TestTask testTask, TestRun testRun) throws Exception;

    protected void tearDown(TestDevice testDevice, TestTask testTask, TestRun testRun) {
        // stop performance test
        if (performanceTestManagementService != null) {
            performanceTestManagementService.testTearDown(testDevice, log);
        }

        //execute actions
        if (testTask.getDeviceActions() != null) {
            testRun.getLogger().info("Start executing tearDown actions.");
            List<Exception> exceptions = actionExecutor.doActions(testDevice, testRun.getLogger(),
                    testTask.getDeviceActions(), DeviceAction.When.TEAR_DOWN);
            if (exceptions.size() > 0) {
                testRun.getLogger().error("Execute actions failed when tearDown!", exceptions.get(0));
            }
        }
        testDevice.testDeviceUnset(testRun.getLogger());

        //generate xml report and upload files
        if (testRun.getTotalCount() > 0) {
            try {
                String absoluteReportPath = xmlBuilder.buildTestResultXml(testTask, testRun);
                testRun.setTestXmlReportPath(agentManagementService.getTestBaseRelPathInUrl(new File(absoluteReportPath)));
            } catch (Exception e) {
                testRun.getLogger().error("Error in buildTestResultXml", e);
            }
        }
        if (testTaskRunCallback != null) {
            try {
                testTaskRunCallback.onOneDeviceComplete(testTask, testDevice, testRun.getLogger(), testRun);
            } catch (Exception e) {
                testRun.getLogger().error("Error in onOneDeviceComplete", e);
            }
        }
        testRun.getLogger().info("Start Close/finish resource");
        LogUtils.releaseLogger(testRun.getLogger());
    }

    protected void reInstallApp(TestDevice testDevice, TestTask testTask, Logger reportLogger) throws Exception {
        if (testTask.getRequireReinstall()) {
            testDevice.uninstallApp(testTask.getPkgName(), reportLogger);
            ThreadUtils.safeSleep(3000);
        } else if (testTask.getRequireClearData()) {
            testDevice.resetPackage(testTask.getPkgName(), reportLogger);
        }
        checkTestTaskCancel(testTask);

        testDevice.installApp(testTask.getAppFile().getAbsolutePath(), reportLogger);
    }

    protected void reInstallTestApp(TestDevice testDevice, TestTask testTask, Logger reportLogger)
            throws Exception {
        if (!shouldInstallTestPackageAsApp()) {
            return;
        }
        if (testTask.getTestAppFile() == null) {
            return;
        }
        if (StringUtils.isEmpty(testTask.getTestPkgName())) {
            return;
        }
        if (!testTask.getTestAppFile().exists()) {
            return;
        }
        if (testTask.getRequireReinstall()) {
            testDevice.uninstallApp(testTask.getTestPkgName(), reportLogger);
            // test package uninstall should be faster than app package removal.
            ThreadUtils.safeSleep(2000);
        }
        checkTestTaskCancel(testTask);
        testDevice.installApp(testTask.getTestAppFile().getAbsolutePath(), reportLogger);
    }

    protected boolean shouldInstallTestPackageAsApp() {
        return false;
    }

    private Logger createLoggerForTestRun(TestRun testRun, String loggerNamePrefix, Logger parentLogger) {
        parentLogger.info("Start setup report child parentLogger");
        String dateInfo = DateUtil.fileNameDateFormat.format(new Date());
        File instrumentLogFile = new File(testRun.getResultFolder(), loggerNamePrefix + "_" + dateInfo + ".log");
        // make sure it's a child logger of the parentLogger
        String loggerName = parentLogger.getName() + ".test." + dateInfo;
        Logger reportLogger =
                LogUtils.getLoggerWithRollingFileAppender(loggerName, instrumentLogFile.getAbsolutePath(),
                        "%d %p -- %m%n");
        testRun.setInstrumentReportPath(agentManagementService.getTestBaseRelPathInUrl(instrumentLogFile));

        return reportLogger;
    }

    public void stopTest(TestDevice testDevice) {
        testDevice.killAll();
    }
}
