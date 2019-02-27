package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public class DTAnalysis {

    private List<Hyperrectangle> gaps = new ArrayList<>();
    private final DMNModelInstrumentedBase sourceDT;

    public DTAnalysis(DMNModelInstrumentedBase sourceDT) {
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

}
