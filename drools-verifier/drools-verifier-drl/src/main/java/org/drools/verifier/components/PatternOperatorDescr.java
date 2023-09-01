package org.drools.verifier.components;

import java.io.Serializable;

public class PatternOperatorDescr extends PatternComponent
    implements
    Serializable {
    private static final long serialVersionUID = 510l;

    private OperatorDescrType type;

    public PatternOperatorDescr(Pattern pattern,
                                OperatorDescrType operatorType) {
        super( pattern );
        this.type = operatorType;
    }

    public OperatorDescrType getType() {
        return type;
    }

    public void setType(OperatorDescrType type) {
        this.type = type;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.OPERATOR;
    }

}
