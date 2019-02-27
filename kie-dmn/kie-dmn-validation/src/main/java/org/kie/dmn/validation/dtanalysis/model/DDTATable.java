package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.List;

public class DDTATable {

    private List<DDTAInputClause> inputs = new ArrayList<>();
    private List<DDTARule> rules = new ArrayList<>();

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

}
