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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DDTATable {

    private List<DDTAInputClause> inputs = new ArrayList<>();
    private List<DDTARule> rules = new ArrayList<>();
    private List<DDTAOutputClause> outputs = new ArrayList<>();
    private Map<List<List<Interval>>, List<Integer>> cacheByInputEntry = new HashMap<>();
    private Map<List<Comparable<?>>, List<Integer>> cacheByOutputEntry = new HashMap<>();
    private List<Integer> colIDsStringWithoutEnum = new ArrayList<>();

    public List<DDTAInputClause> getInputs() {
        return inputs;
    }

    public List<DDTARule> getRule() {
        return Collections.unmodifiableList(rules);
    }

    public int inputCols() {
        return inputs.size();
    }

    public int inputRules() {
        return rules.size();
    }

    public List<Interval> projectOnColumnIdx(int jColIdx) {
        List<Interval> results = new ArrayList<>();
        for (DDTARule r : rules) {
            DDTAInputEntry ieX = r.getInputEntry().get(jColIdx);
            results.addAll(ieX.getIntervals());
        }
        return results;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DDTATable [rules=");
        rules.forEach(r -> builder.append("\n" + r));
        builder.append("\n]");
        return builder.toString();
    }

    public List<DDTAOutputClause> getOutputs() {
        return outputs;
    }

    public int outputCols() {
        return outputs.size();
    }

    public void addRule(DDTARule ddtaRule) {
        rules.add(ddtaRule);
        int ruleID = rules.size();
        List<List<Interval>> ieIntervals = ddtaRule.getInputEntry().stream().map(DDTAInputEntry::getIntervals).collect(Collectors.toList());
        cacheByInputEntry.computeIfAbsent(ieIntervals, x -> new ArrayList<>()).add(ruleID);
        cacheByOutputEntry.computeIfAbsent(ddtaRule.getOutputEntry(), x -> new ArrayList<>()).add(ruleID);
    }

    public Set<List<Comparable<?>>> outputEntries() {
        return Collections.unmodifiableSet(cacheByOutputEntry.keySet());
    }

    public List<Integer> ruleIDsByOutputEntry(List<Comparable<?>> oe) {
        return Collections.unmodifiableList(cacheByOutputEntry.getOrDefault(oe, Collections.emptyList()));
    }

    public Map<List<List<Interval>>, List<Integer>> getCacheByInputEntry() {
        return Collections.unmodifiableMap(cacheByInputEntry);
    }

    public void addColIdStringWithoutEnum(int colID) {
        this.colIDsStringWithoutEnum.add(colID);
    }

    public List<Integer> getColIDsStringWithoutEnum() {
        return Collections.unmodifiableList(colIDsStringWithoutEnum);
    }
}
