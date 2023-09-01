package org.kie.dmn.model.v1_2;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DecisionService;

public class TDecisionService extends TInvocable implements DecisionService {

    protected List<DMNElementReference> outputDecision;
    protected List<DMNElementReference> encapsulatedDecision;
    protected List<DMNElementReference> inputDecision;
    protected List<DMNElementReference> inputData;

    @Override
    public List<DMNElementReference> getOutputDecision() {
        if (outputDecision == null) {
            outputDecision = new ArrayList<>();
        }
        return this.outputDecision;
    }

    @Override
    public List<DMNElementReference> getEncapsulatedDecision() {
        if (encapsulatedDecision == null) {
            encapsulatedDecision = new ArrayList<>();
        }
        return this.encapsulatedDecision;
    }

    @Override
    public List<DMNElementReference> getInputDecision() {
        if (inputDecision == null) {
            inputDecision = new ArrayList<>();
        }
        return this.inputDecision;
    }

    @Override
    public List<DMNElementReference> getInputData() {
        if (inputData == null) {
            inputData = new ArrayList<>();
        }
        return this.inputData;
    }

}
