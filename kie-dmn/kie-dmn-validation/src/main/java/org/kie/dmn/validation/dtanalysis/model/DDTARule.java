/**
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
package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DDTARule {

    private List<DDTAInputEntry> inputEntry = new ArrayList<>();
    private List<Comparable<?>> outputEntry = new ArrayList<>();

    public List<DDTAInputEntry> getInputEntry() {
        return inputEntry;
    }

    public List<Comparable<?>> getOutputEntry() {
        return outputEntry;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DDTARule inputEntries > ");
        builder.append(inputEntry.stream().map(DDTAInputEntry::toString).collect(Collectors.joining(" | ")));
        builder.append(" outputEntries > ");
        builder.append(outputEntry.stream().map(Objects::toString).collect(Collectors.joining(" | ")));
        return builder.toString();
    }

    static boolean inputEntriesIncludeAll(List<DDTAInputEntry> curInputEntries, List<DDTAInputEntry> otherInputEntries) {
        boolean includeAll = true;
        for (int i = 0; i < curInputEntries.size(); i++) {
            DDTAInputEntry curIE = curInputEntries.get(i);
            DDTAInputEntry otherIE = otherInputEntries.get(i);
            includeAll &= curIE.includes(otherIE);
        }
        return includeAll;
    }

}
