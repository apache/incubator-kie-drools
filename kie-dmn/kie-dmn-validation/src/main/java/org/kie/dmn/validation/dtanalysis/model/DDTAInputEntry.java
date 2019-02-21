package org.kie.dmn.validation.dtanalysis.model;

import java.util.List;

import org.kie.dmn.feel.lang.ast.BaseNode;

public class DDTAInputEntry {

    private final List<BaseNode> uts;
    private final List<Interval> intervals;

    public DDTAInputEntry(List<BaseNode> uts, List<Interval> intervals) {
        this.uts = uts;
        this.intervals = intervals;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(intervals);
        return builder.toString();
    }

    public List<BaseNode> getUts() {
        return uts;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

}
