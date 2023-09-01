package org.kie.dmn.model.v1_4;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.PerformanceIndicator;


public class TPerformanceIndicator extends TBusinessContextElement implements PerformanceIndicator {

    protected List<DMNElementReference> impactingDecision;

    @Override
    public List<DMNElementReference> getImpactingDecision() {
        if (impactingDecision == null) {
            impactingDecision = new ArrayList<>();
        }
        return this.impactingDecision;
    }

}
