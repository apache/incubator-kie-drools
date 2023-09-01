package org.kie.dmn.validation.dtanalysis;

import java.util.Collection;

import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

public class DMNDTAnalysisMessage extends DMNMessageImpl {

    private final DTAnalysis analysis;
    private final Collection<Integer> rules;

    public DMNDTAnalysisMessage(DTAnalysis analysis, Severity severity, String message, DMNMessageType messageType) {
        this(analysis, severity, message, messageType, null);
    }

    public DMNDTAnalysisMessage(DTAnalysis analysis, Severity severity, String message, DMNMessageType messageType, Collection<Integer> rules) {
        super(severity, message, messageType, analysis.getSource());
        this.analysis = analysis;
        this.rules = rules;
    }


    public DTAnalysis getAnalysis() {
        return analysis;
    }

    public Collection<Integer> getRules() {
       return rules;
    }

}
