/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.report;

import org.apache.commons.lang3.StringUtils;

public class ReportHelper {

    /**
     * Escape illegal HTML element id characters, such as a dot.
     * <p>
     * This escape function guarantees that 2 distinct strings will result into 2 distinct escape strings
     * (presuming that both have been escaped by this method).
     *
     * @param rawHtmlId never null
     * @return never null
     */
    public static String escapeHtmlId(String rawHtmlId) {
        // Uses unicode numbers to escape, see http://unicode-table.com
        // Uses '-' as the escape character
        return rawHtmlId
                .replaceAll(" ", "-0020")
                .replaceAll("!", "-0021")
                .replaceAll("#", "-0023")
                .replaceAll("\\$", "-0024")
                .replaceAll(",", "-002C")
                .replaceAll("-", "-002D")
                .replaceAll("\\.", "-002E")
                .replaceAll("\\(", "-0028")
                .replaceAll("\\)", "-0029")
                .replaceAll(":", "-003A")
                .replaceAll(";", "-003B")
                .replaceAll("\\?", "-003F");
    }

    public static String capitalize(String s) {
        return StringUtils.capitalize(s);
    }

}
