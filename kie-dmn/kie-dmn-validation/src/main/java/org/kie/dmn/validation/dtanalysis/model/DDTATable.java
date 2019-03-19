/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.List;

public class DDTATable {

    private List<DDTAInputClause> inputs = new ArrayList<>();
    private List<DDTARule> rules = new ArrayList<>();
    private List<DDTAOutputClause> outputs = new ArrayList<>();

    public List<DDTAInputClause> getInputs() {
        return inputs;
    }

    public List<DDTARule> getRule() {
        return rules;
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

}
