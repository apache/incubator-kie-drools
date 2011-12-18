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

package org.drools.planner.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.drools.planner.benchmark.statistic.SolverStatistic;
import org.drools.planner.benchmark.statistic.SolverStatisticType;
import org.drools.planner.benchmark.statistic.StatisticManager;
import org.drools.planner.config.termination.TerminationConfig;
import org.drools.planner.core.Solver;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolver;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XStreamAlias("solverBenchmarkSuite")
public class SolverBenchmarkSuite {

    private static final NumberFormat TIME_FORMAT = NumberFormat.getIntegerInstance(Locale.ENGLISH);

    @XStreamOmitField
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private File benchmarkDirectory = null;
    private File benchmarkInstanceDirectory = null;
    private File solvedSolutionFilesDirectory = null;
    private File solverStatisticFilesDirectory = null;
    @XStreamImplicit(itemFieldName = "solverStatisticType")
    private List<SolverStatisticType> solverStatisticTypeList = null;
    private Comparator<SolverBenchmark> solverBenchmarkComparator = null;

    private Long warmUpTimeMillisSpend = null;
    private Long warmUpSecondsSpend = null;
    private Long warmUpMinutesSpend = null;
    private Long warmUpHoursSpend = null;

    private SolverBenchmark inheritedSolverBenchmark = null;

    @XStreamImplicit(itemFieldName = "solverBenchmark")
    private List<SolverBenchmark> solverBenchmarkList = null;

//    @XStreamImplicit(itemFieldName = "solverBenchmarkSuiteResult")
//    private List<SolverBenchmarkSuiteResult> solverBenchmarkSuiteResultList;

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

    public File getSolvedSolutionFilesDirectory() {
        return solvedSolutionFilesDirectory;
    }

    public void setSolvedSolutionFilesDirectory(File solvedSolutionFilesDirectory) {
        this.solvedSolutionFilesDirectory = solvedSolutionFilesDirectory;
    }

    public File getSolverStatisticFilesDirectory() {
        return solverStatisticFilesDirectory;
    }

    public void setSolverStatisticFilesDirectory(File solverStatisticFilesDirectory) {
        this.solverStatisticFilesDirectory = solverStatisticFilesDirectory;
    }

    public List<SolverStatisticType> getSolverStatisticTypeList() {
        return solverStatisticTypeList;
    }

