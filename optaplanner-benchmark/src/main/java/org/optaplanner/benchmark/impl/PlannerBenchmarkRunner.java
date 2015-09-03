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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkException;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.BenchmarkResultIO;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.PureSingleStatistic;
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

    private ExecutorService warmUpExecutorService;
    private ExecutorCompletionService<SingleBenchmarkRunner> warmUpExecutorCompletionService;
    private ExecutorService executorService;
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
        warmUpExecutorService = Executors.newFixedThreadPool(plannerBenchmarkResult.getParallelBenchmarkCount());
        warmUpExecutorCompletionService = new ExecutorCompletionService<SingleBenchmarkRunner>(warmUpExecutorService);
        executorService = Executors.newFixedThreadPool(plannerBenchmarkResult.getParallelBenchmarkCount());
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
        long timeLeftTotal = plannerBenchmarkResult.getWarmUpTimeMillisSpentLimit();
        int parallelBenchmarkCount = plannerBenchmarkResult.getParallelBenchmarkCount();
        int solverBenchmarkResultCount = plannerBenchmarkResult.getSolverBenchmarkResultList().size();
        long cyclesCount = Math.round(Math.ceil(solverBenchmarkResultCount / (double) parallelBenchmarkCount));
        long timeLeftPerCycle = Math.round(Math.floor((timeLeftTotal / (double) cyclesCount)));
        Map<ProblemBenchmarkResult, List<ProblemStatistic>> originalProblemStatisticMap
                = new HashMap<ProblemBenchmarkResult, List<ProblemStatistic>>(plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList().size());
        ConcurrentMap<SolverBenchmarkResult, Integer> singleBenchmarkResultIndexMap
                = new ConcurrentHashMap<SolverBenchmarkResult, Integer>(solverBenchmarkResultCount);

        Map<SolverBenchmarkResult, WarmUpConfigBackup> warmUpConfigBackupMap = WarmUpConfigBackup.backupBenchmarkConfig(plannerBenchmarkResult, originalProblemStatisticMap);
        SolverBenchmarkResult[] solverBenchmarkResultCycle = new SolverBenchmarkResult[parallelBenchmarkCount];
        int solverBenchmarkResultIndex = 0;
        for (int i = 0; i < cyclesCount; i++) {
            long timeCycleEnd = System.currentTimeMillis() + timeLeftPerCycle;
            for (int j = 0; j < parallelBenchmarkCount; j++) {
                solverBenchmarkResultCycle[j] = plannerBenchmarkResult.
                        getSolverBenchmarkResultList().get(solverBenchmarkResultIndex % solverBenchmarkResultCount);
                solverBenchmarkResultIndex++;
            }
            ConcurrentMap<Future<SingleBenchmarkRunner>, SingleBenchmarkRunner> futureMap
                    = new ConcurrentHashMap<Future<SingleBenchmarkRunner>, SingleBenchmarkRunner>(parallelBenchmarkCount);
            warmUpPopulate(futureMap, singleBenchmarkResultIndexMap, solverBenchmarkResultCycle, timeLeftPerCycle);
            warmUp(futureMap, singleBenchmarkResultIndexMap, timeCycleEnd);
        }
        WarmUpConfigBackup.restoreBenchmarkConfig(plannerBenchmarkResult, originalProblemStatisticMap, warmUpConfigBackupMap);
        List<Runnable> notFinishedWarmUpList = warmUpExecutorService.shutdownNow();
        if (!notFinishedWarmUpList.isEmpty()) {
            throw new IllegalStateException("Impossible state: notFinishedWarmUpList (" + notFinishedWarmUpList
                    + ") is not empty.");
        }
        logger.info("================================================================================");
        logger.info("Warm up ended");
        logger.info("================================================================================");
    }

    private void warmUpPopulate(Map<Future<SingleBenchmarkRunner>, SingleBenchmarkRunner> futureMap,
            ConcurrentMap<SolverBenchmarkResult, Integer> singleBenchmarkResultIndexMap,
            SolverBenchmarkResult[] solverBenchmarkResultArray, long timeLeftPerSolverConfig) {
        for (SolverBenchmarkResult solverBenchmarkResult : solverBenchmarkResultArray) {
            TerminationConfig originalTerminationConfig = solverBenchmarkResult.getSolverConfig().getTerminationConfig();
            TerminationConfig tmpTerminationConfig = originalTerminationConfig == null
                    ? new TerminationConfig() : originalTerminationConfig.clone();
            tmpTerminationConfig.shortenTimeMillisSpentLimit(timeLeftPerSolverConfig);
            solverBenchmarkResult.getSolverConfig().setTerminationConfig(tmpTerminationConfig);

            Integer singleBenchmarkResultIndex = singleBenchmarkResultIndexMap.get(solverBenchmarkResult);
            singleBenchmarkResultIndex = (singleBenchmarkResultIndex == null) ? 0 : singleBenchmarkResultIndex % solverBenchmarkResult.getSingleBenchmarkResultList().size();
            SingleBenchmarkResult singleBenchmarkResult
                    = solverBenchmarkResult.getSingleBenchmarkResultList().get(singleBenchmarkResultIndex);
            SingleBenchmarkRunner singleBenchmarkRunner = new SingleBenchmarkRunner(singleBenchmarkResult);
            Future<SingleBenchmarkRunner> future = warmUpExecutorCompletionService.submit(singleBenchmarkRunner);
            futureMap.put(future, singleBenchmarkRunner);
            singleBenchmarkResultIndexMap.put(solverBenchmarkResult, singleBenchmarkResultIndex + 1);
        }
    }

    private void warmUp(Map<Future<SingleBenchmarkRunner>, SingleBenchmarkRunner> futureMap,
            ConcurrentMap<SolverBenchmarkResult, Integer> singleBenchmarkResultIndexMap, long timePhaseEnd) {
        // Wait for the warm up benchmarks to complete
        int tasksCount = futureMap.size();
        // Use a counter because completion order of futures is different from input order
        for (int i = 0; i < tasksCount; i++) {
            Future<SingleBenchmarkRunner> future;
            try {
                future = warmUpExecutorCompletionService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Waiting for a warm up singleBenchmarkRunner was interrupted.", e);
            }

            Throwable failureThrowable = null;
            SingleBenchmarkRunner singleBenchmarkRunner;
            try {
                // Explicitly returning it in the Callable guarantees memory visibility
                singleBenchmarkRunner = future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                singleBenchmarkRunner = futureMap.get(future);
                singleBenchmarkRunnerExceptionLogger.error("The warm up singleBenchmarkRunner ({}) was interrupted.",
                        singleBenchmarkRunner, e);
                failureThrowable = e;
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                singleBenchmarkRunner = futureMap.get(future);
                singleBenchmarkRunnerExceptionLogger.warn("The warm up singleBenchmarkRunner ({}) failed.",
                        singleBenchmarkRunner, cause);
                failureThrowable = cause;
            }
            if (failureThrowable != null) {
                singleBenchmarkRunner.setFailureThrowable(failureThrowable);
                if (firstFailureSingleBenchmarkRunner == null) {
                    firstFailureSingleBenchmarkRunner = singleBenchmarkRunner;
                }
            }

            SolverBenchmarkResult solverBenchmarkResult = singleBenchmarkRunner.getSingleBenchmarkResult().getSolverBenchmarkResult();
            long timeLeftInCycle = timePhaseEnd - System.currentTimeMillis();
            if (timeLeftInCycle > 0L) {
                SolverBenchmarkResult[] solverBenchmarkResultSingleton = new SolverBenchmarkResult[]{solverBenchmarkResult};
                warmUpPopulate(futureMap, singleBenchmarkResultIndexMap, solverBenchmarkResultSingleton, timeLeftInCycle);
                tasksCount++;
            }
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
                Thread.currentThread().interrupt();
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

    private static final class WarmUpConfigBackup {

        private final TerminationConfig terminationConfig;
        private final Map<SingleBenchmarkResult, List<PureSingleStatistic>> pureSingleStatisticMap;

        public WarmUpConfigBackup(TerminationConfig terminationConfig) {
            this.terminationConfig = terminationConfig;
            this.pureSingleStatisticMap = new HashMap<SingleBenchmarkResult, List<PureSingleStatistic>>();
        }

        public Map<SingleBenchmarkResult, List<PureSingleStatistic>> getPureSingleStatisticMap() {
            return pureSingleStatisticMap;
        }

        public TerminationConfig getTerminationConfig() {
            return terminationConfig;
        }

        private static void restoreBenchmarkConfig(PlannerBenchmarkResult plannerBenchmarkResult, Map<ProblemBenchmarkResult, List<ProblemStatistic>> originalProblemStatisticMap, Map<SolverBenchmarkResult, WarmUpConfigBackup> warmUpConfigBackupMap) {
            for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
                WarmUpConfigBackup warmUpConfigBackup = warmUpConfigBackupMap.get(solverBenchmarkResult);
                TerminationConfig originalTerminationConfig = warmUpConfigBackup.getTerminationConfig();
                solverBenchmarkResult.getSolverConfig().setTerminationConfig(originalTerminationConfig);
                for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                    singleBenchmarkResult.setPureSingleStatisticList(warmUpConfigBackup.getPureSingleStatisticMap().get(singleBenchmarkResult));
                    ProblemBenchmarkResult problemBenchmarkResult = singleBenchmarkResult.getProblemBenchmarkResult();
                    if (problemBenchmarkResult.getProblemStatisticList() == null || problemBenchmarkResult.getProblemStatisticList().size() <= 0) {
                        problemBenchmarkResult.setProblemStatisticList(originalProblemStatisticMap.get(problemBenchmarkResult));
                    }
                    singleBenchmarkResult.initSingleStatisticMap();
                }
            }
        }

        private static Map<SolverBenchmarkResult, WarmUpConfigBackup> backupBenchmarkConfig(PlannerBenchmarkResult plannerBenchmarkResult, Map<ProblemBenchmarkResult, List<ProblemStatistic>> originalProblemStatisticMap) { // backup & remove stats, backup termination config
            Map<SolverBenchmarkResult, WarmUpConfigBackup> warmUpConfigBackupMap = new HashMap<SolverBenchmarkResult, WarmUpConfigBackup>(plannerBenchmarkResult.getSolverBenchmarkResultList().size());
            for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
                TerminationConfig originalTerminationConfig = solverBenchmarkResult.getSolverConfig().getTerminationConfig();
                WarmUpConfigBackup warmUpConfigBackup = new WarmUpConfigBackup(originalTerminationConfig);
                for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                    List<PureSingleStatistic> originalPureSingleStatisticList = singleBenchmarkResult.getPureSingleStatisticList();
                    List<PureSingleStatistic> singleBenchmarkStatisticPutResult = warmUpConfigBackup.getPureSingleStatisticMap().put(singleBenchmarkResult, originalPureSingleStatisticList);
                    if (singleBenchmarkStatisticPutResult != null) {
                        throw new IllegalStateException("SingleBenchmarkStatisticMap of WarmUpConfigBackup ( " + warmUpConfigBackup
                                + " ) already contained key ( " + singleBenchmarkResult + " ) with value ( "
                                + singleBenchmarkStatisticPutResult + " ).");
                    }
                    singleBenchmarkResult.setPureSingleStatisticList(Collections.<PureSingleStatistic>emptyList());

                    ProblemBenchmarkResult problemBenchmarkResult = singleBenchmarkResult.getProblemBenchmarkResult();
                    List<ProblemStatistic> originalProblemStatisticList = problemBenchmarkResult.getProblemStatisticList();
                    if (!originalProblemStatisticMap.containsKey(problemBenchmarkResult)) { // TODO: After Java 8, do Map#putIfAbsent
                        List<ProblemStatistic> problemStatisticPutResult = originalProblemStatisticMap.put(problemBenchmarkResult, originalProblemStatisticList);
                        if (problemStatisticPutResult != null) {
                            throw new IllegalStateException("OriginalProblemStatisticMap already contained key ( "
                                    + problemBenchmarkResult + " ) with value ( "
                                    + problemStatisticPutResult + " ).");
                        }
                    }
                    singleBenchmarkResult.getProblemBenchmarkResult().setProblemStatisticList(Collections.<ProblemStatistic>emptyList());
                    singleBenchmarkResult.initSingleStatisticMap();
                }
                WarmUpConfigBackup warmUpConfigBackupPutResult = warmUpConfigBackupMap.put(solverBenchmarkResult, warmUpConfigBackup);
                if (warmUpConfigBackupPutResult != null) {
                    throw new IllegalStateException("WarmUpConfigBackupMap already contained key ( " + solverBenchmarkResult
                            + " ) with value ( " + warmUpConfigBackupPutResult + " ).");
                }
            }
            return warmUpConfigBackupMap;
        }
    }

}
