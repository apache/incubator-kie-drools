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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.drools.core.util.StringUtils;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkException;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlannerBenchmarkRunner implements PlannerBenchmark {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final DefaultPlannerBenchmark plannerBenchmarkResult;

    private File benchmarkDirectory = null;
    private BenchmarkReport benchmarkReport = null;

    private ExecutorService executorService;

    private long startingSystemTimeMillis = -1L;
    private SingleBenchmarkRunner firstFailureSingleBenchmarkRunner = null;

    public PlannerBenchmarkRunner(DefaultPlannerBenchmark plannerBenchmarkResult) {
        this.plannerBenchmarkResult = plannerBenchmarkResult;
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
        List<SolverBenchmark> solverBenchmarkList = plannerBenchmarkResult.getSolverBenchmarkList();
        if (solverBenchmarkList == null || solverBenchmarkList.isEmpty()) {
            throw new IllegalArgumentException(
                    "The solverBenchmarkList (" + solverBenchmarkList + ") cannot be empty.");
        }
        initBenchmarkDirectoryAndSubdirs();
        executorService = Executors.newFixedThreadPool(plannerBenchmarkResult.getParallelBenchmarkCount());
        logger.info("Benchmarking started: solverBenchmarkList size ({}), parallelBenchmarkCount ({}).",
                solverBenchmarkList.size(), plannerBenchmarkResult.getParallelBenchmarkCount());
    }

    private void initBenchmarkDirectoryAndSubdirs() {
        if (benchmarkDirectory == null) {
            throw new IllegalArgumentException("The benchmarkDirectory (" + benchmarkDirectory + ") must not be null.");
        }
        // benchmarkDirectory usually already exists
        benchmarkDirectory.mkdirs();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(plannerBenchmarkResult.getStartingTimestamp());
        if (StringUtils.isEmpty(plannerBenchmarkResult.getName())) {
            plannerBenchmarkResult.setName(timestamp);
        }
        File benchmarkReportDirectory = new File(benchmarkDirectory, timestamp);
        boolean benchmarkReportDirectoryAdded = benchmarkReportDirectory.mkdirs();
        if (!benchmarkReportDirectoryAdded) {
            throw new IllegalArgumentException("The benchmarkReportDirectory (" + benchmarkReportDirectory
                    + ") creation failed. It probably already exists.");
        }
        benchmarkReport.setBenchmarkReportDirectory(benchmarkReportDirectory);
        benchmarkReport.initSubdirs();
    }

    private void warmUp() {
        if (plannerBenchmarkResult.getWarmUpTimeMillisSpend() > 0L) {
            logger.info("================================================================================");
            logger.info("Warming up started");
            logger.info("================================================================================");
            long startingTimeMillis = System.currentTimeMillis();
            long timeLeft = plannerBenchmarkResult.getWarmUpTimeMillisSpend();
            List<ProblemBenchmark> unifiedProblemBenchmarkList = plannerBenchmarkResult.getUnifiedProblemBenchmarkList();
            Iterator<ProblemBenchmark> it = unifiedProblemBenchmarkList.iterator();
            while (timeLeft > 0L) {
                if (!it.hasNext()) {
                    it = unifiedProblemBenchmarkList.iterator();
                }
                ProblemBenchmark problemBenchmark = it.next();
                timeLeft = problemBenchmark.warmUp(startingTimeMillis, plannerBenchmarkResult.getWarmUpTimeMillisSpend(), timeLeft);
            }
            logger.info("================================================================================");
            logger.info("Warming up ended");
            logger.info("================================================================================");
        }
    }

    protected void runSingleBenchmarks() {
        Map<SingleBenchmarkRunner, Future<SingleBenchmarkRunner>> futureMap
                = new HashMap<SingleBenchmarkRunner, Future<SingleBenchmarkRunner>>();
        for (ProblemBenchmark problemBenchmark : plannerBenchmarkResult.getUnifiedProblemBenchmarkList()) {
            for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmark.getSingleBenchmarkResultList()) {
                SingleBenchmarkRunner singleBenchmarkRunner = new SingleBenchmarkRunner(singleBenchmarkResult);
                Future<SingleBenchmarkRunner> future = executorService.submit(singleBenchmarkRunner);
                futureMap.put(singleBenchmarkRunner, future);
            }
        }
        // wait for the benchmarks to complete
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
                logger.error("The singleBenchmarkRunner (" + singleBenchmarkRunner.getName() + ") was interrupted.", e);
                failureThrowable = e;
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                logger.error("The singleBenchmarkRunner (" + singleBenchmarkRunner.getName() + ") failed.", cause);
                failureThrowable = cause;
            } catch (IllegalStateException e) {
                // TODO WORKAROUND Remove when PLANNER-46 is fixed.
                logger.error("The singleBenchmarkRunner (" + singleBenchmarkRunner.getName() + ") failed.", e);
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
        plannerBenchmarkResult.setBenchmarkTimeMillisSpend(calculateTimeMillisSpend());
        benchmarkReport.writeReport();
        if (plannerBenchmarkResult.getFailureCount() == 0) {
            logger.info("Benchmarking ended: time spend ({}), favoriteSolverBenchmark ({}), statistic html overview ({}).",
                    plannerBenchmarkResult.getBenchmarkTimeMillisSpend(),
                    plannerBenchmarkResult.getFavoriteSolverBenchmark().getName(),
                    benchmarkReport.getHtmlOverviewFile().getAbsolutePath());
        } else {
            logger.info("Benchmarking failed: time spend ({}), failureCount ({}), statistic html overview ({}).",
                    plannerBenchmarkResult.getBenchmarkTimeMillisSpend(),
                    plannerBenchmarkResult.getFailureCount(),
                    benchmarkReport.getHtmlOverviewFile().getAbsolutePath());
            throw new PlannerBenchmarkException("Benchmarking failed: failureCount ("
                    + plannerBenchmarkResult.getFailureCount() + ")." +
                    " The exception of the firstFailureSingleBenchmarkRunner ("
                    + firstFailureSingleBenchmarkRunner.getName() + ") is chained.",
                    firstFailureSingleBenchmarkRunner.getFailureThrowable());
        }
    }

    public long calculateTimeMillisSpend() {
        long now = System.currentTimeMillis();
        return now - startingSystemTimeMillis;
    }

}
