package org.drools.rule.constraint;

import org.drools.common.InternalWorkingMemory;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Constraint;
import org.mvel2.ParserConfiguration;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class AbstractLiteralConstraint implements AlphaNodeFieldConstraint, Externalizable {

    protected transient ParserConfiguration conf;
    protected String leftValue;
    protected String operator;
    protected String rightValue;

    protected AbstractLiteralConstraint() { }

    protected AbstractLiteralConstraint(ParserConfiguration conf, String leftValue, String operator, String rightValue) {
        this.conf = conf;
        this.leftValue = leftValue;
        this.operator = operator;
        this.rightValue = rightValue;
    }

    public ContextEntry createContextEntry() {
        // no need for context info
        return null;
    }

    public Declaration[] getRequiredDeclarations() {
        return new Declaration[0];
    }

    // Externalizable

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(leftValue);
        out.writeObject(operator);
        out.writeObject(rightValue);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        leftValue = (String)in.readObject();
        operator = (String)in.readObject();
        rightValue = (String)in.readObject();
    }

    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) { }

    public ConstraintType getType() {
        return Constraint.ConstraintType.ALPHA;
    }

    public boolean isTemporal() {
        // todo
        return false;
    }

    public abstract Object clone();

    protected ParserConfiguration getParserConfiguration(InternalWorkingMemory workingMemory) {
        if (conf != null) return conf;
        MVELDialectRuntimeData data = (MVELDialectRuntimeData)workingMemory.getRuleBase().getPackages()[0].getDialectRuntimeRegistry().getDialectData( "mvel" );
        return data.getParserConfiguration();
    }
}
