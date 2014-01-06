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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;

public abstract class AbstractSingleStatistic implements SingleStatistic {

    protected AbstractSingleStatistic() {
    }

    public class SingleStatisticCsv {

        private Map<Long, String> singleStatisticLines = new TreeMap<Long, String>();

        public void addPoint(long timeMillisSpend, long value) {
            addRawPoint(timeMillisSpend, Long.toString(value));
        }

        public void addPoint(long timeMillisSpend, double value) {
            addRawPoint(timeMillisSpend, Double.toString(value));
        }

        public void addPoint(long timeMillisSpend, String value) {
            addRawPoint(timeMillisSpend, "\"" + value.replaceAll("\"", "\"\"") + "\"");
        }

        private void addRawPoint(long timeMillisSpend, String value) {
            singleStatisticLines.put(timeMillisSpend, value);
        }

        public void writeCsvSingleStatisticFile(File outputFile) {
            Writer writer = null;
            try {
                writer = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8");
                for (Long timeSpent : singleStatisticLines.keySet()) {
                    writer.write(timeSpent.toString());
                    writer.append(",");
                    String value = singleStatisticLines.get(timeSpent);
                    if (value != null) {
                        writer.append(value);
                    }
                    writer.append("\n");
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Problem writing csvStatisticFile: " + outputFile, e);
            } finally {
                IOUtils.closeQuietly(writer);
            }
        }
    }

}
