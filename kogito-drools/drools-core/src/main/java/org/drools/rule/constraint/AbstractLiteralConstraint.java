package org.drools.rule.constraint;

import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Constraint;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class AbstractLiteralConstraint implements AlphaNodeFieldConstraint, Externalizable {

    protected String[] imports;
    protected String leftValue;
    protected String operator;
    protected String rightValue;

    protected AbstractLiteralConstraint() { }

    protected AbstractLiteralConstraint(String[] imports, String leftValue, String operator, String rightValue) {
        this.imports = imports;
        this.leftValue = leftValue;
        this.operator = operator;
        this.rightValue = rightValue;
    }

    protected String getImportString() {
        StringBuilder sb = new StringBuilder();
        for (String imp : imports) sb.append("import ").append(imp).append("; ");
        return sb.toString();
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
        out.writeObject(imports);
        out.writeObject(leftValue);
        out.writeObject(operator);
        out.writeObject(rightValue);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        imports = (String[])in.readObject();
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
}
