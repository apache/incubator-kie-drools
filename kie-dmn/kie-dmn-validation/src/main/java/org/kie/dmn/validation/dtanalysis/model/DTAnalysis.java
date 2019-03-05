package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.DecisionTable;

public class DTAnalysis {

    private final List<Hyperrectangle> gaps = new ArrayList<>();
    private final List<Overlap> overlaps = new ArrayList<>();
    private final DecisionTable sourceDT;

    public DTAnalysis(DecisionTable sourceDT) {
        this.sourceDT = sourceDT;
    }

    public Collection<Hyperrectangle> getGaps() {
        return Collections.unmodifiableList(gaps);
    }

    public void addGap(Hyperrectangle gap) {
        this.gaps.add(gap);
    }

    public DMNModelInstrumentedBase getSource() {
        return sourceDT;
    }

    public List<Overlap> getOverlaps() {
        return Collections.unmodifiableList(overlaps);
    }

    public void addOverlap(Overlap overlap) {
        this.overlaps.add(overlap);
    }

    public void normalize() {
        int prevSize = this.overlaps.size();
        internal_normalize();
        int curSize = this.overlaps.size();
        if (curSize != prevSize) {
            normalize();
        }
    }

    private void internal_normalize() {
        List<Overlap> newOverlaps = new ArrayList<>();
        List<Overlap> overlapsProcessing = new ArrayList<>();
        overlapsProcessing.addAll(overlaps);
        while (!overlapsProcessing.isEmpty()) {
            Overlap curOverlap = overlapsProcessing.remove(0);
            for (Overlap otherOverlap : overlapsProcessing) {
                int x = curOverlap.contigousOnDimension(otherOverlap);
                if (x > 0) {
                    Overlap mergedOverlap = Overlap.newByMergeOnDimension(curOverlap, otherOverlap, x);
                    curOverlap = null;
                    overlapsProcessing.remove(otherOverlap);
                    overlapsProcessing.add(0, mergedOverlap);
                }
            }
            if (curOverlap != null) {
                newOverlaps.add(curOverlap);
            }
        }
        this.overlaps.clear();
        this.overlaps.addAll(newOverlaps);
    }

    public String getDMNMessageString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("Gaps: ");
        builder.append("\n");
        for (Hyperrectangle g : gaps) {
            builder.append(g);
            builder.append("\n");
        }
        builder.append(";");
        return MsgUtil.createMessage(Msg.DTANALYSISRESULT, builder.toString());
    }

    public List<? extends DMNMessage> asDMNMessages() {
        List<? extends DMNMessage> results = new ArrayList<>();
        results.addAll(gapsAsMessages());
        return results;
    }

    private Collection gapsAsMessages() {
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (Hyperrectangle gap : gaps) {
            results.add(new DMNDTAnalysisMessage(this, Severity.INFO, MsgUtil.createMessage(Msg.DTANALYSIS_GAP, gap), DMNMessageType.DECISION_TABLE_GAP));
        }
        return results;
    }

}
