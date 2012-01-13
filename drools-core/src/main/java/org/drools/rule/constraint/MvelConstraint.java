package org.drools.rule.constraint;

import org.drools.base.DroolsQuery;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.BitMaskUtil;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.IndexEvaluator;
import org.drools.rule.IndexableConstraint;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.MutableTypeConstraint;
import org.drools.runtime.rule.Variable;
import org.drools.spi.InternalReadAccessor;
import org.drools.util.CompositeClassLoader;
import org.mvel2.ParserConfiguration;
import org.mvel2.compiler.ExecutableStatement;
import org.drools.rule.constraint.ConditionAnalyzer.*;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.drools.core.util.ClassUtils.*;

public class MvelConstraint extends MutableTypeConstraint implements IndexableConstraint {
    private static final boolean TEST_JITTING = false;
    private static final int JIT_THRESOLD = 1; // Integer.MAX_VALUE;

    private transient AtomicInteger invocationCounter = new AtomicInteger(1);
    private transient boolean jitted = false;

    private String packageName;
    private String expression;
    private boolean isIndexable;
    private Declaration[] declarations;
    private Declaration indexingDeclaration;
    private InternalReadAccessor extractor;
    private boolean isUnification;

    private transient ConditionEvaluator conditionEvaluator;
    private transient Condition analyzedCondition;

    public MvelConstraint() {}

    public MvelConstraint(String packageName, String expression) {
        this(packageName, expression, false, null, null, null, false);
    }

    public MvelConstraint(String packageName, String expression, boolean isIndexable, Declaration[] declarations,
                          Declaration indexingDeclaration, InternalReadAccessor extractor, boolean isUnification) {
        this.packageName = packageName;
        this.expression = expression;
        this.isIndexable = isIndexable;
        this.declarations = declarations == null ? new Declaration[0] : declarations;
        this.indexingDeclaration = indexingDeclaration;
        this.extractor = extractor;
        this.isUnification = isUnification;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getExpression() {
        return expression;
    }

    public boolean isUnification() {
        return isUnification;
    }

    public void unsetUnification() {
        isUnification = false;
    }

    public boolean isIndexable() {
        return isIndexable && indexingDeclaration != null;
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (isUnification) {
            throw new UnsupportedOperationException( "Should not be called" );
        }

        Map<String, Object> vars = context == null ? null : ((MvelContextEntry)context).getRightVars(workingMemory, handle);
        return evaluate(handle.getObject(), workingMemory, vars);
    }

    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        if (isUnification) {
            if (((UnificationContextEntry)context).getVariable() != null) {
                return true;
            }
            context = ((UnificationContextEntry)context).getContextEntry();
        }

        MvelContextEntry mvelContextEntry = (MvelContextEntry)context;
        return evaluate(handle.getObject(), mvelContextEntry.workingMemory, mvelContextEntry.vars);
    }

    public boolean isAllowedCachedRight(LeftTuple tuple, ContextEntry context) {
        if (isUnification) {
            DroolsQuery query = ( DroolsQuery ) tuple.get( 0 ).getObject();
            Variable v = query.getVariables()[ ((UnificationContextEntry)context).getReader().getIndex() ];

            if (v != null) {
                return true;
            }
            context = ((UnificationContextEntry)context).getContextEntry();
        }

        MvelContextEntry mvelContextEntry = (MvelContextEntry)context;
        return evaluate(mvelContextEntry.right, mvelContextEntry.workingMemory, mvelContextEntry.getRightVars(tuple));
    }

