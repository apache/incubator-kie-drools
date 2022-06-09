package org.optaplanner.benchmark.impl.statistic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.optaplanner.benchmark.impl.report.ReportHelper;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Tags;

/**
 * 1 statistic of {@link SubSingleBenchmarkResult}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class SubSingleStatistic<Solution_, StatisticPoint_ extends StatisticPoint> {

    private static final String FAILED = "Failed";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @XmlTransient // Bi-directional relationship restored through BenchmarkResultIO
    protected SubSingleBenchmarkResult subSingleBenchmarkResult;

    @XmlTransient
    protected List<StatisticPoint_> pointList;

    protected SubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        this.subSingleBenchmarkResult = subSingleBenchmarkResult;
    }

    public SubSingleBenchmarkResult getSubSingleBenchmarkResult() {
        return subSingleBenchmarkResult;
    }

    public void setSubSingleBenchmarkResult(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        this.subSingleBenchmarkResult = subSingleBenchmarkResult;
    }

    public abstract StatisticType getStatisticType();

    public List<StatisticPoint_> getPointList() {
        return pointList;
    }

    public void setPointList(List<StatisticPoint_> pointList) {
        this.pointList = pointList;
    }

    /**
     * @return never null, the relative path from {@link PlannerBenchmarkResult#getBenchmarkReportDirectory()}.
     */
    public String getRelativeCsvFilePath() {
        SingleBenchmarkResult singleBenchmarkResult = subSingleBenchmarkResult.getSingleBenchmarkResult();
        return singleBenchmarkResult.getProblemBenchmarkResult().getProblemReportDirectoryName() + "/"
                + singleBenchmarkResult.getResultDirectoryName() + "/"
                + subSingleBenchmarkResult.getResultDirectoryName() + "/"
                + getCsvFileName();
    }

    public String getCsvFileName() {
        return getStatisticType().name() + ".csv";
    }

    public File getCsvFile() {
        return new File(subSingleBenchmarkResult.getResultDirectory(), getCsvFileName());
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public abstract void open(StatisticRegistry<Solution_> registry, Tags runTag, Solver<Solution_> solver);

    public void close(StatisticRegistry<Solution_> registry, Tags runTag, Solver<Solution_> solver) {
        // Empty by default; SubSingleBenchmarkRunner unregisters the Registry (and thus the listeners)
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    public void initPointList() {
        pointList = new ArrayList<>();
    }

    protected abstract String getCsvHeader();

    private void writeCsvStatisticFile() {
        File csvFile = getCsvFile();
        try (BufferedWriter writer = Files.newBufferedWriter(csvFile.toPath(), StandardCharsets.UTF_8)) {
            writer.append(getCsvHeader());
            writer.newLine();
            for (StatisticPoint point : getPointList()) {
                writer.append(point.toCsvLine());
                writer.newLine();
            }
            if (subSingleBenchmarkResult.hasAnyFailure()) {
                writer.append(FAILED);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed writing csvFile (" + csvFile + ").", e);
        }
    }

    private void readCsvStatisticFile() {
        File csvFile = getCsvFile();
        ScoreDefinition<?> scoreDefinition = subSingleBenchmarkResult.getSingleBenchmarkResult().getSolverBenchmarkResult()
                .getScoreDefinition();
        if (!pointList.isEmpty()) {
            throw new IllegalStateException("The pointList with size (" + pointList.size() + ") should be empty.");
        }
        if (!csvFile.exists()) {
            if (subSingleBenchmarkResult.hasAnyFailure()) {
                pointList = Collections.emptyList();
                return;
            } else {
                throw new IllegalStateException("The csvFile (" + csvFile + ") does not exist.");
            }
        }

        try (BufferedReader reader = Files.newBufferedReader(csvFile.toPath(), StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            if (!getCsvHeader().equals(line)) {
                throw new IllegalStateException("The read line (" + line
                        + ") is expected to be the header line (" + getCsvHeader()
                        + ") for statisticType (" + getStatisticType() + ").");
            }
            for (line = reader.readLine(); line != null && !line.isEmpty(); line = reader.readLine()) {
                if (line.equals(FAILED)) {
                    if (subSingleBenchmarkResult.hasAnyFailure()) {
                        continue;
                    }
                    throw new IllegalStateException("SubSingleStatistic (" + this + ") failed even though the "
                            + "corresponding subSingleBenchmarkResult (" + subSingleBenchmarkResult + ") is a success.");
                }
                // HACK
                // Some statistics (such as CONSTRAINT_MATCH_TOTAL_STEP_SCORE) contain the same String many times
                // During generation those are all the same instance to save memory.
                // During aggregation this code assures they are the same instance too
                List<String> csvLine = StatisticPoint.parseCsvLine(line)
                        .stream()
                        .map(String::intern)
                        .collect(Collectors.toList());
                pointList.add(createPointFromCsvLine(scoreDefinition, csvLine));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed reading csvFile (" + csvFile + ").", e);
        }
    }

    public void unhibernatePointList() {
        if (!getCsvFile().exists()) {
            throw new IllegalStateException("The csvFile (" + getCsvFile() + ") of the statistic (" + getStatisticType()
                    + ") of the single benchmark (" + subSingleBenchmarkResult + ") doesn't exist.");
        } else if (pointList != null) {
            throw new IllegalStateException("The pointList (" + pointList + ") of the statistic (" + getStatisticType()
                    + ") of the single benchmark (" + subSingleBenchmarkResult + ") should be null when unhibernating.");
        }
        initPointList();
        readCsvStatisticFile();
    }

    public void hibernatePointList() {
        writeCsvStatisticFile();
        pointList = null;
    }

    protected abstract StatisticPoint_ createPointFromCsvLine(ScoreDefinition<?> scoreDefinition, List<String> csvLine);

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    public String getAnchorId() {
        return ReportHelper.escapeHtmlId(subSingleBenchmarkResult.getName() + "_" + getStatisticType().name());
    }

}