    public void setSolverStatisticTypeList(List<SolverStatisticType> solverStatisticTypeList) {
        this.solverStatisticTypeList = solverStatisticTypeList;
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

    public Long getWarmUpSecondsSpend() {
        return warmUpSecondsSpend;
    }

    public void setWarmUpSecondsSpend(Long warmUpSecondsSpend) {
        this.warmUpSecondsSpend = warmUpSecondsSpend;
    }

    public Long getWarmUpMinutesSpend() {
        return warmUpMinutesSpend;
    }

    public void setWarmUpMinutesSpend(Long warmUpMinutesSpend) {
        this.warmUpMinutesSpend = warmUpMinutesSpend;
    }

    public Long getWarmUpHoursSpend() {
        return warmUpHoursSpend;
    }

    public void setWarmUpHoursSpend(Long warmUpHoursSpend) {
        this.warmUpHoursSpend = warmUpHoursSpend;
    }

    public List<SolverBenchmark> getSolverBenchmarkList() {
        return solverBenchmarkList;
    }

    public void setSolverBenchmarkList(List<SolverBenchmark> solverBenchmarkList) {
        this.solverBenchmarkList = solverBenchmarkList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public void benchmarkingStarted() {
        if (solverBenchmarkList == null || solverBenchmarkList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <solverBenchmark> in the <solverBenchmarkSuite> configuration.");
        }
        Set<String> nameSet = new HashSet<String>(solverBenchmarkList.size());
        Set<SolverBenchmark> noNameBenchmarkSet = new LinkedHashSet<SolverBenchmark>(solverBenchmarkList.size());
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            if (solverBenchmark.getName() != null) {
                boolean unique = nameSet.add(solverBenchmark.getName());
                if (!unique) {
                    throw new IllegalStateException("The benchmark name (" + solverBenchmark.getName()
                            + ") is used in more than 1 benchmark.");
                }
            } else {
                noNameBenchmarkSet.add(solverBenchmark);
            }
            if (inheritedSolverBenchmark != null) {
                solverBenchmark.inherit(inheritedSolverBenchmark);
            }
            solverBenchmark.validate();
            solverBenchmark.resetSolverBenchmarkResultList();
        }
        int generatedNameIndex = 0;
        for (SolverBenchmark solverBenchmark : noNameBenchmarkSet) {
            String generatedName = "Config_" + generatedNameIndex;
            while (nameSet.contains(generatedName)) {
                generatedNameIndex++;
                generatedName = "Config_" + generatedNameIndex;
            }
            solverBenchmark.setName(generatedName);
            generatedNameIndex++;
        }
        initBenchmarkDirectoryAndSubdirs();
        if (solverBenchmarkComparator == null) {
            solverBenchmarkComparator = new TotalScoreSolverBenchmarkComparator();
        }
//        resetSolverBenchmarkSuiteResultList();
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
        if (solvedSolutionFilesDirectory == null) {
            solvedSolutionFilesDirectory = new File(benchmarkInstanceDirectory, "solved");
        }
        solvedSolutionFilesDirectory.mkdirs();
        if (solverStatisticFilesDirectory == null) {
            solverStatisticFilesDirectory = new File(benchmarkInstanceDirectory, "statistic");
        }
        solverStatisticFilesDirectory.mkdirs();
    }

//    private void resetSolverBenchmarkSuiteResultList() {
//        solverBenchmarkSuiteResultList = new ArrayList<SolverBenchmarkSuiteResult>();
//        Map<File, SolverBenchmarkSuiteResult> unsolvedSolutionFileToSuiteResultMap
//                = new LinkedHashMap<File, SolverBenchmarkSuiteResult>();
//        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
//            for (File unsolvedSolutionFile : solverBenchmark.getUnsolvedSolutionFileList()) {
//                if (!unsolvedSolutionFileToSuiteResultMap.containsKey(unsolvedSolutionFile)) {
//                    SolverBenchmarkSuiteResult suiteResult = new SolverBenchmarkSuiteResult();
//                    suiteResult.setUnsolvedSolutionFile(unsolvedSolutionFile);
//                    suiteResult.setSolverBenchmarkResultList(new ArrayList<SolverBenchmarkResult>(
//                            solverBenchmarkList.size()));
//                    solverBenchmarkSuiteResultList.add(suiteResult);
//                    unsolvedSolutionFileToSuiteResultMap.put(unsolvedSolutionFile, suiteResult);
//                }
//            }
//        }
//    }

    public void benchmark(XStream xStream) { // TODO refactor out xstream
        benchmarkingStarted();
        warmUp(xStream);
        // LinkedHashMap because order of unsolvedSolutionFile should be respected in output
        Map<File, List<SolverStatistic>> unsolvedSolutionFileToStatisticMap = new LinkedHashMap<File, List<SolverStatistic>>();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            for (SolverBenchmarkResult result : solverBenchmark.getSolverBenchmarkResultList()) {
                // Intentionally create a fresh solver for every result to reset Random, tabu lists, ...
                Solver solver = solverBenchmark.getSolverConfig().buildSolver();
                
                File unsolvedSolutionFile = result.getUnsolvedSolutionFile();
                Solution unsolvedSolution = readUnsolvedSolution(xStream, unsolvedSolutionFile);
                solver.setPlanningProblem(unsolvedSolution);
                List<SolverStatistic> statisticList = getOrCreateStatisticList(unsolvedSolutionFileToStatisticMap, unsolvedSolutionFile);
                for (SolverStatistic statistic : statisticList) {
                    statistic.addListener(solver, solverBenchmark.getName());
                }
                solver.solve();
                Solution solvedSolution = solver.getBestSolution();
                result.setTimeMillisSpend(solver.getTimeMillisSpend());
                DefaultSolverScope solverScope = ((DefaultSolver) solver).getSolverScope();
                result.setCalculateCount(solverScope.getCalculateCount());
                result.setScore(solvedSolution.getScore());
                SolutionDescriptor solutionDescriptor = ((DefaultSolver) solver).getSolutionDescriptor();
                result.setPlanningEntityCount(solutionDescriptor.getPlanningEntityCount(solvedSolution));
                result.setProblemScale(solutionDescriptor.getProblemScale(solvedSolution));
                for (SolverStatistic statistic : statisticList) {
                    statistic.removeListener(solver, solverBenchmark.getName());
                }
                writeSolvedSolution(xStream, solverBenchmark, result, solvedSolution);
            }
        }
        benchmarkingEnded(xStream, unsolvedSolutionFileToStatisticMap);
    }

