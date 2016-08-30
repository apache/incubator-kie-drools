/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.rule.constraint;

import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.base.extractors.MVELObjectClassFieldReader;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.PropertySpecificUtil;
import org.drools.core.reteoo.builder.BuildContext;
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
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.MemoryUtil;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.index.IndexUtil;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.concurrent.ExecutorProviderFactory;
import org.mvel2.ParserConfiguration;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.drools.core.reteoo.PropertySpecificUtil.*;
import static org.drools.core.util.ClassUtils.areNullSafeEquals;
import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.core.util.Drools.isJmxAvailable;
import static org.drools.core.util.StringUtils.*;

public class MvelConstraint extends MutableTypeConstraint implements IndexableConstraint, AcceptsReadAccessor {
    protected static final boolean TEST_JITTING = false;

    private static final Logger logger = LoggerFactory.getLogger(MvelConstraint.class);

    protected final transient AtomicInteger invocationCounter = new AtomicInteger(1);
    protected transient boolean jitted = false;

    private Set<String> packageNames;
    protected String expression;
    private IndexUtil.ConstraintType constraintType = IndexUtil.ConstraintType.UNKNOWN;
    private Declaration[] declarations;
    private EvaluatorWrapper[] operators;
    private Declaration indexingDeclaration;
    private InternalReadAccessor extractor;
    private boolean isUnification;
    protected boolean isDynamic;
    private FieldValue fieldValue;

    protected MVELCompilationUnit compilationUnit;

    private EvaluationContext evaluationContext = new EvaluationContext();

    protected transient volatile ConditionEvaluator conditionEvaluator;
    private transient volatile Condition analyzedCondition;

    private static final Declaration[] EMPTY_DECLARATIONS = new Declaration[0];
    private static final EvaluatorWrapper[] EMPTY_OPERATORS = new EvaluatorWrapper[0];

    public MvelConstraint() {}

    public MvelConstraint(final String packageName,
                          String expression,
                          MVELCompilationUnit compilationUnit,
                          IndexUtil.ConstraintType constraintType,
                          FieldValue fieldValue,
                          InternalReadAccessor extractor,
                          EvaluatorWrapper[] operators) {
        this.packageNames = new HashSet<String>() {{ add(packageName); }};
        this.expression = expression;
        this.compilationUnit = compilationUnit;
        this.constraintType = constraintType;
        this.declarations = EMPTY_DECLARATIONS;
        this.operators = operators == null ? EMPTY_OPERATORS : operators;
        this.fieldValue = fieldValue;
        this.extractor = extractor;
    }

    public MvelConstraint(final String packageName,
                          String expression,
                          Declaration[] declarations,
                          EvaluatorWrapper[] operators,
                          MVELCompilationUnit compilationUnit,
                          boolean isDynamic) {
        this.packageNames = new HashSet<String>() {{ add(packageName); }};
        this.expression = expression;
        this.declarations = declarations == null ? EMPTY_DECLARATIONS : declarations;
        this.operators = operators == null ? EMPTY_OPERATORS : operators;
        this.compilationUnit = compilationUnit;
        this.isDynamic = isDynamic;
    }

