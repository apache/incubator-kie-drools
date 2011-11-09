package org.drools.rule.constraint;

import org.drools.base.ValueType;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.ContextEntry;
import org.mvel2.MVEL;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collection;

public class MvelLiteralConstraint extends AbstractLiteralConstraint {

    private ValueType type;
    private String mvelExp;

    private transient Serializable compiledExpression;

    public MvelLiteralConstraint() {}

    public MvelLiteralConstraint(Collection<String> imports, ValueType type, String mvelExp, String leftValue, String operator, String rightValue) {
        this(imports.toArray(new String[imports.size()]), type, mvelExp, leftValue, operator, rightValue);
    }

    public MvelLiteralConstraint(String[] imports, ValueType type, String mvelExp, String leftValue, String operator, String rightValue) {
        super(imports, leftValue, operator, rightValue);
        this.type = type;
        this.mvelExp = mvelExp;
    }

    private void compile() {
        compiledExpression = MVEL.compileExpression(getImportString() + mvelExp);
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (compiledExpression == null) compile();
        try {
            return (Boolean)MVEL.executeExpression(compiledExpression, handle.getObject());
        } catch (ClassCastException cce) {
            return false;
        }
    }

    // Externalizable

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(type);
        out.writeObject(mvelExp);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        type = (ValueType)in.readObject();
        mvelExp = (String)in.readObject();
    }

    public Object clone() {
        return new MvelLiteralConstraint(imports, type, mvelExp, leftValue, operator, rightValue);
    }

    public int hashCode() {
        return mvelExp.hashCode();
    }

    public boolean equals(final Object object) {
        if ( this == object ) return true;
        if ( object == null || object.getClass() != MvelLiteralConstraint.class ) return false;
        return mvelExp.equals(((MvelLiteralConstraint)object).mvelExp);
    }
}
