package org.drools.rule.constraint;

import org.drools.base.DroolsQuery;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.base.mvel.MVELCompilationUnit;
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
import org.drools.spi.ExecutorFactory;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.util.CompositeClassLoader;
import org.mvel2.ParserConfiguration;
import org.drools.rule.constraint.ConditionAnalyzer.*;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableStatement;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.drools.core.util.ClassUtils.*;
import static org.drools.core.util.StringUtils.extractFirstIdentifier;
import static org.drools.core.util.StringUtils.skipBlanks;

public class MvelConstraint extends MutableTypeConstraint implements IndexableConstraint {
    private static final boolean TEST_JITTING = false;
    private static final int JIT_THRESOLD = 20; // Integer.MAX_VALUE;

    private transient AtomicInteger invocationCounter = new AtomicInteger(1);
    private transient boolean jitted = false;

    private String packageName;
    private String expression;
    private boolean isIndexable;
    private Declaration[] declarations;
    private Declaration indexingDeclaration;
    private InternalReadAccessor extractor;
    private boolean isUnification;
    private boolean isDynamic;
    private FieldValue fieldValue;

    private MVELCompilationUnit compilationUnit;

    private transient volatile ConditionEvaluator conditionEvaluator;
    private transient volatile Condition analyzedCondition;

    public MvelConstraint() {}

    public MvelConstraint(String packageName, String expression, MVELCompilationUnit compilationUnit, boolean isIndexable, FieldValue fieldValue, InternalReadAccessor extractor) {
        this.packageName = packageName;
        this.expression = expression;
        this.compilationUnit = compilationUnit;
        this.isIndexable = isIndexable;
        this.declarations = new Declaration[0];
        this.fieldValue = fieldValue;
        this.extractor = extractor;
    }

