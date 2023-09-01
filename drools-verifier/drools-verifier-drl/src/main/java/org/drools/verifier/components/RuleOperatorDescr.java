package org.drools.verifier.components;

import java.io.Serializable;

import org.drools.drl.ast.descr.BaseDescr;

public class RuleOperatorDescr extends RuleComponent
    implements
    Serializable {
    private static final long serialVersionUID = 510l;

    private OperatorDescrType type;

    public RuleOperatorDescr(BaseDescr descr, VerifierRule rule,
                             OperatorDescrType operatorType) {
        super( descr, rule.getPackageName(),
            rule.getName() );
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
