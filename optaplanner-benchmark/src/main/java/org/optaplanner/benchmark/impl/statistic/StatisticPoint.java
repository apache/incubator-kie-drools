/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

/**
 * Implementations must be immutable.
 */
public abstract class StatisticPoint {

    public abstract String toCsvLine();

    public static String buildCsvLineWithLongs(long timeMillisSpent, long... values) {
        StringBuilder line = new StringBuilder(values.length * 10);
        line.append(Long.toString(timeMillisSpent));
        for (long value : values) {
            line.append(",").append(Long.toString(value));
        }
        return line.toString();
    }

    public static String buildCsvLineWithDoubles(long timeMillisSpent, double... values) {
        StringBuilder line = new StringBuilder(values.length * 10);
        line.append(Long.toString(timeMillisSpent));
        for (double value : values) {
            line.append(",").append(Double.toString(value));
        }
        return line.toString();
    }

    public static String buildCsvLineWithStrings(long timeMillisSpent, String... values) {
        StringBuilder line = new StringBuilder(values.length * 10);
        line.append(Long.toString(timeMillisSpent));
        for (String value : values) {
            line.append(",").append("\"").append(value.replaceAll("\"", "\"\"")).append("\"");
        }
        return line.toString();
    }

    public static String buildCsvLine(String... values) {
        StringBuilder line = new StringBuilder(values.length * 10);
        for (String value : values) {
            line.append(",").append("\"").append(value.replaceAll("\"", "\"\"")).append("\"");
        }
        return line.substring(1).toString();
    }

    public static List<String> parseCsvLine(String line) {
        String[] tokens = line.split(",");
        List<String> csvLine = new ArrayList<>(tokens.length);
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            while (token.trim().startsWith("\"") && !token.trim().endsWith("\"")) {
                i++;
                if (i >= tokens.length) {
                    throw new IllegalArgumentException("The CSV line (" + line + ") is not a valid CSV line.");
                }
                token += "," + tokens[i];
            }
            token = token.trim();
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
                token = token.replaceAll("\"\"", "\"");
            }
            csvLine.add(token);
        }
        return csvLine;
    }

}