    private void warmUp(XStream xStream) {
        if (warmUpTimeMillisSpend != null || warmUpSecondsSpend != null || warmUpMinutesSpend != null
                || warmUpHoursSpend != null) {
            logger.info("================================================================================");
            logger.info("Warming up");
            logger.info("================================================================================");
            long warmUpTimeMillisSpendTotal = 0L;
            if (warmUpTimeMillisSpend != null) {
                warmUpTimeMillisSpendTotal += warmUpTimeMillisSpend;
            }
            if (warmUpSecondsSpend != null) {
                warmUpTimeMillisSpendTotal += warmUpSecondsSpend * 1000L;
            }
            if (warmUpMinutesSpend != null) {
                warmUpTimeMillisSpendTotal += warmUpMinutesSpend * 60000L;
            }
            if (warmUpHoursSpend != null) {
                warmUpTimeMillisSpendTotal += warmUpHoursSpend * 3600000L;
            }
            long startingTimeMillis = System.currentTimeMillis();
            long timeLeft = warmUpTimeMillisSpendTotal;
            Iterator<SolverBenchmark> solverBenchmarkIt = solverBenchmarkList.iterator();
            int overallResultIndex = 0;
            while (timeLeft > 0L) {
                if (!solverBenchmarkIt.hasNext()) {
                    solverBenchmarkIt = solverBenchmarkList.iterator();
                    overallResultIndex++;
                }
                SolverBenchmark solverBenchmark = solverBenchmarkIt.next();
                List<SolverBenchmarkResult> solverBenchmarkResultList = solverBenchmark.getSolverBenchmarkResultList();
                int resultIndex = overallResultIndex % solverBenchmarkResultList.size();
                SolverBenchmarkResult result = solverBenchmarkResultList.get(resultIndex);
                TerminationConfig originalTerminationConfig = solverBenchmark.getSolverConfig().getTerminationConfig();
                TerminationConfig tmpTerminationConfig = originalTerminationConfig.clone();
                tmpTerminationConfig.shortenMaximumTimeMillisSpendTotal(timeLeft);
                solverBenchmark.getSolverConfig().setTerminationConfig(tmpTerminationConfig);
                Solver solver = solverBenchmark.getSolverConfig().buildSolver();
                File unsolvedSolutionFile = result.getUnsolvedSolutionFile();
                Solution unsolvedSolution = readUnsolvedSolution(xStream, unsolvedSolutionFile);
                solver.setPlanningProblem(unsolvedSolution);
                solver.solve();
                solverBenchmark.getSolverConfig().setTerminationConfig(originalTerminationConfig);
                long timeSpend = System.currentTimeMillis() - startingTimeMillis;
                timeLeft = warmUpTimeMillisSpendTotal - timeSpend;
            }
            logger.info("================================================================================");
            logger.info("Finished warmUp");
            logger.info("================================================================================");
        }
    }

    private List<SolverStatistic> getOrCreateStatisticList(
            Map<File, List<SolverStatistic>> unsolvedSolutionFileToStatisticMap, File unsolvedSolutionFile) {
        if (solverStatisticTypeList == null) {
            return Collections.emptyList();
        }
        List<SolverStatistic> statisticList = unsolvedSolutionFileToStatisticMap.get(unsolvedSolutionFile);
        if (statisticList == null) {
            statisticList = new ArrayList<SolverStatistic>(solverStatisticTypeList.size());
            for (SolverStatisticType solverStatisticType : solverStatisticTypeList) {
                statisticList.add(solverStatisticType.create());
            }
            unsolvedSolutionFileToStatisticMap.put(unsolvedSolutionFile, statisticList);
        }
        return statisticList;
    }

