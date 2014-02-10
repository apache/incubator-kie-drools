package org.drools.core.rule.constraint;

import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.base.extractors.MVELObjectClassFieldReader;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.MemoryUtil;
import org.drools.core.util.index.IndexUtil;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.IndexEvaluator;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.rule.constraint.ConditionAnalyzer.CombinedCondition;
import org.drools.core.rule.constraint.ConditionAnalyzer.Condition;
import org.drools.core.rule.constraint.ConditionAnalyzer.EvaluatedExpression;
import org.drools.core.rule.constraint.ConditionAnalyzer.Expression;
import org.drools.core.rule.constraint.ConditionAnalyzer.FieldAccessInvocation;
import org.drools.core.rule.constraint.ConditionAnalyzer.Invocation;
import org.drools.core.rule.constraint.ConditionAnalyzer.MethodInvocation;
import org.drools.core.rule.constraint.ConditionAnalyzer.SingleCondition;
import org.drools.core.spi.AcceptsReadAccessor;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.kie.internal.concurrent.ExecutorProviderFactory;
import org.kie.api.runtime.rule.Variable;
import org.mvel2.ParserConfiguration;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.core.util.StringUtils.extractFirstIdentifier;
import static org.drools.core.util.StringUtils.skipBlanks;

public class MvelConstraint extends MutableTypeConstraint implements IndexableConstraint, AcceptsReadAccessor {
    protected static final boolean TEST_JITTING = false;
    protected static final int JIT_THRESOLD = 20; // Integer.MAX_VALUE;

    private static final Logger logger = LoggerFactory.getLogger(MvelConstraint.class);

    protected final transient AtomicInteger invocationCounter = new AtomicInteger(1);
    protected transient boolean jitted = false;

    private String packageName;
    protected String expression;
    private IndexUtil.ConstraintType constraintType = IndexUtil.ConstraintType.UNKNOWN;
    private Declaration[] declarations;
    private Declaration indexingDeclaration;
    private InternalReadAccessor extractor;
    private boolean isUnification;
    protected boolean isDynamic;
    private FieldValue fieldValue;

    protected MVELCompilationUnit compilationUnit;

    protected transient volatile ConditionEvaluator conditionEvaluator;
    private transient volatile Condition analyzedCondition;

    public MvelConstraint() {}

    public MvelConstraint(String packageName,
                          String expression,
                          MVELCompilationUnit compilationUnit,
                          IndexUtil.ConstraintType constraintType,
                          FieldValue fieldValue,
                          InternalReadAccessor extractor) {
        this.packageName = packageName;
        this.expression = expression;
        this.compilationUnit = compilationUnit;
        this.constraintType = constraintType;
        this.declarations = new Declaration[0];
        this.fieldValue = fieldValue;
        this.extractor = extractor;
    }

    public MvelConstraint(String packageName,
                          String expression,
                          Declaration[] declarations,
                          MVELCompilationUnit compilationUnit,
                          boolean isDynamic) {
        this.packageName = packageName;
        this.expression = expression;
        this.declarations = declarations;
        this.compilationUnit = compilationUnit;
        this.isDynamic = isDynamic;
    }

    public MvelConstraint(String packageName,
                          String expression,
                          Declaration[] declarations,
                          MVELCompilationUnit compilationUnit,
                          IndexUtil.ConstraintType constraintType,
                          Declaration indexingDeclaration,
                          InternalReadAccessor extractor,
                          boolean isUnification) {
        this.packageName = packageName;
        this.expression = expression;
        this.compilationUnit = compilationUnit;
        this.constraintType = indexingDeclaration != null ? constraintType : IndexUtil.ConstraintType.UNKNOWN;
        this.declarations = declarations == null ? new Declaration[0] : declarations;
        this.indexingDeclaration = indexingDeclaration;
        this.extractor = extractor;
        this.isUnification = isUnification;
    }

    protected String getAccessedClass() {
        return extractor instanceof ClassFieldReader ?
               ((ClassFieldReader)extractor).getClassName() :
               extractor instanceof MVELObjectClassFieldReader ?
                    ((MVELObjectClassFieldReader)extractor).getClassName() :
                    null;
    }

    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.extractor = readAccessor;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getExpression() {
        return expression;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public boolean isUnification() {
        return isUnification;
    }

    public void unsetUnification() {
        isUnification = false;
    }

    public boolean isIndexable(short nodeType) {
        return getConstraintType().isIndexableForNode(nodeType);
    }

    public IndexUtil.ConstraintType getConstraintType() {
        return constraintType;
    }

    public FieldValue getField() {
        return fieldValue;
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (isUnification) {
            throw new UnsupportedOperationException( "Should not be called" );
        }

        return evaluate(handle, workingMemory, null);
    }

    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        if (isUnification) {
            if (((UnificationContextEntry)context).getVariable() != null) {
                return true;
            }
            context = ((UnificationContextEntry)context).getContextEntry();
        }

        MvelContextEntry mvelContextEntry = (MvelContextEntry)context;
        return evaluate(handle, mvelContextEntry.workingMemory, mvelContextEntry.leftTuple);
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
        return evaluate(mvelContextEntry.rightHandle, mvelContextEntry.workingMemory, tuple);
    }

