package org.drools.lang.descr;

import org.drools.rule.EvalCondition;
import org.drools.rule.NamedConsequence;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ConditionalBranchDescr extends BaseDescr {
    private EvalDescr condition = EvalDescr.TRUE;

    private NamedConsequenceDescr consequence;

    private ConditionalBranchDescr elseBranch;

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal( in );
        condition = (EvalDescr) in.readObject();
        consequence = (NamedConsequenceDescr) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject( condition );
        out.writeObject( consequence );
    }

    public EvalDescr getCondition() {
        return condition;
    }

    public void setCondition(EvalDescr condition) {
        this.condition = condition;
    }

    public NamedConsequenceDescr getConsequence() {
        return consequence;
    }

    public void setConsequence(NamedConsequenceDescr consequence) {
        this.consequence = consequence;
    }

    public ConditionalBranchDescr getElseBranch() {
        return elseBranch;
    }

    public void setElseBranch(ConditionalBranchDescr elseBranch) {
        this.elseBranch = elseBranch;
    }

    @Override
    public String toString() {
        return "if ( " + condition + " ) " + consequence + (elseBranch != null ? " else " + elseBranch : "");
    }
}
