package org.kie.dmn.validation.dtanalysis.model;

import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.impl.DMNMessageImpl;

public class DMNDTAnalysisMessage extends DMNMessageImpl {

    private final DTAnalysis analysis;

    public DMNDTAnalysisMessage(DTAnalysis analysis, Severity severity, String message, DMNMessageType messageType) {
        super(severity, message, messageType, analysis.getSource());
        this.analysis = analysis;
    }

    public DTAnalysis getAnalysis() {
        return analysis;
    }

}