    protected boolean evaluate(InternalFactHandle handle, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        if (!jitted) {
            if (conditionEvaluator == null) {
                createMvelConditionEvaluator(workingMemory);
                if (TEST_JITTING && !isDynamic) { // Only for test purposes
                    boolean mvelValue = forceJitEvaluator(handle, workingMemory, leftTuple);
                }
            }

            if (!TEST_JITTING && !isDynamic && invocationCounter.getAndIncrement() == JIT_THRESOLD) {
                jitEvaluator(handle, workingMemory, leftTuple);
            }
        }
        return conditionEvaluator.evaluate(handle, workingMemory, leftTuple);
    }

    protected void createMvelConditionEvaluator(InternalWorkingMemory workingMemory) {
        if (compilationUnit != null) {
            MVELDialectRuntimeData data = getMVELDialectRuntimeData(workingMemory);
            ExecutableStatement statement = (ExecutableStatement)compilationUnit.getCompiledExpression(data);
            ParserConfiguration configuration = statement instanceof CompiledExpression ?
                    ((CompiledExpression)statement).getParserConfiguration() :
                    data.getParserConfiguration();
            conditionEvaluator = new MvelConditionEvaluator(compilationUnit, configuration, statement, declarations, getAccessedClass());
        } else {
            conditionEvaluator = new MvelConditionEvaluator(getParserConfiguration(workingMemory), expression, declarations, getAccessedClass());
        }
    }

    protected boolean forceJitEvaluator(InternalFactHandle handle, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        boolean mvelValue;
        try {
            mvelValue = conditionEvaluator.evaluate(handle, workingMemory, leftTuple);
        } catch (ClassCastException cce) {
            mvelValue = false;
        }
        jitEvaluator(handle, workingMemory, leftTuple);
        return mvelValue;
    }

    protected void jitEvaluator(InternalFactHandle handle, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        jitted = true;
        if (TEST_JITTING) {
            executeJitting(handle, workingMemory, leftTuple);
        } else {
            ExecutorHolder.executor.execute(new ConditionJitter(this, handle, workingMemory, leftTuple));
        }
    }

    private static class ConditionJitter implements Runnable {
        private MvelConstraint mvelConstraint;
        private InternalFactHandle rightHandle;
        private InternalWorkingMemory workingMemory;
        private LeftTuple leftTuple;

        private ConditionJitter(MvelConstraint mvelConstraint, InternalFactHandle rightHandle, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
            this.mvelConstraint = mvelConstraint;
            this.rightHandle = rightHandle;
            this.workingMemory = workingMemory;
            this.leftTuple = leftTuple;
        }

        public void run() {
            mvelConstraint.executeJitting(rightHandle, workingMemory, leftTuple);
            mvelConstraint = null;
            rightHandle = null;
            workingMemory = null;
            leftTuple = null;
        }
    }

    private static class ExecutorHolder {
        private static final Executor executor = ExecutorProviderFactory.getExecutorProvider().getExecutor();
    }