    private Solution readUnsolvedSolution(XStream xStream, File unsolvedSolutionFile) {
        Solution unsolvedSolution;
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(unsolvedSolutionFile), "utf-8");
            unsolvedSolution = (Solution) xStream.fromXML(reader);
        } catch (XStreamException e) {
            throw new IllegalArgumentException("Problem reading unsolvedSolutionFile: " + unsolvedSolutionFile, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem reading unsolvedSolutionFile: " + unsolvedSolutionFile, e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return unsolvedSolution;
    }

    private void writeSolvedSolution(XStream xStream, SolverBenchmark solverBenchmark, SolverBenchmarkResult result,
            Solution solvedSolution) {
        File solvedSolutionFile = null;
        String baseName = FilenameUtils.getBaseName(result.getUnsolvedSolutionFile().getName());
        String solverBenchmarkName = solverBenchmark.getName().replaceAll(" ", "_").replaceAll("[^\\w\\d_\\-]", "");
        String scoreString = result.getScore().toString().replaceAll("[\\/ ]", "_");
        String timeString = TIME_FORMAT.format(result.getTimeMillisSpend()) + "ms";
        solvedSolutionFile = new File(solvedSolutionFilesDirectory, baseName + "_" + solverBenchmarkName
                + "_score" + scoreString + "_time" + timeString + ".xml");
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(solvedSolutionFile), "utf-8");
            xStream.toXML(solvedSolution, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing solvedSolutionFile: " + solvedSolutionFile, e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        result.setSolvedSolutionFile(solvedSolutionFile);
    }

    public void benchmarkingEnded(XStream xStream, Map<File, List<SolverStatistic>> unsolvedSolutionFileToStatisticMap) {
        determineWinningResultScoreDelta();
        determineRanking();
        StatisticManager statisticManager = new StatisticManager(benchmarkInstanceDirectory.getName(),
                solverStatisticFilesDirectory, unsolvedSolutionFileToStatisticMap);
        statisticManager.writeStatistics(solverBenchmarkList);
        // TODO Temporarily disabled because it crashes because of http://jira.codehaus.org/browse/XSTR-666
        // writeBenchmarkResult(xStream);
    }

    private void determineWinningResultScoreDelta() {
        Map<File, SolverBenchmarkResult> winningResultMap = new LinkedHashMap<File, SolverBenchmarkResult>();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            for (SolverBenchmarkResult result : solverBenchmark.getSolverBenchmarkResultList()) {
                SolverBenchmarkResult winningResult = winningResultMap.get(result.getUnsolvedSolutionFile());
                if (winningResult == null || result.getScore().compareTo(winningResult.getScore()) > 0) {
                    winningResultMap.put(result.getUnsolvedSolutionFile(), result);
                }
            }
        }
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            for (SolverBenchmarkResult result : solverBenchmark.getSolverBenchmarkResultList()) {
                SolverBenchmarkResult winningResult = winningResultMap.get(result.getUnsolvedSolutionFile());
                result.setWinningScoreDifference(result.getScore().subtract(winningResult.getScore()));
            }
        }
    }

    private void determineRanking() {
        List<SolverBenchmark> sortedSolverBenchmarkList = new ArrayList<SolverBenchmark>(solverBenchmarkList);
        Collections.sort(sortedSolverBenchmarkList, solverBenchmarkComparator);
        Collections.reverse(sortedSolverBenchmarkList); // Best results first, worst results last
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            solverBenchmark.setRanking(sortedSolverBenchmarkList.indexOf(solverBenchmark));
        }
    }

    public void writeBenchmarkResult(XStream xStream) {
        File benchmarkResultFile = new File(benchmarkInstanceDirectory, "benchmarkResult.xml");
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(benchmarkResultFile), "utf-8");
            xStream.toXML(this, writer);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This JVM does not support utf-8 encoding.", e);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(
                    "Could not create benchmarkResultFile (" + benchmarkResultFile + ").", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}
