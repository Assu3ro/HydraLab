// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.
package com.microsoft.hydralab.performance;

/**
 * @author zhoule
 * @date 12/14/2022
 */

public interface PerformanceInspector {
    void initialize(PerformanceTestSpec performanceTestSpec);

    void capturePerformanceMatrix(PerformanceTestSpec performanceTestSpec);

    PerfResult<?> analyzeResult(PerformanceTestSpec performanceTestSpec);
}
