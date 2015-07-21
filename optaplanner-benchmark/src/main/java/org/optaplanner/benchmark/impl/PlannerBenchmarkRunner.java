/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.impl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkException;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.BenchmarkResultIO;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlannerBenchmarkRunner implements PlannerBenchmark {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());
    protected final transient Logger singleBenchmarkRunnerExceptionLogger = LoggerFactory.getLogger(
            getClass().getName() + ".singleBenchmarkRunnerException");

    private final PlannerBenchmarkResult plannerBenchmarkResult;

    private File benchmarkDirectory = null;
    private BenchmarkReport benchmarkReport = null;

    private ExecutorService executorService;
    private ExecutorService warmUpExecutorService;
    private BenchmarkResultIO benchmarkResultIO;

    private long startingSystemTimeMillis = -1L;
    private SingleBenchmarkRunner firstFailureSingleBenchmarkRunner = null;

    public PlannerBenchmarkRunner(PlannerBenchmarkResult plannerBenchmarkResult) {
        this.plannerBenchmarkResult = plannerBenchmarkResult;
    }

    public PlannerBenchmarkResult getPlannerBenchmarkResult() {
        return plannerBenchmarkResult;
    }

    public File getBenchmarkDirectory() {
        return benchmarkDirectory;
    }

    public void setBenchmarkDirectory(File benchmarkDirectory) {
        this.benchmarkDirectory = benchmarkDirectory;
    }

    public BenchmarkReport getBenchmarkReport() {
        return benchmarkReport;
    }

    public void setBenchmarkReport(BenchmarkReport benchmarkReport) {
        this.benchmarkReport = benchmarkReport;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    public void benchmark() {
        benchmarkingStarted();
        warmUp();
        runSingleBenchmarks();
        benchmarkingEnded();
    }

    public void benchmarkingStarted() {
        if (startingSystemTimeMillis >= 0L) {
            throw new IllegalStateException("This benchmark has already ran before.");
        }
        startingSystemTimeMillis = System.currentTimeMillis();
        plannerBenchmarkResult.setStartingTimestamp(new Date());
        List<SolverBenchmarkResult> solverBenchmarkResultList = plannerBenchmarkResult.getSolverBenchmarkResultList();
        if (ConfigUtils.isEmptyCollection(solverBenchmarkResultList)) {
            throw new IllegalArgumentException(
                    "The solverBenchmarkResultList (" + solverBenchmarkResultList + ") cannot be empty.");
        }
        initBenchmarkDirectoryAndSubdirs();
        plannerBenchmarkResult.initSystemProperties();
        executorService = Executors.newFixedThreadPool(plannerBenchmarkResult.getParallelBenchmarkCount());
        warmUpExecutorService = Executors.newFixedThreadPool(plannerBenchmarkResult.getParallelBenchmarkCount());
        benchmarkResultIO = new BenchmarkResultIO();
        logger.info("Benchmarking started: solverBenchmarkResultList size ({}), parallelBenchmarkCount ({}).",
                solverBenchmarkResultList.size(), plannerBenchmarkResult.getParallelBenchmarkCount());
    }

    private void initBenchmarkDirectoryAndSubdirs() {
        if (benchmarkDirectory == null) {
            throw new IllegalArgumentException("The benchmarkDirectory (" + benchmarkDirectory + ") must not be null.");
        }
        // benchmarkDirectory usually already exists
        benchmarkDirectory.mkdirs();
        plannerBenchmarkResult.initBenchmarkReportDirectory(benchmarkDirectory);
    }

    private void warmUp() {
        if (plannerBenchmarkResult.getWarmUpTimeMillisSpentLimit() <= 0L) {
            return;
        }
        logger.info("================================================================================");
        logger.info("Warm up started");
        logger.info("================================================================================");
        long timeLeft = plannerBenchmarkResult.getWarmUpTimeMillisSpentLimit();
        long timeEnd = System.currentTimeMillis() + timeLeft;
        Map<SingleBenchmarkRunner, Future<SingleBenchmarkRunner>> futureMap
                = new HashMap<SingleBenchmarkRunner, Future<SingleBenchmarkRunner>>();
        Map<SolverBenchmarkResult, TerminationConfig> originalTerminationConfigMap
                = new HashMap<SolverBenchmarkResult, TerminationConfig>();
        while (timeLeft > 0L) {
            warmUpStarted(futureMap, originalTerminationConfigMap, timeLeft);
            warmUp(futureMap, timeEnd);
            timeLeft = timeEnd - System.currentTimeMillis();
            futureMap.clear();
        }
        warmUpEnded(originalTerminationConfigMap);
        warmUpExecutorService.shutdownNow(); // Terminate warm up solvers early
        List<Runnable> notFinishedWarmUpList = warmUpExecutorService.shutdownNow(); // Make sure they're terminated
        if (!notFinishedWarmUpList.isEmpty()) {
            throw new IllegalStateException("Impossible state: notFinishedWarmUpList size (" + notFinishedWarmUpList + ").");
        }
        logger.info("================================================================================");
        logger.info("Warm up ended");
        logger.info("================================================================================");
    }

    private void warmUpStarted(Map<SingleBenchmarkRunner, Future<SingleBenchmarkRunner>> futureMap,
                               Map<SolverBenchmarkResult, TerminationConfig> originalTerminationConfigMap, long timeLeft) {

        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            TerminationConfig originalTerminationConfig = solverBenchmarkResult.getSolverConfig().getTerminationConfig();
            TerminationConfig tmpTerminationConfig = originalTerminationConfig == null
                    ? new TerminationConfig() : originalTerminationConfig.clone();
            tmpTerminationConfig.shortenTimeMillisSpentLimit(timeLeft);
            solverBenchmarkResult.getSolverConfig().setTerminationConfig(tmpTerminationConfig);
            originalTerminationConfigMap.put(solverBenchmarkResult, originalTerminationConfig);
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                SingleBenchmarkRunner singleBenchmarkRunner = new SingleBenchmarkRunner(singleBenchmarkResult);
                Future<SingleBenchmarkRunner> future = warmUpExecutorService.submit(singleBenchmarkRunner);
                futureMap.put(singleBenchmarkRunner, future);
            }
        }
    }

    private void warmUp(Map<SingleBenchmarkRunner, Future<SingleBenchmarkRunner>> futureMap, long timeEnd) {
        // Wait for the warm up benchmarks to complete or time-out
        for (Map.Entry<SingleBenchmarkRunner, Future<SingleBenchmarkRunner>> futureEntry : futureMap.entrySet()) {
            SingleBenchmarkRunner singleBenchmarkRunner = futureEntry.getKey();
            Future<SingleBenchmarkRunner> future = futureEntry.getValue();
            Throwable failureThrowable = null;
            try {
                // Explicitly returning it in the Callable guarantees memory visibility
                long timeLeft = timeEnd - System.currentTimeMillis();
                singleBenchmarkRunner = future.get(timeLeft, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                singleBenchmarkRunnerExceptionLogger.error("The warm up singleBenchmarkRunner ({}) was interrupted.",
                        singleBenchmarkRunner, e);
                failureThrowable = e;
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                singleBenchmarkRunnerExceptionLogger.warn("The warm up singleBenchmarkRunner ({}) failed.",
                        singleBenchmarkRunner.getName(), cause);
                failureThrowable = cause;
            } catch (TimeoutException e) {
                // Do not update failureThrowable on TimeoutException as it is expected to occur
                singleBenchmarkRunnerExceptionLogger.debug("The warm up singleBenchmarkRunner ({}) timed out.",
                        singleBenchmarkRunner);
                singleBenchmarkRunner.terminateSolverEarly();
                future.cancel(true);
            }

            if (failureThrowable != null) {
                singleBenchmarkRunner.setFailureThrowable(failureThrowable);
                if (firstFailureSingleBenchmarkRunner == null) {
                    firstFailureSingleBenchmarkRunner = singleBenchmarkRunner;
                }
            }
        }
    }

    private void warmUpEnded(Map<SolverBenchmarkResult, TerminationConfig> originalTerminationConfigMap) {
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            TerminationConfig originalTerminationConfig = originalTerminationConfigMap.get(solverBenchmarkResult);
            solverBenchmarkResult.getSolverConfig().setTerminationConfig(originalTerminationConfig);
        }
    }

    protected void runSingleBenchmarks() {
        Map<SingleBenchmarkRunner, Future<SingleBenchmarkRunner>> futureMap
                = new HashMap<SingleBenchmarkRunner, Future<SingleBenchmarkRunner>>();
        for (ProblemBenchmarkResult problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
            for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
                SingleBenchmarkRunner singleBenchmarkRunner = new SingleBenchmarkRunner(singleBenchmarkResult);
                Future<SingleBenchmarkRunner> future = executorService.submit(singleBenchmarkRunner);
                futureMap.put(singleBenchmarkRunner, future);
            }
        }
        // Wait for the benchmarks to complete
        for (Map.Entry<SingleBenchmarkRunner, Future<SingleBenchmarkRunner>> futureEntry : futureMap.entrySet()) {
            SingleBenchmarkRunner singleBenchmarkRunner = futureEntry.getKey();
            Future<SingleBenchmarkRunner> future = futureEntry.getValue();
            Throwable failureThrowable = null;
            try {
                // Explicitly returning it in the Callable guarantees memory visibility
                singleBenchmarkRunner = future.get();
                // TODO WORKAROUND Remove when PLANNER-46 is fixed.
                if (singleBenchmarkRunner.getSingleBenchmarkResult().getScore() == null) {
                    throw new IllegalStateException("Score is null. TODO fix PLANNER-46.");
                }
            } catch (InterruptedException e) {
                singleBenchmarkRunnerExceptionLogger.error("The singleBenchmarkRunner ({}) was interrupted.",
                        singleBenchmarkRunner, e);
                failureThrowable = e;
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                singleBenchmarkRunnerExceptionLogger.warn("The singleBenchmarkRunner ({}) failed.",
                        singleBenchmarkRunner, cause);
                failureThrowable = cause;
            } catch (IllegalStateException e) {
                // TODO WORKAROUND Remove when PLANNER-46 is fixed.
                singleBenchmarkRunnerExceptionLogger.warn("The singleBenchmarkRunner ({}) failed.",
                        singleBenchmarkRunner, e);
                failureThrowable = e;
            }
            if (failureThrowable == null) {
                singleBenchmarkRunner.getSingleBenchmarkResult().setSucceeded(true);
            } else {
                singleBenchmarkRunner.getSingleBenchmarkResult().setSucceeded(false);
                singleBenchmarkRunner.setFailureThrowable(failureThrowable);
                if (firstFailureSingleBenchmarkRunner == null) {
                    firstFailureSingleBenchmarkRunner = singleBenchmarkRunner;
                }
            }
        }
    }

    public void benchmarkingEnded() {
        List<Runnable> notExecutedBenchmarkList = executorService.shutdownNow();
        if (!notExecutedBenchmarkList.isEmpty()) {
            throw new IllegalStateException("Impossible state: notExecutedBenchmarkList size ("
                    + notExecutedBenchmarkList + ").");
        }
        plannerBenchmarkResult.setBenchmarkTimeMillisSpent(calculateTimeMillisSpent());
        benchmarkResultIO.writePlannerBenchmarkResult(plannerBenchmarkResult.getBenchmarkReportDirectory(),
                plannerBenchmarkResult);
        benchmarkReport.writeReport();
        if (plannerBenchmarkResult.getFailureCount() == 0) {
            logger.info("Benchmarking ended: time spent ({}), favoriteSolverBenchmark ({}), statistic html overview ({}).",
                    plannerBenchmarkResult.getBenchmarkTimeMillisSpent(),
                    plannerBenchmarkResult.getFavoriteSolverBenchmarkResult().getName(),
                    benchmarkReport.getHtmlOverviewFile().getAbsolutePath());
        } else {
            logger.info("Benchmarking failed: time spent ({}), failureCount ({}), statistic html overview ({}).",
                    plannerBenchmarkResult.getBenchmarkTimeMillisSpent(),
                    plannerBenchmarkResult.getFailureCount(),
                    benchmarkReport.getHtmlOverviewFile().getAbsolutePath());
            throw new PlannerBenchmarkException("Benchmarking failed: failureCount ("
                    + plannerBenchmarkResult.getFailureCount() + ")." +
                    " The exception of the firstFailureSingleBenchmarkRunner ("
                    + firstFailureSingleBenchmarkRunner.getName() + ") is chained.",
                    firstFailureSingleBenchmarkRunner.getFailureThrowable());
        }
    }

    public long calculateTimeMillisSpent() {
        long now = System.currentTimeMillis();
        return now - startingSystemTimeMillis;
    }

}