    private void executeJitting(InternalFactHandle handle, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        InternalRuleBase ruleBase = ((InternalRuleBase) workingMemory.getRuleBase());
        if ( MemoryUtil.permGenStats.isUsageThresholdExceeded(ruleBase.getConfiguration().getPermGenThreshold()) ) {
            return;
        }

        try {
            ClassLoader classLoader = ruleBase.getRootClassLoader();
            if (analyzedCondition == null) {
                analyzedCondition = ((MvelConditionEvaluator) conditionEvaluator).getAnalyzedCondition(handle, workingMemory, leftTuple);
            }
            conditionEvaluator = ASMConditionEvaluatorJitter.jitEvaluator(expression, analyzedCondition, declarations, classLoader, leftTuple);
        } catch (Throwable t) {
            if (TEST_JITTING) {
                if (analyzedCondition == null) {
                    logger.error("Unable to analize condition for expression: " + expression, t);
                } else {
                    throw new RuntimeException(t);
                }
            } else {
                t.printStackTrace();
                logger.warn( "Exception jitting: " + expression, t.getMessage() );
            }
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
        return analyzedCondition != null ?
                calculateMask(analyzedCondition, settableProperties) :
                calculateMaskFromExpression(settableProperties);
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

        if (propertyName.startsWith("is") || propertyName.startsWith("get")) {
            int exprPos = simpleExpression.indexOf(propertyName);
            int propNameEnd = exprPos + propertyName.length();
            if (simpleExpression.length() > propNameEnd + 2 && simpleExpression.charAt(propNameEnd) == '(') {
                propertyName = getter2property(propertyName);
            }
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
            if (method == null) {
                if (invocations.size() > 1) {
                    invocation = invocations.get(1);
                    if (invocation instanceof MethodInvocation) {
                        method = ((MethodInvocation)invocation).getMethod();
                    } else if (invocation instanceof FieldAccessInvocation) {
                        return ((FieldAccessInvocation)invocation).getField().getName();
                    }
                } else {
                    return null;
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
        out.writeObject(constraintType);
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
        constraintType = (IndexUtil.ConstraintType) in.readObject();
        isUnification = in.readBoolean();
        isDynamic = in.readBoolean();
        fieldValue = (FieldValue) in.readObject();
        compilationUnit = (MVELCompilationUnit) in.readObject();
    }

    public boolean isTemporal() {
        return false;
    }

    @Override
    public MvelConstraint cloneIfInUse() {
        MvelConstraint clone = (MvelConstraint)super.cloneIfInUse();
        if ( clone != this) {
            clone.conditionEvaluator = null;
        }
        return clone;
    }

    public MvelConstraint clone() {
        Declaration[] clonedDeclarations = new Declaration[declarations.length];
        System.arraycopy(declarations, 0, clonedDeclarations, 0, declarations.length);

        MvelConstraint clone = new MvelConstraint();
        clone.setType(getType());
        clone.packageName = packageName;
        clone.expression = expression;
        clone.constraintType = constraintType;
        clone.declarations = clonedDeclarations;
        clone.indexingDeclaration = indexingDeclaration;
        clone.extractor = extractor;
        clone.isUnification = isUnification;
        clone.isDynamic = isDynamic;
        clone.conditionEvaluator = conditionEvaluator;
        clone.compilationUnit = compilationUnit != null ? compilationUnit.clone() : null;
        return clone;
    }

    public int hashCode() {
        if (isAlphaHashable()) {
            return 29 * getLeftForEqualExpression().hashCode() + 31 * fieldValue.hashCode();
        }
        return expression.hashCode();
    }

    private String getLeftForEqualExpression() {
        return expression.substring(0, expression.indexOf("==")).trim();
    }

    private boolean isAlphaHashable() {
        return fieldValue != null && constraintType == IndexUtil.ConstraintType.EQUAL && getType() == ConstraintType.ALPHA;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || object.getClass() != MvelConstraint.class ) {
            return false;
        }
        MvelConstraint other = (MvelConstraint) object;
        if (isAlphaHashable()) {
            if ( !other.isAlphaHashable() ||
                    !getLeftForEqualExpression().equals(other.getLeftForEqualExpression()) ||
                    !fieldValue.equals(other.fieldValue) ) {
                return false;
            }
        } else {
            if (!expression.equals(other.expression)) {
                return false;
            }
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

    protected ParserConfiguration getParserConfiguration(InternalWorkingMemory workingMemory) {
        return getMVELDialectRuntimeData(workingMemory).getParserConfiguration();
    }

    protected MVELDialectRuntimeData getMVELDialectRuntimeData(InternalWorkingMemory workingMemory) {
        return ((MVELDialectRuntimeData)workingMemory.getRuleBase().getPackage(packageName).getDialectRuntimeRegistry().getDialectData( "mvel" ));
    }

    // MvelArrayContextEntry

    public static class MvelContextEntry implements ContextEntry {

        protected ContextEntry next;
        protected LeftTuple leftTuple;
        protected InternalFactHandle rightHandle;
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
            rightHandle = handle;
        }

        public void resetTuple() {
            leftTuple = null;
        }

        public void resetFactHandle() {
            workingMemory = null;
            rightHandle = null;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(leftTuple);
            out.writeObject(rightHandle);
            out.writeObject(declarations);
            out.writeObject(next);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            leftTuple = (LeftTuple)in.readObject();
            rightHandle = (InternalFactHandle) in.readObject();
            declarations = (Declaration[])in.readObject();
            next = (ContextEntry)in.readObject();
        }

        public LeftTuple getLeftTuple() {
            return leftTuple;
        }

        public InternalFactHandle getRight() {
            return rightHandle;
        }

        public Declaration[] getDeclarations() {
            return declarations;
        }

        public InternalWorkingMemory getWorkingMemory() {
            return workingMemory;
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
                return value2 != null && value1.equals(value2.toString());
            }
            return value1.equals( value2 );
        }
    }
}
