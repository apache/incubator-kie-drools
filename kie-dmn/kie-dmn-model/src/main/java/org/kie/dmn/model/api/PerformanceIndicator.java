package org.kie.dmn.model.api;

import java.util.List;

public interface PerformanceIndicator extends BusinessContextElement {

    List<DMNElementReference> getImpactingDecision();

}
