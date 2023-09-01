package org.kie.dmn.model.v1_1.extensions;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase;

public class DecisionServices extends KieDMNModelInstrumentedBase {

    private List<DecisionService> decisionService;

    public List<DecisionService> getDecisionService() {
        if (decisionService == null) {
            decisionService = new ArrayList<>();
        }
        return this.decisionService;
    }

    @Override
    public String toString() {
        return decisionService.toString();
    }

}
