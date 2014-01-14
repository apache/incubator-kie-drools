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

package org.optaplanner.benchmark.impl.statistic;

import java.util.ArrayList;
import java.util.List;

public abstract class StatisticPoint {

    public abstract List<String> toCsvLine();

    public static List<String> buildCsvLineWithLongs(long timeMillisSpend, long... values) {
        List<String> line = new ArrayList<String>(values.length + 1);
        line.add(Long.toString(timeMillisSpend));
        for (long value : values) {
            line.add(Long.toString(value));
        }
        return line;
    }

    public static List<String> buildCsvLineWithDoubles(long timeMillisSpend, double... values) {
        List<String> line = new ArrayList<String>(values.length + 1);
        line.add(Long.toString(timeMillisSpend));
        for (double value : values) {
            line.add(Double.toString(value));
        }
        return line;
    }

    public static List<String> buildCsvLineWithStrings(long timeMillisSpend, String... values) {
        List<String> line = new ArrayList<String>(values.length + 1);
        line.add(Long.toString(timeMillisSpend));
        for (String value : values) {
            line.add("\"" + value.replaceAll("\"", "\"\"") + "\"");
        }
        return line;
    }

    public static List<String> buildCsvLine(String... values) {
        List<String> line = new ArrayList<String>(values.length);
        for (String value : values) {
            line.add("\"" + value.replaceAll("\"", "\"\"") + "\"");
        }
        return line;
    }

}
