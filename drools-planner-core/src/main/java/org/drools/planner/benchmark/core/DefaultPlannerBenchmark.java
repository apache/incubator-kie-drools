/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.benchmark.core;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.drools.planner.benchmark.core.comparator.TotalScoreSolverBenchmarkComparator;
import org.drools.planner.benchmark.core.statistic.ProblemStatisticType;
import org.drools.planner.benchmark.core.statistic.StatisticManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPlannerBenchmark implements PlannerBenchmark {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private File benchmarkDirectory = null;
    private File benchmarkInstanceDirectory = null;
    private File outputSolutionFilesDirectory = null;
    private File statisticDirectory = null;
    private List<ProblemStatisticType> problemStatisticTypeList = null;
    private Comparator<SolverBenchmark> solverBenchmarkComparator = null;

    private Long warmUpTimeMillisSpend = null;

    private List<SolverBenchmark> solverBenchmarkList = null;
    private List<ProblemBenchmark> unifiedProblemBenchmarkList = null;

    private SolverBenchmark winningSolverBenchmark = null;

    public File getBenchmarkDirectory() {
        return benchmarkDirectory;
    }

    public void setBenchmarkDirectory(File benchmarkDirectory) {
        this.benchmarkDirectory = benchmarkDirectory;
    }

    public File getBenchmarkInstanceDirectory() {
        return benchmarkInstanceDirectory;
    }

    public void setBenchmarkInstanceDirectory(File benchmarkInstanceDirectory) {
        this.benchmarkInstanceDirectory = benchmarkInstanceDirectory;
    }

    public File getOutputSolutionFilesDirectory() {
        return outputSolutionFilesDirectory;
    }

    public void setOutputSolutionFilesDirectory(File outputSolutionFilesDirectory) {
        this.outputSolutionFilesDirectory = outputSolutionFilesDirectory;
    }

    public File getStatisticDirectory() {
        return statisticDirectory;
    }

    public void setStatisticDirectory(File statisticDirectory) {
        this.statisticDirectory = statisticDirectory;
    }

    public List<ProblemStatisticType> getProblemStatisticTypeList() {
        return problemStatisticTypeList;
    }

    public void setProblemStatisticTypeList(List<ProblemStatisticType> problemStatisticTypeList) {
        this.problemStatisticTypeList = problemStatisticTypeList;
    }

    public Comparator<SolverBenchmark> getSolverBenchmarkComparator() {
        return solverBenchmarkComparator;
    }

    public void setSolverBenchmarkComparator(Comparator<SolverBenchmark> solverBenchmarkComparator) {
        this.solverBenchmarkComparator = solverBenchmarkComparator;
    }

    public Long getWarmUpTimeMillisSpend() {
        return warmUpTimeMillisSpend;
    }

    public void setWarmUpTimeMillisSpend(Long warmUpTimeMillisSpend) {
        this.warmUpTimeMillisSpend = warmUpTimeMillisSpend;
    }

    public List<SolverBenchmark> getSolverBenchmarkList() {
        return solverBenchmarkList;
    }

    public void setSolverBenchmarkList(List<SolverBenchmark> solverBenchmarkList) {
        this.solverBenchmarkList = solverBenchmarkList;
    }

    public List<ProblemBenchmark> getUnifiedProblemBenchmarkList() {
        return unifiedProblemBenchmarkList;
    }

    public void setUnifiedProblemBenchmarkList(List<ProblemBenchmark> unifiedProblemBenchmarkList) {
        this.unifiedProblemBenchmarkList = unifiedProblemBenchmarkList;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    public void benchmark() {
        benchmarkingStarted();
        warmUp();
        for (ProblemBenchmark problemBenchmark : unifiedProblemBenchmarkList) {
            problemBenchmark.benchmark();
        }
        benchmarkingEnded();
    }

    public void benchmarkingStarted() {
        if (solverBenchmarkList == null || solverBenchmarkList.isEmpty()) {
            throw new IllegalArgumentException(
                    "The solverBenchmarkList (" + solverBenchmarkList + ") cannot be empty.");
        }
        initBenchmarkDirectoryAndSubdirs();
        if (solverBenchmarkComparator == null) {
            solverBenchmarkComparator = new TotalScoreSolverBenchmarkComparator();
        }
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            solverBenchmark.benchmarkingStarted();
        }
        for (ProblemBenchmark problemBenchmark : unifiedProblemBenchmarkList) {
            problemBenchmark.setOutputSolutionFilesDirectory(outputSolutionFilesDirectory);
            problemBenchmark.benchmarkingStarted();
        }
        logger.info("Benchmarking started: solverBenchmarkList size ({}).",
                solverBenchmarkList.size());
    }

    private void initBenchmarkDirectoryAndSubdirs() {
        if (benchmarkDirectory == null) {
            throw new IllegalArgumentException("The benchmarkDirectory (" + benchmarkDirectory + ") must not be null.");
        }
        benchmarkDirectory.mkdirs();
        if (benchmarkInstanceDirectory == null) {
            String timestampDirectory = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
            benchmarkInstanceDirectory = new File(benchmarkDirectory, timestampDirectory);
        }
        benchmarkInstanceDirectory.mkdirs();
        if (outputSolutionFilesDirectory == null) {
            outputSolutionFilesDirectory = new File(benchmarkInstanceDirectory, "output");
        }
        outputSolutionFilesDirectory.mkdirs();
        if (statisticDirectory == null) {
            statisticDirectory = new File(benchmarkInstanceDirectory, "statistic");
        }
        statisticDirectory.mkdirs();
    }

    private void warmUp() {
        if (warmUpTimeMillisSpend != null) {
            logger.info("================================================================================");
            logger.info("Warming up");
            logger.info("================================================================================");
            long startingTimeMillis = System.currentTimeMillis();
            long timeLeft = warmUpTimeMillisSpend;
            Iterator<ProblemBenchmark> it = unifiedProblemBenchmarkList.iterator();
            while (timeLeft > 0L) {
                if (!it.hasNext()) {
                    it = unifiedProblemBenchmarkList.iterator();
                }
                ProblemBenchmark problemBenchmark = it.next();
                timeLeft = problemBenchmark.warmUp(startingTimeMillis, warmUpTimeMillisSpend, timeLeft);
            }
            logger.info("================================================================================");
            logger.info("Finished warmUp");
            logger.info("================================================================================");
        }
    }

    public void benchmarkingEnded() {
        for (ProblemBenchmark problemBenchmark : unifiedProblemBenchmarkList) {
            problemBenchmark.benchmarkingEnded();
        }
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            solverBenchmark.benchmarkingEnded();
        }
        determineRanking();
        StatisticManager statisticManager = new StatisticManager(benchmarkInstanceDirectory.getName(),
                statisticDirectory, unifiedProblemBenchmarkList);
        statisticManager.writeStatistics(solverBenchmarkList);
        logger.info("Benchmarking ended: winning solverBenchmark ({}), statistic html overview ({}).",
                winningSolverBenchmark.getName(), statisticManager.getHtmlOverviewFile().getAbsolutePath());
    }

    private void determineRanking() {
        List<SolverBenchmark> sortedSolverBenchmarkList = new ArrayList<SolverBenchmark>(solverBenchmarkList);
        Collections.sort(sortedSolverBenchmarkList, solverBenchmarkComparator);
        Collections.reverse(sortedSolverBenchmarkList); // Best results first, worst results last
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            int ranking = sortedSolverBenchmarkList.indexOf(solverBenchmark);
            solverBenchmark.setRanking(ranking);
            if (ranking == 0) {
                winningSolverBenchmark = solverBenchmark;
            }
        }
    }

    // TODO Temporarily disabled because it crashes because of http://jira.codehaus.org/browse/XSTR-666
//    public void writeBenchmarkResult(XStream xStream) {
//        File benchmarkResultFile = new File(benchmarkInstanceDirectory, "benchmarkResult.xml");
//        OutputStreamWriter writer = null;
//        try {
//            writer = new OutputStreamWriter(new FileOutputStream(benchmarkResultFile), "UTF-8");
//            xStream.toXML(this, writer);
//        } catch (UnsupportedEncodingException e) {
//            throw new IllegalStateException("This JVM does not support UTF-8 encoding.", e);
//        } catch (FileNotFoundException e) {
//            throw new IllegalArgumentException(
//                    "Could not create benchmarkResultFile (" + benchmarkResultFile + ").", e);
//        } finally {
//            IOUtils.closeQuietly(writer);
//        }
//    }

}
