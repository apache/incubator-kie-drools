package org.optaplanner.benchmark.impl.result;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.PureSubSingleStatistic;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.io.OptaPlannerXmlSerializationException;
import org.optaplanner.core.impl.io.jaxb.ElementNamespaceOverride;
import org.optaplanner.core.impl.io.jaxb.GenericJaxbIO;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkResultIO {
    // BenchmarkResult contains <solverConfig/> element instead of the default SolverConfig.XML_ELEMENT_NAME.
    private static final String SOLVER_CONFIG_XML_ELEMENT_NAME = "solverConfig";
    private static final String PLANNER_BENCHMARK_RESULT_FILENAME = "plannerBenchmarkResult.xml";

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkResultIO.class);

    private final GenericJaxbIO<PlannerBenchmarkResult> genericJaxbIO = new GenericJaxbIO<>(PlannerBenchmarkResult.class);

    public void writePlannerBenchmarkResult(File benchmarkReportDirectory,
            PlannerBenchmarkResult plannerBenchmarkResult) {
        File plannerBenchmarkResultFile = new File(benchmarkReportDirectory, PLANNER_BENCHMARK_RESULT_FILENAME);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(plannerBenchmarkResultFile), StandardCharsets.UTF_8)) {
            write(plannerBenchmarkResult, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed writing plannerBenchmarkResultFile (" + plannerBenchmarkResultFile + ").", e);
        }
    }

    public List<PlannerBenchmarkResult> readPlannerBenchmarkResultList(File benchmarkDirectory) {
        if (!benchmarkDirectory.exists() || !benchmarkDirectory.isDirectory()) {
            throw new IllegalArgumentException("The benchmarkDirectory (" + benchmarkDirectory
                    + ") does not exist or is not a directory.");
        }
        File[] benchmarkReportDirectories = benchmarkDirectory.listFiles(File::isDirectory);
        if (benchmarkReportDirectories == null) {
            throw new IllegalStateException("Unable to list the subdirectories in the benchmarkDirectory ("
                    + benchmarkDirectory.getAbsolutePath() + ").");
        }
        Arrays.sort(benchmarkReportDirectories);
        List<PlannerBenchmarkResult> plannerBenchmarkResultList = new ArrayList<>(benchmarkReportDirectories.length);
        for (File benchmarkReportDirectory : benchmarkReportDirectories) {
            File plannerBenchmarkResultFile = new File(benchmarkReportDirectory, PLANNER_BENCHMARK_RESULT_FILENAME);
            if (plannerBenchmarkResultFile.exists()) {
                PlannerBenchmarkResult plannerBenchmarkResult = readPlannerBenchmarkResult(plannerBenchmarkResultFile);
                plannerBenchmarkResultList.add(plannerBenchmarkResult);
            }
        }
        return plannerBenchmarkResultList;
    }

    protected PlannerBenchmarkResult readPlannerBenchmarkResult(File plannerBenchmarkResultFile) {
        if (!plannerBenchmarkResultFile.exists()) {
            throw new IllegalArgumentException("The plannerBenchmarkResultFile (" + plannerBenchmarkResultFile
                    + ") does not exist.");
        }
        PlannerBenchmarkResult plannerBenchmarkResult;
        try (Reader reader = new InputStreamReader(new FileInputStream(plannerBenchmarkResultFile), StandardCharsets.UTF_8)) {
            plannerBenchmarkResult = read(reader);
        } catch (OptaPlannerXmlSerializationException e) {
            LOGGER.warn("Failed reading plannerBenchmarkResultFile ({}).", plannerBenchmarkResultFile, e);
            // If the plannerBenchmarkResultFile's format has changed, the app should not crash entirely
            String benchmarkReportDirectoryName = plannerBenchmarkResultFile.getParentFile().getName();
            plannerBenchmarkResult = PlannerBenchmarkResult.createUnmarshallingFailedResult(
                    benchmarkReportDirectoryName);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed reading plannerBenchmarkResultFile (" + plannerBenchmarkResultFile + ").", e);
        }
        plannerBenchmarkResult.setBenchmarkReportDirectory(plannerBenchmarkResultFile.getParentFile());
        restoreOmittedBidirectionalFields(plannerBenchmarkResult);
        restoreOtherOmittedFields(plannerBenchmarkResult);
        return plannerBenchmarkResult;
    }

    protected PlannerBenchmarkResult read(Reader reader) {
        return genericJaxbIO.readOverridingNamespace(reader,
                ElementNamespaceOverride.of(SOLVER_CONFIG_XML_ELEMENT_NAME, SolverConfig.XML_NAMESPACE));
    }

    protected void write(PlannerBenchmarkResult plannerBenchmarkResult, Writer writer) {
        genericJaxbIO.writeWithoutNamespaces(plannerBenchmarkResult, writer);
    }

    private void restoreOmittedBidirectionalFields(PlannerBenchmarkResult plannerBenchmarkResult) {
        for (ProblemBenchmarkResult<Object> problemBenchmarkResult : plannerBenchmarkResult
                .getUnifiedProblemBenchmarkResultList()) {
            problemBenchmarkResult.setPlannerBenchmarkResult(plannerBenchmarkResult);
            if (problemBenchmarkResult.getProblemStatisticList() == null) {
                problemBenchmarkResult.setProblemStatisticList(new ArrayList<>(0));
            }
            for (ProblemStatistic problemStatistic : problemBenchmarkResult.getProblemStatisticList()) {
                problemStatistic.setProblemBenchmarkResult(problemBenchmarkResult);
            }
            for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
                singleBenchmarkResult.setProblemBenchmarkResult(problemBenchmarkResult);
            }
        }
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            solverBenchmarkResult.setPlannerBenchmarkResult(plannerBenchmarkResult);
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                singleBenchmarkResult.setSolverBenchmarkResult(solverBenchmarkResult);
                for (SubSingleBenchmarkResult subSingleBenchmarkResult : singleBenchmarkResult
                        .getSubSingleBenchmarkResultList()) {
                    if (subSingleBenchmarkResult.getPureSubSingleStatisticList() == null) {
                        subSingleBenchmarkResult.setPureSubSingleStatisticList(new ArrayList<>(0));
                    }
                }
                for (SubSingleBenchmarkResult subSingleBenchmarkResult : singleBenchmarkResult
                        .getSubSingleBenchmarkResultList()) {
                    for (PureSubSingleStatistic pureSubSingleStatistic : subSingleBenchmarkResult
                            .getPureSubSingleStatisticList()) {
                        pureSubSingleStatistic.setSubSingleBenchmarkResult(subSingleBenchmarkResult);
                    }
                }
            }
        }
    }

    private void restoreOtherOmittedFields(PlannerBenchmarkResult plannerBenchmarkResult) {
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            SolverConfig solverConfig = solverBenchmarkResult.getSolverConfig();
            DefaultSolverFactory<?> defaultSolverFactory = new DefaultSolverFactory<>(solverConfig);
            solverBenchmarkResult.setScoreDefinition(defaultSolverFactory.getSolutionDescriptor().getScoreDefinition());
        }
    }

}