    private boolean evaluate(Object object, InternalWorkingMemory workingMemory, Map<String, Object> vars) {
        if (!jitted) {
            if (conditionEvaluator == null) {
                createMvelConditionEvaluator(workingMemory);
                if (TEST_JITTING) { // TO BE REMOVED
                    boolean mvelValue = forceJitEvaluator(workingMemory, object, vars);
                }
            } else if (invocationCounter.getAndIncrement() == JIT_THRESOLD) {
                jitEvaluator(workingMemory, object, vars);
            }
        }

        try {
            return conditionEvaluator.evaluate(object, vars);
        } catch (ClassCastException cce) {
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private void createMvelConditionEvaluator(InternalWorkingMemory workingMemory) {
        conditionEvaluator = new MvelConditionEvaluator(getParserConfiguration(workingMemory), expression);
    }

    private boolean forceJitEvaluator(InternalWorkingMemory workingMemory, Object object, Map<String, Object> vars) {
        boolean mvelValue;
        try {
            mvelValue = conditionEvaluator.evaluate(object, vars);
        } catch (ClassCastException cce) {
            mvelValue = false;
        }
        jitEvaluator(workingMemory, object, vars);
        return mvelValue;
    }

    private void jitEvaluator(InternalWorkingMemory workingMemory, Object object, Map<String, Object> vars) {
        jitted = true;
        try {
            CompositeClassLoader classLoader = ((AbstractRuleBase)workingMemory.getRuleBase()).getRootClassLoader();
            if (analyzedCondition == null) {
                analyzedCondition = ((MvelConditionEvaluator) conditionEvaluator).getAnalyzedCondition(object, vars);
            }
            conditionEvaluator = ASMConditionEvaluatorJitter.jit(analyzedCondition, classLoader);
        } catch (Throwable t) {
            throw new RuntimeException("Exception jitting: " + expression, t);
        }
    }

    public ContextEntry createContextEntry() {
        if (declarations.length == 0) return null;
        ContextEntry contextEntry = new MvelContextEntry(declarations);
        if (isUnification) {
            contextEntry = new UnificationContextEntry(contextEntry, declarations[0]);
        }
        return contextEntry;
    }

    public FieldIndex getFieldIndex() {
        // declaration's offset can be modified by the reteoo's PatternBuilder so modify the indexingDeclaration accordingly
        indexingDeclaration.getPattern().setOffset(declarations[0].getPattern().getOffset());
        return new FieldIndex(extractor, indexingDeclaration, INDEX_EVALUATOR);
    }

    public InternalReadAccessor getFieldExtractor() {
        return extractor;
    }

    public Declaration[] getRequiredDeclarations() {
        return declarations;
    }

    public Declaration getIndexingDeclaration() {
        return indexingDeclaration;
    }

    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        for (int i = 0; i < declarations.length; i++) {
            if (declarations[i].equals(oldDecl)) {
                declarations[i] = newDecl;
                break;
            }
        }

        if (indexingDeclaration != null && indexingDeclaration.equals(oldDecl)) {
            indexingDeclaration = newDecl;
        }
    }

    // Slot specific

    public long getListenedPropertyMask(Class<?> nodeClass) {
        if (conditionEvaluator == null) return 0L;
        if (analyzedCondition == null) {
            analyzedCondition = ((MvelConditionEvaluator) conditionEvaluator).getAnalyzedCondition();
        }
        return calculateMask(analyzedCondition, nodeClass, getSettableProperties(nodeClass));
    }

    private long calculateMask(Condition condition, Class<?> nodeClass, List<String> settableProperties) {
        if (condition instanceof SingleCondition) {
            return calculateMask((SingleCondition)condition, nodeClass, settableProperties);
        }
        long mask = 0L;
        for (Condition c : ((CombinedCondition)condition).getConditions()) {
            mask |= calculateMask(c, nodeClass, settableProperties);
        }
        return mask;
    }

    private long calculateMask(SingleCondition condition, Class<?> nodeClass, List<String> settableProperties) {
        Method method = getFirstInvokedMethod(condition.getLeft());
        if (method == null) return Long.MAX_VALUE;
        String propertyName = getter2property(method.getName());
        if (propertyName != null) {
            int pos = settableProperties.indexOf(propertyName);
            if (pos < 0) throw new RuntimeException("Unknown property " + propertyName + " for " + nodeClass);
            return 1L << pos;
        }

        // Invocation of a non-getter => cannot calculate the mask
        return Long.MAX_VALUE;
    }

    private Method getFirstInvokedMethod(Expression expression) {
        if (!(expression instanceof EvaluatedExpression)) return null;
        List<Invocation> invocations = ((EvaluatedExpression)expression).invocations;
        Invocation invocation = invocations.get(0);
        if (!(invocation instanceof MethodInvocation)) return null;
        Method method = ((MethodInvocation)invocation).getMethod();
        if (method == null && invocations.size() > 1) {
            invocation = invocations.get(1);
            if (invocation instanceof MethodInvocation) method = ((MethodInvocation)invocation).getMethod();
        }
        return method;
    }

    // Externalizable

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(packageName);
        out.writeObject(expression);
        out.writeObject(declarations);
        out.writeObject(indexingDeclaration);
        out.writeObject(extractor);
        out.writeBoolean(isIndexable);
        out.writeBoolean(isUnification);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        packageName = (String)in.readObject();
        expression = (String)in.readObject();
        declarations = (Declaration[]) in.readObject();
        indexingDeclaration = (Declaration) in.readObject();
        extractor = (InternalReadAccessor) in.readObject();
        isIndexable = in.readBoolean();
        isUnification = in.readBoolean();
    }

    public boolean isTemporal() {
        return false;
    }

    public Object clone() {
        return new MvelConstraint(packageName, expression, isIndexable, declarations, indexingDeclaration, extractor, isUnification);
    }

    public int hashCode() {
        return expression.hashCode();
    }

    public boolean equals(final Object object) {
        if ( this == object ) return true;
        if ( object == null || object.getClass() != MvelConstraint.class ) return false;
        return expression.equals(((MvelConstraint) object).expression);
    }

    private ParserConfiguration getParserConfiguration(InternalWorkingMemory workingMemory) {
        return ((MVELDialectRuntimeData)workingMemory.getRuleBase().getPackage(packageName).getDialectRuntimeRegistry().getDialectData( "mvel" )).getParserConfiguration();
    }

