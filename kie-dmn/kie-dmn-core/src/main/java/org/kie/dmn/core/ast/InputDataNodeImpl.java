package org.kie.dmn.core.ast;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.model.api.InputData;

public class InputDataNodeImpl extends DMNBaseNode
        implements InputDataNode {

    private DMNType dmnType;
    private InputData inputData;

    public InputDataNodeImpl() {
    }

    public InputDataNodeImpl(InputData inputData ) {
        this( inputData, null );
    }

    public InputDataNodeImpl(InputData inputData, DMNType dmnType ) {
        super( inputData );
        this.inputData = inputData;
        this.dmnType = dmnType;
    }

    public String getName() {
        return this.inputData.getName();
    }

    public DMNType getType() {
        return dmnType;
    }

    public void setType(DMNType dmnType) {
        this.dmnType = dmnType;
    }

    public InputData getInputData() {
        return inputData;
    }

    public void setInputData(InputData inputData) {
        this.inputData = inputData;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof InputDataNodeImpl) ) return false;

        InputDataNodeImpl that = (InputDataNodeImpl) o;

        return inputData != null ? inputData.equals( that.inputData ) : that.inputData == null;
    }

    @Override
    public int hashCode() {
        return inputData != null ? inputData.hashCode() : 0;
    }
}