    public MvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, boolean isDynamic) {
        this.packageName = packageName;
        this.expression = expression;
        this.declarations = declarations;
        this.compilationUnit = compilationUnit;
        this.isDynamic = isDynamic;
    }

    public MvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit,
                          boolean isIndexable, Declaration indexingDeclaration, InternalReadAccessor extractor, boolean isUnification) {
        this.packageName = packageName;
        this.expression = expression;
        this.compilationUnit = compilationUnit;
        this.isIndexable = isIndexable && indexingDeclaration != null;
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
        return isIndexable;
    }

    public FieldValue getField() {
        return fieldValue;
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (isUnification) {
            throw new UnsupportedOperationException( "Should not be called" );
        }

        return evaluate(handle.getObject(), workingMemory, null);
    }

    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        if (isUnification) {
            if (((UnificationContextEntry)context).getVariable() != null) {
                return true;
            }
            context = ((UnificationContextEntry)context).getContextEntry();
        }

        MvelContextEntry mvelContextEntry = (MvelContextEntry)context;
        return evaluate(handle.getObject(), mvelContextEntry.workingMemory, mvelContextEntry.leftTuple);
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
        return evaluate(mvelContextEntry.right, mvelContextEntry.workingMemory, tuple);
    }

    private boolean evaluate(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        if (!jitted) {
            if (conditionEvaluator == null) {
                createMvelConditionEvaluator(workingMemory);
                if (TEST_JITTING && !isDynamic) { // Only for test purposes
                    boolean mvelValue = forceJitEvaluator(object, workingMemory, leftTuple);
                }
            }

            if (!isDynamic && invocationCounter.getAndIncrement() == JIT_THRESOLD) {
                jitEvaluator(object, workingMemory, leftTuple);
            }
        }
        return conditionEvaluator.evaluate(object, workingMemory, leftTuple);
    }

    private void createMvelConditionEvaluator(InternalWorkingMemory workingMemory) {
        if (compilationUnit != null) {
            MVELDialectRuntimeData data = getMVELDialectRuntimeData(workingMemory);
            ExecutableStatement statement = (ExecutableStatement)compilationUnit.getCompiledExpression(data);
            ParserContext context = statement instanceof CompiledExpression ?
                    ((CompiledExpression)statement).getParserContext() :
                    new ParserContext(data.getParserConfiguration());
            conditionEvaluator = new MvelConditionEvaluator(compilationUnit, context, statement, declarations);
        } else {
            conditionEvaluator = new MvelConditionEvaluator(getParserConfiguration(workingMemory), expression, declarations);
        }
    }

    private boolean forceJitEvaluator(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        boolean mvelValue;
        try {
            mvelValue = conditionEvaluator.evaluate(object, workingMemory, leftTuple);
        } catch (ClassCastException cce) {
            mvelValue = false;
        }
        jitEvaluator(object, workingMemory, leftTuple);
        return mvelValue;
    }

    private void jitEvaluator(final Object object, final InternalWorkingMemory workingMemory, final LeftTuple leftTuple) {
        jitted = true;
        try {
            if (TEST_JITTING) {
                executeJitting(object, workingMemory, leftTuple);
            } else {
                ExecutorFactory.getExecutor().execute(new Runnable() {
                    public void run() {
                        executeJitting(object, workingMemory, leftTuple);
                    }
                });
            }
        } catch (Throwable t) {
            throw new RuntimeException("Exception jitting: " + expression, t);
        }
    }

    private void executeJitting(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        CompositeClassLoader classLoader = ((AbstractRuleBase)workingMemory.getRuleBase()).getRootClassLoader();
        if (analyzedCondition == null) {
            analyzedCondition = ((MvelConditionEvaluator) conditionEvaluator).getAnalyzedCondition(object, workingMemory, leftTuple);
        }
        conditionEvaluator = ASMConditionEvaluatorJitter.jitEvaluator(expression, analyzedCondition, declarations, classLoader, leftTuple);
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
                if (compilationUnit != null) {
                    compilationUnit.replaceDeclaration(declarations[i], newDecl);
                }
                declarations[i] = newDecl;
                break;
            }
        }

        if (indexingDeclaration != null && indexingDeclaration.equals(oldDecl)) {
            indexingDeclaration = newDecl;
        }
    }

    // Slot specific

    public long getListenedPropertyMask(List<String> settableProperties) {
        if (conditionEvaluator == null) {
            return calculateMaskFromExpression(settableProperties);
        }
        if (analyzedCondition == null) {
            analyzedCondition = ((MvelConditionEvaluator) conditionEvaluator).getAnalyzedCondition();
        }
        return calculateMask(analyzedCondition, settableProperties);
    }

    private long calculateMaskFromExpression(List<String> settableProperties) {
        long mask = 0;
        String[] simpleExpressions = expression.split("\\Q&&\\E|\\Q||\\E");

        for (String simpleExpression : simpleExpressions) {
            String propertyName = getPropertyNameFromSimpleExpression(simpleExpression);
            if (propertyName.length() == 0) {
                continue;
            }
            if (propertyName.equals("this")) {
                return Long.MAX_VALUE;
            }
            int pos = settableProperties.indexOf(propertyName);
            if (pos < 0 && Character.isUpperCase(propertyName.charAt(0))) {
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                pos = settableProperties.indexOf(propertyName);
            }
            if (pos >= 0) { // Ignore not settable properties
                mask = BitMaskUtil.set(mask, pos);
            }
        }

        return mask;
    }

    private String getPropertyNameFromSimpleExpression(String simpleExpression) {
        StringBuilder propertyNameBuilder = new StringBuilder();
        int cursor = extractFirstIdentifier(simpleExpression, propertyNameBuilder, 0);

        String propertyName = propertyNameBuilder.toString();
        if (propertyName.equals("this")) {
            cursor = skipBlanks(simpleExpression, cursor);
            if (simpleExpression.charAt(cursor) != '.') {
                return "this";
            }
            propertyNameBuilder = new StringBuilder();
            extractFirstIdentifier(simpleExpression, propertyNameBuilder, cursor);
            propertyName = propertyNameBuilder.toString();
        }
        return propertyName;
    }

    private long calculateMask(Condition condition, List<String> settableProperties) {
        if (condition instanceof SingleCondition) {
            return calculateMask((SingleCondition) condition, settableProperties);
        }
        long mask = 0L;
        for (Condition c : ((CombinedCondition)condition).getConditions()) {
            mask |= calculateMask(c, settableProperties);
        }
        return mask;
    }

    private long calculateMask(SingleCondition condition, List<String> settableProperties) {
        String propertyName = getFirstInvokedPropertyName(condition.getLeft());
        if (propertyName == null) {
            return Long.MAX_VALUE;
        }

        int pos = settableProperties.indexOf(propertyName);
        if (pos < 0) {
            throw new RuntimeException("Unknown property: " + propertyName);
        }
        return 1L << pos;
    }

    private String getFirstInvokedPropertyName(Expression expression) {
        if (!(expression instanceof EvaluatedExpression)) {
            return null;
        }
        List<Invocation> invocations = ((EvaluatedExpression)expression).invocations;
        Invocation invocation = invocations.get(0);

        if (invocation instanceof MethodInvocation) {
            Method method = ((MethodInvocation)invocation).getMethod();
            if (method == null && invocations.size() > 1) {
                invocation = invocations.get(1);
                if (invocation instanceof MethodInvocation) {
                    method = ((MethodInvocation)invocation).getMethod();
                } else if (invocation instanceof FieldAccessInvocation) {
                    return ((FieldAccessInvocation)invocation).getField().getName();
                }
            }
            return getter2property(method.getName());
        }

        if (invocation instanceof FieldAccessInvocation) {
            return ((FieldAccessInvocation)invocation).getField().getName();
        }

        return null;
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
        out.writeBoolean(isDynamic);
        out.writeObject(fieldValue);
        out.writeObject(compilationUnit);
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
        isDynamic = in.readBoolean();
        fieldValue = (FieldValue) in.readObject();
        compilationUnit = (MVELCompilationUnit) in.readObject();
    }

    public boolean isTemporal() {
        return false;
    }

    public Object clone() {
        MvelConstraint clone = new MvelConstraint();
        clone.setType(getType());
        clone.packageName = packageName;
        clone.expression = expression;
        clone.isIndexable = isIndexable;
        clone.declarations = declarations;
        clone.indexingDeclaration = indexingDeclaration;
        clone.extractor = extractor;
        clone.isUnification = isUnification;
        clone.isDynamic = isDynamic;
        clone.conditionEvaluator = conditionEvaluator;
        clone.compilationUnit = compilationUnit;
        return clone;
    }

    public int hashCode() {
        return expression.hashCode();
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || object.getClass() != MvelConstraint.class ) {
            return false;
        }
        MvelConstraint other = (MvelConstraint) object;
        if (!expression.equals(other.expression)) {
            return false;
        }
        if (declarations.length != other.declarations.length) {
            return false;
        }
        for (int i = 0; i < declarations.length; i++) {
            if ( !declarations[i].getExtractor().equals( other.declarations[i].getExtractor() ) ) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return expression;
    }

    private ParserConfiguration getParserConfiguration(InternalWorkingMemory workingMemory) {
        return getMVELDialectRuntimeData(workingMemory).getParserConfiguration();
    }

    private MVELDialectRuntimeData getMVELDialectRuntimeData(InternalWorkingMemory workingMemory) {
        return ((MVELDialectRuntimeData)workingMemory.getRuleBase().getPackage(packageName).getDialectRuntimeRegistry().getDialectData( "mvel" ));
    }

    // MvelArrayContextEntry

    public static class MvelContextEntry implements ContextEntry {

        protected ContextEntry next;
        protected LeftTuple leftTuple;
        protected Object right;
        protected Declaration[] declarations;

        protected transient InternalWorkingMemory workingMemory;

        public MvelContextEntry() { }

        public MvelContextEntry(Declaration[] declarations) {
            this.declarations = declarations;
        }

        public ContextEntry getNext() {
            return this.next;
        }

        public void setNext(final ContextEntry entry) {
            this.next = entry;
        }

        public void updateFromTuple(InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
            this.leftTuple = leftTuple;
            this.workingMemory = workingMemory;
        }

        public void updateFromFactHandle(InternalWorkingMemory workingMemory, InternalFactHandle handle) {
            this.workingMemory = workingMemory;
            right = handle.getObject();
        }

        public void resetTuple() {
            leftTuple = null;
        }

        public void resetFactHandle() {
            workingMemory = null;
            right = null;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(leftTuple);
            out.writeObject(right);
            out.writeObject(declarations);
            out.writeObject(next);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            leftTuple = (LeftTuple)in.readObject();
            right = in.readObject();
            declarations = (Declaration[])in.readObject();
            next = (ContextEntry)in.readObject();
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
