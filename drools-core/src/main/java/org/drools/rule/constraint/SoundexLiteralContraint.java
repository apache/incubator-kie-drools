package org.drools.rule.constraint;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.ContextEntry;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.util.Soundex;

import java.io.Serializable;
import java.util.Collection;

public class SoundexLiteralContraint extends AbstractLiteralConstraint {

    private transient Serializable leftCompiledExpression;
    private transient Serializable rightCompiledExpression;

    public SoundexLiteralContraint() { }

    public SoundexLiteralContraint(ParserConfiguration conf, String leftValue, String operator, String rightValue) {
        super(conf, leftValue, operator, rightValue);
    }

    public Object clone() {
        return new SoundexLiteralContraint(conf, leftValue, operator, rightValue);
    }

    private void compile(InternalWorkingMemory workingMemory) {
        ParserContext context = new ParserContext(getParserConfiguration(workingMemory));
        leftCompiledExpression = MVEL.compileExpression(leftValue, context);
        rightCompiledExpression = MVEL.compileExpression(rightValue, context);
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (leftCompiledExpression == null) compile(workingMemory);

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
