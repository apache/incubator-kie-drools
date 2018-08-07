package org.kie.dmn.model.v1x;

import java.util.List;

public interface PerformanceIndicator extends BusinessContextElement {

    List<DMNElementReference> getImpactingDecision();

}
