/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.benchmark.impl.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Implementations must be immutable.
 */
public abstract class StatisticPoint {

    private static final Pattern DOUBLE_QUOTE = Pattern.compile("\"\"");
    private static final Pattern SINGLE_QUOTE = Pattern.compile("\"");

    public abstract String toCsvLine();

    public static String buildCsvLineWithLongs(long timeMillisSpent, long... values) {
        return LongStream.concat(LongStream.of(timeMillisSpent), Arrays.stream(values))
                .mapToObj(Long::toString)
                .collect(Collectors.joining(","));
    }

    public static String buildCsvLineWithDoubles(long timeMillisSpent, double... values) {
        return timeMillisSpent + "," + Arrays.stream(values)
                .mapToObj(Double::toString)
                .collect(Collectors.joining(","));
    }

    public static String buildCsvLineWithStrings(long timeMillisSpent, String... values) {
        return Stream.concat(Stream.of(timeMillisSpent), Arrays.stream(values))
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    public static String buildCsvLine(String... values) {
        return Arrays.stream(values)
                .map(s -> '"' + SINGLE_QUOTE.matcher(s).replaceAll("\"\"") + '"')
                .collect(Collectors.joining(","));
    }

    public static List<String> parseCsvLine(String line) {
        String[] tokens = line.split(",");
        List<String> csvLine = new ArrayList<>(tokens.length);
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            while (token.startsWith("\"") && !token.endsWith("\"")) {
                i++;
                if (i >= tokens.length) {
                    throw new IllegalArgumentException("The CSV line (" + line + ") is not a valid CSV line.");
                }
                token += "," + tokens[i].trim();
            }
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
                token = DOUBLE_QUOTE.matcher(token).replaceAll("\"");
            }
            csvLine.add(token);
        }
        return csvLine;
    }

}
