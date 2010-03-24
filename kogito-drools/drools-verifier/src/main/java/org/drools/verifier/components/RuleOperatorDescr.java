package org.drools.verifier.components;

import java.io.Serializable;

/**
 * 
 * @author Toni Rikkola
 */
public class RuleOperatorDescr extends RuleComponent
    implements
    Serializable {
    private static final long serialVersionUID = 8393994152436331910L;

    private OperatorDescrType type;

    public RuleOperatorDescr(VerifierRule rule,
                             OperatorDescrType operatorType) {
        super( rule );
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
