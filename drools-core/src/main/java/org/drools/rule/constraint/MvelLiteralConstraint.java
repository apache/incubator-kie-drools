package org.drools.rule.constraint;

import org.drools.base.ValueType;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.ContextEntry;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.util.CompositeClassLoader;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.atomic.AtomicInteger;

public class MvelLiteralConstraint extends AbstractLiteralConstraint {

    private static final boolean TEST_JITTING = true;
    private static final int JIT_THRESOLD = Integer.MAX_VALUE;
//  private static final int JIT_THRESOLD = 2;

    private AtomicInteger invocationCounter = new AtomicInteger(0);
    private boolean jitted = false;

    private ValueType type;
    private String mvelExp;

    private transient ConditionEvaluator conditionEvaluator;

    public MvelLiteralConstraint() {}

    public MvelLiteralConstraint(ParserConfiguration conf, String packageName, ValueType type, String mvelExp, String leftValue, String operator, String rightValue) {
        super(conf, packageName, leftValue, operator, rightValue);
        this.type = type;
        this.mvelExp = mvelExp;
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (TEST_JITTING) return isJITAllowed(handle, workingMemory, context);

        if (!jitted) {
            if (conditionEvaluator == null) {
                conditionEvaluator = new MvelConditionEvaluator(getParserConfiguration(workingMemory), mvelExp);
            } else {
                if (invocationCounter.getAndIncrement() == JIT_THRESOLD) {
                    CompositeClassLoader classLoader = ((AbstractRuleBase)workingMemory.getRuleBase()).getRootClassLoader();
                    conditionEvaluator = ASMConditionEvaluatorJitter.jit(((MvelConditionEvaluator)conditionEvaluator).getExecutableStatement(), classLoader);
                    jitted = true;
                }
            }
        }

        try {
            return conditionEvaluator.evaluate(handle.getObject());
        } catch (ClassCastException cce) {
            return false;
        }
    }

    public boolean isJITAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        boolean mvelValue = false;
        if (conditionEvaluator == null) {
            conditionEvaluator = new MvelConditionEvaluator(getParserConfiguration(workingMemory), mvelExp);
            try {
                mvelValue = conditionEvaluator.evaluate(handle.getObject());
            } catch (ClassCastException cce) {
                System.out.println("Got ClassCastException: " + cce);
                mvelValue = false;
            }
            try {
                CompositeClassLoader classLoader = ((AbstractRuleBase)workingMemory.getRuleBase()).getRootClassLoader();
                conditionEvaluator = ASMConditionEvaluatorJitter.jit(((MvelConditionEvaluator)conditionEvaluator).getExecutableStatement(), classLoader);
            } catch (Throwable t) {
                throw new RuntimeException("Exception jitting: " + mvelExp, t);
            }
        }

        boolean asmValue = false;
        try {
            asmValue = conditionEvaluator.evaluate(handle.getObject());
        } catch (ClassCastException cce) {
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }

//        System.out.println(mvelExp + " => mvel = " + mvelValue + "; asm = " + asmValue);
        return asmValue;
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
        return new MvelLiteralConstraint(conf, packageName, type, mvelExp, leftValue, operator, rightValue);
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
