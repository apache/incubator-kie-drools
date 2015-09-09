/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.impl.result;

public class StatsUtil {

    // TODO Do the locale formatting in benchmarkReport.html.ftl - https://issues.jboss.org/browse/PLANNER-169
    public static String getStandardDeviationString(double[] standardDeviationDoubles) {
        if (standardDeviationDoubles == null) {
            return null;
        }
        StringBuilder standardDeviationString = new StringBuilder(standardDeviationDoubles.length * 9);
        boolean first = true;
        for (double standardDeviationDouble : standardDeviationDoubles) {
            if (first) {
                first = false;
            } else {
                standardDeviationString.append("/");
            }
            String abbreviated = Double.toString(standardDeviationDouble);
            // Abbreviate to 2 decimals
            // We don't use DecimalFormat to abbreviate because it's written locale insensitive (like java literals)
            int dotIndex = abbreviated.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex + 3 < abbreviated.length()) {
                abbreviated = abbreviated.substring(0, dotIndex + 3);
            }
            standardDeviationString.append(abbreviated);
        }
        return standardDeviationString.toString();
    }

}
