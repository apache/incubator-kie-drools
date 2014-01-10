/*
 * Copyright 2012 JBoss Inc
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
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.optaplanner.benchmark.impl.SingleBenchmarkResult;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public abstract class AbstractSingleStatistic<P extends AbstractSingleStatisticPoint> implements SingleStatistic {

    protected final SingleBenchmarkResult singleBenchmarkResult;
    protected final StatisticType statisticType;

    protected File csvFile = null;

    protected AbstractSingleStatistic(SingleBenchmarkResult singleBenchmarkResult, StatisticType statisticType) {
        this.singleBenchmarkResult = singleBenchmarkResult;
        this.statisticType = statisticType;
    }

    public abstract List<P> getPointList();

    public File getCsvFile() {
        return csvFile;
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    protected abstract List<String> getCsvHeader();

    public void writeCsvStatisticFile() {
        csvFile = new File(singleBenchmarkResult.getReportDirectory(), statisticType.name() + ".csv");
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8");
            writeCsvLine(writer, getCsvHeader());
            writer.append("\n");
            for (AbstractSingleStatisticPoint point : getPointList()) {
                writeCsvLine(writer, point.toCsvLine());
            }
            if (singleBenchmarkResult.isFailure()) {
                writer.append("Failed\n");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing csvFile: " + csvFile, e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private void writeCsvLine(Writer writer, List<String> line) throws IOException {
        boolean firstToken = true;
        for (String token : line) {
            if (firstToken) {
                firstToken = false;
            } else {
                writer.append(",");
            }
            writer.append(token);
        }
        writer.append("\n");
    }

    public void readCsvStatisticFile() {
        ScoreDefinition scoreDefinition = singleBenchmarkResult.getSolverBenchmarkResult().getSolverConfig()
                .getScoreDirectorFactoryConfig().buildScoreDefinition();
        List<P> pointList = getPointList();
        if (!pointList.isEmpty()) {
            throw new IllegalStateException("The pointList with size (" + pointList.size() + ") should be empty.");
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"));
            for (String line = reader.readLine(); line != null && !line.isEmpty(); line = reader.readLine()) {
                String[] tokens = line.split(",");
                List<String> csvLine = new ArrayList<String>(tokens.length);
                for (int i = 0; i < tokens.length; i++) {
                    String token = tokens[i];
                    while (token.trim().startsWith("\"") && !token.trim().endsWith("\"")) {
                        i++;
                        if (i >= tokens.length) {
                            throw new IllegalArgumentException("The CSV line (" + line
                                    + ") is not valid in csvFile (" + csvFile + ").");
                        }
                        token += tokens[i];
                    }
                    token = token.trim();
                    if (token.startsWith("\"") && token.endsWith("\"")) {
                        token = token.substring(1, token.length() - 1);
                        token = token.replaceAll("\"\"", "\"");
                    }
                    csvLine.add(token);
                }
                pointList.add(createPointFromCsvLine(scoreDefinition, csvLine));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem reading csvFile: " + csvFile, e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    protected abstract P createPointFromCsvLine(ScoreDefinition scoreDefinition, List<String> csvLine);

}
