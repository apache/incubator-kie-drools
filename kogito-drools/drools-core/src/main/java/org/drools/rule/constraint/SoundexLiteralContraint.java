package org.drools.rule.constraint;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.ContextEntry;
import org.mvel2.MVEL;
import org.mvel2.util.Soundex;

import java.io.Serializable;
import java.util.Collection;

public class SoundexLiteralContraint extends AbstractLiteralConstraint {

    private transient Serializable leftCompiledExpression;
    private transient Serializable rightCompiledExpression;

    public SoundexLiteralContraint() { }

    public SoundexLiteralContraint(String[] imports, String leftValue, String operator, String rightValue) {
        super(imports, leftValue, operator, rightValue);
    }

    public Object clone() {
        return new SoundexLiteralContraint(imports, leftValue, operator, rightValue);
    }

    private void compile() {
        String imports = getImportString();
        leftCompiledExpression = MVEL.compileExpression(imports + leftValue);
        rightCompiledExpression = MVEL.compileExpression(imports + rightValue);
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (leftCompiledExpression == null) compile();

        String value1 = (String)MVEL.executeExpression(leftCompiledExpression, handle.getObject());
        if (value1 == null) {
            return false;
        }
        String soundex1 = Soundex.soundex(value1);
        if (soundex1 == null) {
            return false;
        }

        String value2 = (String)MVEL.executeExpression(rightCompiledExpression, handle.getObject());
        if (value2 == null) {
            return false;
        }

        return soundex1.equals(Soundex.soundex(value2));
    }
}