    // MvelContextEntry

    public static class MvelContextEntry implements ContextEntry {

        private Object left;
        private Object right;
        private Declaration[] declarations;
        private ContextEntry next;

        private transient Map<String, Object> vars;
        private transient InternalWorkingMemory workingMemory;

        public MvelContextEntry() { }

        public MvelContextEntry(Declaration[] declarations) {
            this.declarations = declarations;
            if (declarations.length > 0 ) {
                vars = new HashMap<String, Object>();
            }
        }

        public ContextEntry getNext() {
            return this.next;
        }

        public void setNext(final ContextEntry entry) {
            this.next = entry;
        }

        public void updateFromTuple(InternalWorkingMemory workingMemory, LeftTuple tuple) {
            this.workingMemory = workingMemory;
            Declaration declaration = declarations[0];
            InternalFactHandle handle = tuple.get(declaration);
            left = declaration.getExtractor().getValue(workingMemory, handle.getObject());
            if (declarations.length == 1) {
                vars.put(declaration.getBindingName(), left);
            } else {
                getRightVars(tuple);
            }
        }

        public void updateFromFactHandle(InternalWorkingMemory workingMemory, InternalFactHandle handle) {
            this.workingMemory = workingMemory;
            right = handle.getObject();
        }

        Map<String, Object> getRightVars(LeftTuple tuple) {
            if (declarations.length == 0) return null;
            for (Declaration declaration : declarations) {
                try {
                    vars.put(declaration.getBindingName(), declaration.getExtractor().getValue(workingMemory, tuple.get(declaration).getObject()));
                } catch (NullPointerException npe) {
                    vars.put(declaration.getBindingName(), declarations[0].getExtractor().getValue(workingMemory, tuple.get(declarations[0]).getObject()));
                }
            }
            return vars;
        }

        Map<String, Object> getRightVars(InternalWorkingMemory workingMemory, InternalFactHandle handle) {
            if (declarations.length == 0) return null;
            for (Declaration declaration : declarations) {
                vars.put(declaration.getBindingName(), declaration.getExtractor().getValue(workingMemory, handle.getObject()));
            }
            return vars;
        }

        public void resetTuple() {
            left = null;
            if (vars != null) vars.clear();
        }

        public void resetFactHandle() {
            workingMemory = null;
            right = null;
            if (vars != null) vars.clear();
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            left = in.readObject();
            right = in.readObject();
            declarations = (Declaration[])in.readObject();
            next = (ContextEntry)in.readObject();
            if (declarations.length > 0 ) {
                vars = new HashMap<String, Object>();
            }
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(left);
            out.writeObject(right);
            out.writeObject(declarations);
            out.writeObject(next);
        }
    }

    public static class UnificationContextEntry implements ContextEntry {
        private ContextEntry contextEntry;
        private Declaration declaration;
        private Variable variable;
        private ArrayElementReader reader;

        public UnificationContextEntry() { }

        public UnificationContextEntry(ContextEntry contextEntry,
                                       Declaration declaration) {
            this.contextEntry = contextEntry;
            this.declaration = declaration;
            reader = ( ArrayElementReader ) this.declaration.getExtractor();
        }

        public ContextEntry getContextEntry() {
            return this.contextEntry;
        }

        public ArrayElementReader getReader() {
            return reader;
        }

        public ContextEntry getNext() {
            return this.contextEntry.getNext();
        }

        public void resetFactHandle() {
            this.contextEntry.resetFactHandle();
        }

        public void resetTuple() {
            this.contextEntry.resetTuple();
            this.variable = null;
        }

        public void setNext(ContextEntry entry) {
            this.contextEntry.setNext( entry );
        }

        public void updateFromFactHandle(InternalWorkingMemory workingMemory,
                                         InternalFactHandle handle) {
            this.contextEntry.updateFromFactHandle( workingMemory, handle );
        }

        public void updateFromTuple(InternalWorkingMemory workingMemory,
                                    LeftTuple tuple) {
            DroolsQuery query = ( DroolsQuery ) tuple.get( 0 ).getObject();
            Variable v = query.getVariables()[ this.reader.getIndex() ];
            if ( v == null ) {
                // if there is no Variable, handle it as a normal constraint
                this.variable = null;
                this.contextEntry.updateFromTuple( workingMemory, tuple );
            } else {
                this.variable = v;
            }
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            this.contextEntry = (ContextEntry) in.readObject();
            this.declaration = ( Declaration ) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( this.contextEntry );
            out.writeObject( this.declaration );
        }

        public Variable getVariable() {
            return this.variable;
        }

    }

    public static final IndexEvaluator INDEX_EVALUATOR = new PlainIndexEvaluator();
    public static class PlainIndexEvaluator implements IndexEvaluator {
        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            if (value1 == null) {
                return value2 == null;
            }
            if (value1 instanceof String) {
                return value1.equals(value2.toString());
            }
            return value1.equals( value2 );
        }
    }
}
