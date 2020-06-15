/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.statistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.optaplanner.benchmark.impl.report.ReportHelper;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamInclude;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * 1 statistic of {@link SubSingleBenchmarkResult}.
 */
@XStreamInclude({
        PureSubSingleStatistic.class
})
public abstract class SubSingleStatistic<Solution_, StatisticPoint_ extends StatisticPoint> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @XStreamOmitField // Bi-directional relationship restored through subSingleBenchmarkResultIO
    protected SubSingleBenchmarkResult subSingleBenchmarkResult;

    @XStreamOmitField
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

    public abstract void open(Solver<Solution_> solver);

    public abstract void close(Solver<Solution_> solver);

    // ************************************************************************
    // Write methods
    // ************************************************************************

    public void initPointList() {
        pointList = new ArrayList<>();
    }

    protected abstract String getCsvHeader();

    private void writeCsvStatisticFile() {
        File csvFile = getCsvFile();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8")) {
            writer.append(getCsvHeader()).append("\n");
            for (StatisticPoint point : getPointList()) {
                writer.append(point.toCsvLine()).append("\n");
            }
            if (subSingleBenchmarkResult.hasAnyFailure()) {
                writer.append("Failed\n");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed writing csvFile (" + csvFile + ").", e);
        }
    }

    private void readCsvStatisticFile() {
        File csvFile = getCsvFile();
        ScoreDefinition scoreDefinition = subSingleBenchmarkResult.getSingleBenchmarkResult().getSolverBenchmarkResult()
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {
            String line = reader.readLine();
            if (!getCsvHeader().equals(line)) {
                throw new IllegalStateException("The read line (" + line
                        + ") is expected to be the header line (" + getCsvHeader()
                        + ") for statisticType (" + getStatisticType() + ").");
            }
            Map<String, String> stringDuplicationRemovalMap = new HashMap<>(1024);
            for (line = reader.readLine(); line != null && !line.isEmpty(); line = reader.readLine()) {
                if (line.equals("Failed")) {
                    if (subSingleBenchmarkResult.hasAnyFailure()) {
                        continue;
                    }
                    throw new IllegalStateException("SubSingleStatistic (" + this + ") failed even though the "
                            + "corresponding subSingleBenchmarkResult (" + subSingleBenchmarkResult + ") is a success.");
                }
                List<String> csvLine = StatisticPoint.parseCsvLine(line);
                // HACK
                // Some statistics (such as CONSTRAINT_MATCH_TOTAL_STEP_SCORE) contain the same String many times
                // During generation those are all the same instance to save memory.
                // During aggregation this code assures they are the same instance too
                for (ListIterator<String> it = csvLine.listIterator(); it.hasNext();) {
                    String token = it.next();
                    if (token == null) {
                        continue;
                    }
                    String originalToken = stringDuplicationRemovalMap.get(token);
                    if (originalToken == null) {
                        stringDuplicationRemovalMap.put(token, token);
                    } else {
                        it.set(originalToken);
                    }
                }
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

    protected abstract StatisticPoint_ createPointFromCsvLine(ScoreDefinition scoreDefinition, List<String> csvLine);

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    public String getAnchorId() {
        return ReportHelper.escapeHtmlId(subSingleBenchmarkResult.getName() + "_" + getStatisticType().name());
    }

}