    public MvelConstraint(Collection<String> packageNames,
                          String expression,
                          Declaration[] declarations,
                          EvaluatorWrapper[] operators,
                          MVELCompilationUnit compilationUnit,
                          IndexUtil.ConstraintType constraintType,
                          Declaration indexingDeclaration,
                          InternalReadAccessor extractor,
                          boolean isUnification) {
        this.packageNames = new HashSet<String>(packageNames);
        this.expression = expression;
        this.compilationUnit = compilationUnit;
        this.constraintType = indexingDeclaration != null ? constraintType : IndexUtil.ConstraintType.UNKNOWN;
        this.declarations = declarations == null ? EMPTY_DECLARATIONS : declarations;
        this.operators = operators == null ? EMPTY_OPERATORS : operators;
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

    public Collection<String> getPackageNames() {
        return packageNames;
    }

    public void addPackageNames(Collection<String> otherPkgs) {
        packageNames.addAll(otherPkgs);
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

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory) {
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
        return evaluate(handle, mvelContextEntry.workingMemory, mvelContextEntry.tuple);
    }

    public boolean isAllowedCachedRight(Tuple tuple, ContextEntry context) {
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

    protected boolean evaluate(InternalFactHandle handle, InternalWorkingMemory workingMemory, Tuple tuple) {
        if (!jitted) {
            int jittingThreshold = TEST_JITTING ? 0 : workingMemory.getKnowledgeBase().getConfiguration().getJittingThreshold();
            if (conditionEvaluator == null) {
                createMvelConditionEvaluator(workingMemory);
                if (jittingThreshold == 0 && !isDynamic) { // Only for test purposes
                    forceJitEvaluator(handle, workingMemory, tuple);
                }
            }

            if (!TEST_JITTING && !isDynamic && invocationCounter.getAndIncrement() == jittingThreshold) {
                jitEvaluator(handle, workingMemory, tuple);
            }
        }
        try {
            return conditionEvaluator.evaluate( handle, workingMemory, tuple );
        } catch (Exception e) {
            throw new RuntimeException( "Error evaluating constraint '" + expression + "' in " + evaluationContext, e );
        }
    }

    protected void createMvelConditionEvaluator(InternalWorkingMemory workingMemory) {
        if (compilationUnit != null) {
            MVELDialectRuntimeData data = getMVELDialectRuntimeData(workingMemory);
            ExecutableStatement statement = (ExecutableStatement)compilationUnit.getCompiledExpression(data, evaluationContext);
            ParserConfiguration configuration = statement instanceof CompiledExpression ?
                    ((CompiledExpression)statement).getParserConfiguration() :
                    data.getParserConfiguration();
            conditionEvaluator = new MvelConditionEvaluator(compilationUnit, configuration, statement, declarations, operators, getAccessedClass());
        } else {
            conditionEvaluator = new MvelConditionEvaluator(getParserConfiguration(workingMemory), expression, declarations, operators, getAccessedClass());
        }
    }

    protected boolean forceJitEvaluator(InternalFactHandle handle, InternalWorkingMemory workingMemory, Tuple tuple) {
        boolean mvelValue;
        try {
            mvelValue = conditionEvaluator.evaluate(handle, workingMemory, tuple);
        } catch (ClassCastException cce) {
            mvelValue = false;
        }
        jitEvaluator(handle, workingMemory, tuple);
        return mvelValue;
    }

    protected void jitEvaluator(InternalFactHandle handle, InternalWorkingMemory workingMemory, Tuple tuple) {
        jitted = true;
        if (TEST_JITTING) {
            executeJitting(handle, workingMemory, tuple);
        } else {
            ExecutorHolder.executor.execute(new ConditionJitter(this, handle, workingMemory, tuple));
        }
    }

    private static class ConditionJitter implements Runnable {
        private MvelConstraint mvelConstraint;
        private InternalFactHandle rightHandle;
        private InternalWorkingMemory workingMemory;
        private Tuple tuple;

        private ConditionJitter(MvelConstraint mvelConstraint, InternalFactHandle rightHandle, InternalWorkingMemory workingMemory, Tuple tuple) {
            this.mvelConstraint = mvelConstraint;
            this.rightHandle = rightHandle;
            this.workingMemory = workingMemory;
            this.tuple = tuple;
        }

        public void run() {
            mvelConstraint.executeJitting(rightHandle, workingMemory, tuple);
            mvelConstraint = null;
            rightHandle = null;
            workingMemory = null;
            tuple = null;
        }
    }

    private static class ExecutorHolder {
        private static final Executor executor = ExecutorProviderFactory.getExecutorProvider().getExecutor();
    }

    private void executeJitting(InternalFactHandle handle, InternalWorkingMemory workingMemory, Tuple tuple) {
        InternalKnowledgeBase kBase = workingMemory.getKnowledgeBase();
        if ( !isJmxAvailable() && MemoryUtil.permGenStats.isUsageThresholdExceeded(kBase.getConfiguration().getPermGenThreshold()) ) {
            return;
        }

        try {
            if (analyzedCondition == null) {
                analyzedCondition = ((MvelConditionEvaluator) conditionEvaluator).getAnalyzedCondition(handle, workingMemory, tuple);
            }
            conditionEvaluator = ASMConditionEvaluatorJitter.jitEvaluator(expression, analyzedCondition, declarations, operators, kBase.getRootClassLoader(), tuple);
        } catch (Throwable t) {
            if (TEST_JITTING) {
                if (analyzedCondition == null) {
                    logger.error("Unable to analize condition for expression: " + expression, t);
                } else {
                    throw new RuntimeException("Unable to analize condition for expression: " + expression, t);
                }
            } else {
                logger.warn( "Exception jitting: " + expression +
                             " This is NOT an error and NOT prevent the correct execution since the constraint will be evaluated in intrepreted mode" );
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

    public EvaluatorWrapper[] getOperators() {
        return operators;
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

    public BitMask getListenedPropertyMask(List<String> settableProperties) {
        return analyzedCondition != null ?
                calculateMask(analyzedCondition, settableProperties) :
                calculateMaskFromExpression(settableProperties);
    }

    private BitMask calculateMaskFromExpression(List<String> settableProperties) {
        BitMask mask = getEmptyPropertyReactiveMask(settableProperties.size());
        String[] simpleExpressions = expression.split("\\Q&&\\E|\\Q||\\E");

        for (String simpleExpression : simpleExpressions) {
            String propertyName = getPropertyNameFromSimpleExpression(simpleExpression);
            if (propertyName.length() == 0) {
                continue;
            }
            if (propertyName.equals("this")) {
                return allSetButTraitBitMask();
            }
            int pos = settableProperties.indexOf(propertyName);
            if (pos < 0 && Character.isUpperCase(propertyName.charAt(0))) {
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                pos = settableProperties.indexOf(propertyName);
            }
            if (pos >= 0) { // Ignore not settable properties
                mask = mask.set(pos + PropertySpecificUtil.CUSTOM_BITS_OFFSET);
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

    private BitMask calculateMask(Condition condition, List<String> settableProperties) {
        BitMask mask = getEmptyPropertyReactiveMask(settableProperties.size());
        for (Condition c : ((CombinedCondition)condition).getConditions()) {
            String propertyName = getFirstInvokedPropertyName(((SingleCondition) c).getLeft());
            if (propertyName != null) {
                mask = setPropertyOnMask(mask, settableProperties, propertyName);
            }
        }
        return mask;
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
            return method != null ? getter2property(method.getName()) : null;
        }

        if (invocation instanceof FieldAccessInvocation) {
            return ((FieldAccessInvocation)invocation).getField().getName();
        }

        return null;
    }

    // Externalizable

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(packageNames);
        out.writeObject(expression);
        out.writeObject(declarations);
        out.writeObject(indexingDeclaration);
        out.writeObject(extractor);
        out.writeObject(constraintType);
        out.writeBoolean(isUnification);
        out.writeBoolean(isDynamic);
        out.writeObject(fieldValue);
        out.writeObject(compilationUnit);
        out.writeObject(evaluationContext);
        out.writeObject(operators);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        packageNames = (Set<String>)in.readObject();
        expression = (String)in.readObject();
        declarations = (Declaration[]) in.readObject();
        indexingDeclaration = (Declaration) in.readObject();
        extractor = (InternalReadAccessor) in.readObject();
        constraintType = (IndexUtil.ConstraintType) in.readObject();
        isUnification = in.readBoolean();
        isDynamic = in.readBoolean();
        fieldValue = (FieldValue) in.readObject();
        compilationUnit = (MVELCompilationUnit) in.readObject();
        evaluationContext = (EvaluationContext) in.readObject();
        operators = (EvaluatorWrapper[]) in.readObject();
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
        clone.packageNames = packageNames;
        clone.expression = expression;
        clone.fieldValue = fieldValue;
        clone.constraintType = constraintType;
        clone.declarations = clonedDeclarations;
        clone.operators = operators;
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
            if (!equalsIgnoreSpaces( expression, other.expression )) {
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

    public boolean equals(Object object, InternalKnowledgeBase kbase) {
        if ( !equals( object ) ) {
            return false;
        }
        String thisPkg = packageNames.iterator().next();
        String otherPkg = ((MvelConstraint) object).packageNames.iterator().next();
        if (thisPkg.equals( otherPkg )) {
            return true;
        }

        Map<String, Object> thisImports = ((MVELDialectRuntimeData) kbase.getPackage( thisPkg ).getDialectRuntimeRegistry().getDialectData("mvel")).getImports();
        Map<String, Object> otherImports = ((MVELDialectRuntimeData) kbase.getPackage( otherPkg ).getDialectRuntimeRegistry().getDialectData("mvel")).getImports();

        for (String token : splitExpression(expression)) {
            if ( !areNullSafeEquals(thisImports.get(token), otherImports.get(token)) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Splits the expression in token (words) ignoring everything that is between quotes
     */
    private static List<String> splitExpression(String expression) {
        List<String> tokens = new ArrayList<String>();
        int lastStart = -1;
        boolean isQuoted = false;
        for (int i = 0; i < expression.length(); i++) {
            if ( lastStart == -1 ) {
                if ( !isQuoted && Character.isJavaIdentifierStart( expression.charAt(i) ) ) {
                    lastStart = i;
                }
            } else if ( !Character.isJavaIdentifierPart( expression.charAt(i) ) ) {
                tokens.add(expression.subSequence(lastStart, i).toString());
                lastStart = -1;
            }
            if (expression.charAt(i) == '"' || expression.charAt(i) == '\'') {
                if (i == 0 || expression.charAt(i-1) != '\\') {
                    isQuoted = !isQuoted;
                }
                if (isQuoted) {
                    lastStart = -1;
                }
            }
        }
        if (lastStart != -1) {
            tokens.add( expression.subSequence( lastStart, expression.length() ).toString() );
        }
        return tokens;
    }

    @Override
    public String toString() {
        return expression;
    }

    protected ParserConfiguration getParserConfiguration(InternalWorkingMemory workingMemory) {
        return getMVELDialectRuntimeData(workingMemory).getParserConfiguration();
    }

    protected MVELDialectRuntimeData getMVELDialectRuntimeData(InternalWorkingMemory workingMemory) {
        return getMVELDialectRuntimeData(workingMemory.getKnowledgeBase());
    }

    protected MVELDialectRuntimeData getMVELDialectRuntimeData(InternalKnowledgeBase kbase) {
        for (String packageName : packageNames) {
            InternalKnowledgePackage pkg = kbase.getPackage( packageName );
            if (pkg != null) {
                return ((MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel"));
            }
        }
        return null;
    }

    // MvelArrayContextEntry

    public static class MvelContextEntry implements ContextEntry {

        protected ContextEntry next;
        protected Tuple tuple;
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

        public void updateFromTuple(InternalWorkingMemory workingMemory, Tuple tuple) {
            this.tuple = tuple;
            this.workingMemory = workingMemory;
        }

        public void updateFromFactHandle(InternalWorkingMemory workingMemory, InternalFactHandle handle) {
            this.workingMemory = workingMemory;
            rightHandle = handle;
        }

        public void resetTuple() {
            tuple = null;
        }

        public void resetFactHandle() {
            workingMemory = null;
            rightHandle = null;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(tuple);
            out.writeObject(rightHandle);
            out.writeObject(declarations);
            out.writeObject(next);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            tuple = (Tuple)in.readObject();
            rightHandle = (InternalFactHandle) in.readObject();
            declarations = (Declaration[])in.readObject();
            next = (ContextEntry)in.readObject();
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
                                    Tuple tuple) {
            DroolsQuery query = ( DroolsQuery ) tuple.getObject( 0 );
            this.variable = query.getVariables()[ this.reader.getIndex() ];
            if ( this.variable == null ) {
                // if there is no Variable, handle it as a normal constraint
                this.contextEntry.updateFromTuple( workingMemory, tuple );
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

    public void registerEvaluationContext(BuildContext buildContext) {
        evaluationContext.addContext(buildContext);
    }

    public static class EvaluationContext implements Externalizable {

        private Collection<String> evaluatedRules = new HashSet<String>();

        public void addContext(BuildContext buildContext) {
            evaluatedRules.add(buildContext.getRule().toRuleNameAndPathString());
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(evaluatedRules);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            evaluatedRules = (Collection<String>)in.readObject();
        }

        @Override
        public String toString() {
            return evaluatedRules.toString();
        }
    }
}
