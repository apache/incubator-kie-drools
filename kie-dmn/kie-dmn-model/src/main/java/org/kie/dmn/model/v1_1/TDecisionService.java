package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.api.InformationItem;

public class TDecisionService extends TNamedElement implements DecisionService {

    /**
     * This is not defined in the v1.1 XSD but used in this pojo for full backport of Decision Service onto v1.1 runtime. 
     */
    private InformationItem variable;
    private List<DMNElementReference> outputDecision;
    private List<DMNElementReference> encapsulatedDecision;
    private List<DMNElementReference> inputDecision;
    private List<DMNElementReference> inputData;

    @Override
    public InformationItem getVariable() {
        return variable;
    }

    @Override
    public void setVariable(InformationItem variable) {
        this.variable = variable;
    }

    @Override
    public List<DMNElementReference> getOutputDecision() {
        if ( outputDecision == null ) {
            outputDecision = new ArrayList<>();
        }
        return this.outputDecision;
    }

    @Override
    public List<DMNElementReference> getEncapsulatedDecision() {
        if ( encapsulatedDecision == null ) {
            encapsulatedDecision = new ArrayList<>();
        }
        return this.encapsulatedDecision;
    }

    @Override
    public List<DMNElementReference> getInputDecision() {
        if ( inputDecision == null ) {
            inputDecision = new ArrayList<>();
        }
        return this.inputDecision;
    }

    @Override
    public List<DMNElementReference> getInputData() {
        if ( inputData == null ) {
            inputData = new ArrayList<>();
        }
        return this.inputData;
    }

    @Override
    public String toString() {
        return getName();
    }

}
