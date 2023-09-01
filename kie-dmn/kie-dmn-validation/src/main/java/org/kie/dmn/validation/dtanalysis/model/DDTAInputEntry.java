package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.feel.lang.ast.BaseNode;

public class DDTAInputEntry {

    private final List<BaseNode> uts;
    private final List<Interval> intervals;
    private final boolean allSingularities;

    public DDTAInputEntry(List<BaseNode> uts, List<Interval> intervals, boolean allSingularities) {
        this.uts = uts;
        this.intervals = intervals;
        this.allSingularities = allSingularities;
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
    
    /**
     * True when UnaryTests represent singularity/singularities, regardless of negation.
     * Eg:
     * "a"
     * "a", "b"
     * not("a")
     * not("a", "b")
     */
    public boolean isAllSingularities() {
        return allSingularities;
    }

    public boolean includes(DDTAInputEntry other) {
        List<Interval> otherIntervals = new ArrayList<>(other.intervals);
        for (Interval i : intervals) {
            List<Interval> includedInI = new ArrayList<>();
            for (Interval o : otherIntervals) {
                if (i.includes(o)) {
                    includedInI.add(o);
                }
            }
            otherIntervals.removeAll(includedInI);
        }
        return otherIntervals.isEmpty();
    }

    public boolean adjOrOverlap(DDTAInputEntry other) {
        return Interval.adjOrOverlap(this.intervals, other.intervals);
    }

}
