/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.base.RuleBase;
import org.drools.base.RuleBuildContext;
import org.drools.base.base.DroolsQuery;
import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueResolver;
import org.drools.base.common.DroolsObjectInputStream;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.reteoo.PropertySpecificUtil;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.MutableTypeConstraint;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.AcceptsReadAccessor;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.accessor.RightTupleValueExtractor;
import org.drools.base.rule.accessor.TupleValueExtractor;
import org.drools.base.rule.constraint.Constraint;
import org.drools.base.util.IndexedValueReader;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.compiler.rule.builder.EvaluatorWrapper;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.mvel.ConditionAnalyzer.CombinedCondition;
import org.drools.mvel.ConditionAnalyzer.Condition;
import org.drools.mvel.ConditionAnalyzer.EvaluatedExpression;
import org.drools.mvel.ConditionAnalyzer.Expression;
import org.drools.mvel.ConditionAnalyzer.FieldAccessInvocation;
import org.drools.mvel.ConditionAnalyzer.Invocation;
import org.drools.mvel.ConditionAnalyzer.MethodInvocation;
import org.drools.mvel.ConditionAnalyzer.SingleCondition;
import org.drools.mvel.accessors.ClassFieldReader;
import org.drools.mvel.expr.MVELCompilationUnit;
import org.drools.mvel.extractors.MVELObjectClassFieldReader;
import org.drools.util.bitmask.BitMask;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.concurrent.ExecutorProviderFactory;
import org.kie.internal.conf.ConstraintJittingThresholdOption;
import org.mvel2.ParserConfiguration;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.base.reteoo.PropertySpecificUtil.allSetBitMask;
import static org.drools.base.reteoo.PropertySpecificUtil.allSetButTraitBitMask;
import static org.drools.base.reteoo.PropertySpecificUtil.getEmptyPropertyReactiveMask;
import static org.drools.base.reteoo.PropertySpecificUtil.setPropertyOnMask;
import static org.drools.base.util.Drools.isJmxAvailable;
import static org.drools.core.util.MessageUtils.defaultToEmptyString;
import static org.drools.util.ClassUtils.getter2property;
import static org.drools.util.StringUtils.codeAwareIndexOf;
import static org.drools.util.StringUtils.equalsIgnoreSpaces;
import static org.drools.util.StringUtils.extractFirstIdentifier;
import static org.drools.util.StringUtils.lookAheadIgnoringSpaces;
import static org.drools.util.StringUtils.skipBlanks;

public class MVELConstraint extends MutableTypeConstraint<ContextEntry> implements IndexableConstraint, AcceptsReadAccessor {
    protected static final boolean TEST_JITTING = false;

    private static final Logger logger = LoggerFactory.getLogger(MVELConstraint.class);

    protected final transient AtomicInteger invocationCounter = new AtomicInteger(1);
    protected transient volatile boolean jitted = false;

    private Set<String> packageNames;
    protected String expression;
    private ConstraintTypeOperator constraintType = ConstraintTypeOperator.UNKNOWN;
    private Declaration[] declarations;
    private EvaluatorWrapper[]  operators;
    private TupleValueExtractor leftIndexingDeclaration;

    private TupleValueExtractor rightIndexingDeclaration;
    private ReadAccessor        extractor;


    private boolean isUnification;
    protected boolean isDynamic;
    private FieldValue fieldValue;

    protected MVELCompilationUnit compilationUnit;

    private EvaluationContext evaluationContext = new EvaluationContext();

    protected transient volatile ConditionEvaluator conditionEvaluator;
    private transient volatile Condition analyzedCondition;

    private static final Declaration[] EMPTY_DECLARATIONS = new Declaration[0];
    private static final EvaluatorWrapper[] EMPTY_OPERATORS = new EvaluatorWrapper[0];

    public MVELConstraint() {}

    public MVELConstraint(final String packageName,
                          String expression,
                          MVELCompilationUnit compilationUnit,
                          ConstraintTypeOperator constraintType,
                          FieldValue fieldValue,
                          ReadAccessor extractor,
                          EvaluatorWrapper[] operators) {
        this.packageNames = new LinkedHashSet<>();
        this.packageNames.add(packageName);
        this.expression = expression;
        this.compilationUnit = compilationUnit;
        this.constraintType = constraintType;
        this.declarations = EMPTY_DECLARATIONS;
        this.operators = operators == null ? EMPTY_OPERATORS : operators;
        this.fieldValue = fieldValue;
        this.extractor = extractor;
    }

    public MVELConstraint(final String packageName,
                          String expression,
                          Declaration[] declarations,
                          EvaluatorWrapper[] operators,
                          MVELCompilationUnit compilationUnit,
                          boolean isDynamic) {
        this.packageNames = new LinkedHashSet<>();
        this.packageNames.add(packageName);
        this.expression = expression;
        this.declarations = declarations == null ? EMPTY_DECLARATIONS : declarations;
        this.operators = operators == null ? EMPTY_OPERATORS : operators;
        this.compilationUnit = compilationUnit;
        this.isDynamic = isDynamic;
    }

    public MVELConstraint(Collection<String> packageNames,
                          String expression,
                          Declaration[] declarations,
                          EvaluatorWrapper[] operators,
                          MVELCompilationUnit compilationUnit,
                          ConstraintTypeOperator constraintType,
                          TupleValueExtractor leftIndexingDeclaration,
                          ReadAccessor extractor,
                          boolean isUnification) {
        this.packageNames = new LinkedHashSet<>(packageNames);
        this.expression = expression;
        this.compilationUnit         = compilationUnit;
        this.constraintType          = leftIndexingDeclaration != null ? constraintType : ConstraintTypeOperator.UNKNOWN;
        this.declarations            = declarations == null ? EMPTY_DECLARATIONS : declarations;
        this.operators               = operators == null ? EMPTY_OPERATORS : operators;
        this.leftIndexingDeclaration = leftIndexingDeclaration;
        this.extractor               = extractor;
        this.isUnification = isUnification;
    }

    protected String getAccessedClass() {
        return extractor instanceof ClassFieldReader ?
                ((ClassFieldReader) extractor).getClassName() :
                extractor instanceof MVELObjectClassFieldReader ?
                        ((MVELObjectClassFieldReader) extractor).getClassName() :
                        null;
    }

    public void setReadAccessor(ReadAccessor readAccessor) {
        this.extractor = readAccessor;
    }

    @Override
    public Collection<String> getPackageNames() {
        return packageNames;
    }

    @Override
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

    @Override
    public void unsetUnification() {
        isUnification = false;
    }

    public boolean isIndexable(int nodeType, KieBaseConfiguration config) {
        return getConstraintType().isIndexableForNode(nodeType, this, config);
    }

    public ConstraintTypeOperator getConstraintType() {
        return constraintType;
    }

    public FieldValue getField() {
        return fieldValue;
    }

    public boolean isAllowed(FactHandle handle, ValueResolver valueResolver) {
        if (isUnification) {
            throw new UnsupportedOperationException("Should not be called");
        }

        return evaluate(handle, valueResolver, null);
    }

    public boolean isAllowedCachedLeft(ContextEntry context, FactHandle handle) {
        if (isUnification) {
            if (((UnificationContextEntry) context).getVariable() != null) {
                return true;
            }
            context = ((UnificationContextEntry) context).getContextEntry();
        }

        MvelContextEntry mvelContextEntry = (MvelContextEntry) context;
        return evaluate(handle, mvelContextEntry.valueResolver, mvelContextEntry.tuple);
    }

    public boolean isAllowedCachedRight(BaseTuple tuple, ContextEntry context) {
        if (isUnification) {
            DroolsQuery query = (DroolsQuery) tuple.get(0).getObject();
            Variable v = query.getVariables()[((UnificationContextEntry) context).getReader().getIndex()];

            if (v != null) {
                return true;
            }
            context = ((UnificationContextEntry) context).getContextEntry();
        }

        MvelContextEntry mvelContextEntry = (MvelContextEntry) context;
        return evaluate(mvelContextEntry.rightHandle, mvelContextEntry.valueResolver, tuple);
    }

    protected boolean evaluate(FactHandle handle, ValueResolver valueResolver, BaseTuple tuple) {
        if (!jitted) {
            int jittingThreshold = TEST_JITTING ? 0 : valueResolver.getRuleBase().getConfiguration().getOption(ConstraintJittingThresholdOption.KEY).getThreshold();
            if (conditionEvaluator == null) {
                if (jittingThreshold == 0 && !isDynamic) { // Only for test purposes or when jitting is enforced at first evaluation
                    synchronized (this) {
                        if (conditionEvaluator == null) {
                            conditionEvaluator = forceJitEvaluator(handle, valueResolver, tuple);
                        }
                    }
                } else {
                    conditionEvaluator = createMvelConditionEvaluator(valueResolver);
                }
            }

            if (jittingThreshold != 0 && !isDynamic && invocationCounter.getAndIncrement() == jittingThreshold) {
                jitEvaluator(handle, valueResolver, tuple);
            }
        }
        try {
            return conditionEvaluator.evaluate(handle, valueResolver, tuple);
        } catch (Exception e) {
            throw new ConstraintEvaluationException(expression, evaluationContext, e);
        }
    }

    protected ConditionEvaluator createMvelConditionEvaluator(ValueResolver valueResolver) {
        if (compilationUnit != null) {
            MVELDialectRuntimeData data = getMVELDialectRuntimeData(valueResolver);
            ExecutableStatement statement = (ExecutableStatement)compilationUnit.getCompiledExpression(data, evaluationContext);
            ParserConfiguration configuration = statement instanceof CompiledExpression ?
                    ((CompiledExpression) statement).getParserConfiguration() :
                    data.getParserConfiguration();
            return new MVELConditionEvaluator(compilationUnit, configuration, statement, declarations, operators, getAccessedClass());
        } else {
            return new MVELConditionEvaluator(getParserConfiguration(valueResolver), expression, declarations, operators, getAccessedClass());
        }
    }

    protected ConditionEvaluator forceJitEvaluator(FactHandle handle, ValueResolver valueResolver, BaseTuple tuple) {
        ConditionEvaluator mvelEvaluator = createMvelConditionEvaluator(valueResolver);
        try {
            mvelEvaluator.evaluate(handle, valueResolver, tuple);
        } catch (ClassCastException cce) {
        } catch (Exception e) {
            return createMvelConditionEvaluator(valueResolver);
        }
        return executeJitting(handle, valueResolver, tuple, mvelEvaluator);
    }

    protected void jitEvaluator(FactHandle handle, ValueResolver valueResolver, BaseTuple tuple) {
        jitted = true;
        ExecutorHolder.executor.execute(new ConditionJitter(this, handle, valueResolver, tuple));
    }

    private static class ConditionJitter implements Runnable {
        private MVELConstraint mvelConstraint;
        private FactHandle rightHandle;
        private ValueResolver valueResolver;
        private BaseTuple tuple;

        private ConditionJitter( MVELConstraint mvelConstraint, FactHandle rightHandle, ValueResolver valueResolver, BaseTuple tuple) {
            this.mvelConstraint = mvelConstraint;
            this.rightHandle = rightHandle;
            this.valueResolver = valueResolver;
            this.tuple = tuple;
        }

        public void run() {
            mvelConstraint.conditionEvaluator = mvelConstraint.executeJitting(rightHandle, valueResolver, tuple, mvelConstraint.conditionEvaluator);
            mvelConstraint = null;
            rightHandle = null;
            valueResolver = null;
            tuple = null;
        }
    }

    private static class ExecutorHolder {
        private static final Executor executor = ExecutorProviderFactory.getExecutorProvider().getExecutor();
    }

    private ConditionEvaluator executeJitting(FactHandle handle, ValueResolver valueResolver, BaseTuple tuple, ConditionEvaluator mvelEvaluator) {
        RuleBase kBase = valueResolver.getRuleBase();

        if (!isJmxAvailable()) { // @TODO have Mario check this (mdp feb2023)
            return mvelEvaluator;
        }

        try {
            if (analyzedCondition == null) {
                analyzedCondition = (( MVELConditionEvaluator ) mvelEvaluator).getAnalyzedCondition(handle, valueResolver, tuple);
            }
            ClassLoader jitClassLoader = kBase.getRootClassLoader() instanceof ProjectClassLoader ?
                    ((ProjectClassLoader) kBase.getRootClassLoader()).getTypesClassLoader() :
                    kBase.getRootClassLoader();
            return ASMConditionEvaluatorJitter.jitEvaluator(expression, analyzedCondition, declarations, operators, jitClassLoader, tuple);
        } catch (Throwable t) {
            if (TEST_JITTING) {
                if (analyzedCondition == null) {
                    logger.error("Unable to analize condition for expression: " + expression, t);
                } else {
                    throw new RuntimeException("Unable to analize condition for expression: " + expression, t);
                }
            } else {
                logger.warn("Exception jitting: {}." +
                             " This is NOT an error and NOT prevent the correct execution since the constraint will be evaluated in intrepreted mode",
                            expression);
            }
        }
        return mvelEvaluator;
    }

    public ContextEntry createContext() {
        if (declarations.length == 0) return null;
        ContextEntry contextEntry = new MvelContextEntry(declarations);
        if (isUnification) {
            contextEntry = new UnificationContextEntry(contextEntry, declarations[0]);
        }
        return contextEntry;
    }



    public IndexedValueReader getFieldIndex() {
        return new IndexedValueReader(leftIndexingDeclaration, getRightIndexExtractor());
    }

    public ReadAccessor getFieldExtractor() {
        return extractor;
    }

    @Override
    public TupleValueExtractor getRightIndexExtractor() {
        if (rightIndexingDeclaration == null) {
            rightIndexingDeclaration = new RightTupleValueExtractor(extractor);
        }
        return rightIndexingDeclaration;
    }

    @Override
    public TupleValueExtractor getLeftIndexExtractor() {
        return leftIndexingDeclaration;
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

                if (leftIndexingDeclaration != null && i == 0) {
                    // indexed MVELConstraints currently only have a single required declaration.
                    // So this is a hack that works for this limited scenario.
                    // It needs to clone first, due to unification otherwise you
                    // might change the pattern incorrectly for other nodes.
                    if (!leftIndexingDeclaration.equals(oldDecl)) {
                        // This is true for synthetic declarations
                        leftIndexingDeclaration = leftIndexingDeclaration.clone();
                        ((Declaration) leftIndexingDeclaration).setPattern(newDecl.getPattern());
                    } else {
                        leftIndexingDeclaration = newDecl;
                    }
                }
                break;
            }
        }
    }

    // Slot specific

    @Override
    public BitMask getListenedPropertyMask(Optional<Pattern> pattern, ObjectType modifiedType, List<String> settableProperties) {
        return analyzedCondition != null ?
                calculateMask(modifiedType, settableProperties) :
                calculateMaskFromExpression(pattern, settableProperties);
    }

    /*
     * if pattern is empty, bind variables are considered to be declared in the same pattern. It should be fine for alpha constraints
     */
    private BitMask calculateMaskFromExpression(Optional<Pattern> pattern, List<String> settableProperties) {
        BitMask mask = getEmptyPropertyReactiveMask(settableProperties.size());
        String[] simpleExpressions = expression.split("\\Q&&\\E|\\Q||\\E");

        for (String simpleExpression : simpleExpressions) {
            List<String> properties = getPropertyNamesFromSimpleExpression(simpleExpression);
            if (properties.isEmpty()) {
                return allSetBitMask();
            }
            boolean firstProp = true;
            for (String propertyName : properties) {
                String originalPropertyName = propertyName;
                if (firstProp && pattern.map(p -> p.getDeclaration() != null && originalPropertyName.equals(p.getDeclaration().getBindingName())).orElse(false)) {
                    // drop first property part if it is (redundantly) equal to the declared pattern binding name
                    continue;
                }
                if (propertyName == null || propertyName.equals("this") || propertyName.length() == 0) {
                    return allSetButTraitBitMask();
                }
                int pos = settableProperties.indexOf(propertyName);
                if (pos < 0) {
                    if (Character.isUpperCase(propertyName.charAt(0))) {
                        propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                        pos = settableProperties.indexOf(propertyName);
                    } else {
                        propertyName = findBoundVariable(propertyName, pattern);
                        if (propertyName != null) {
                            pos = settableProperties.indexOf(propertyName);
                        }
                    }
                }
                if (pos >= 0) {
                    mask = mask.set(pos + PropertySpecificUtil.CUSTOM_BITS_OFFSET);
                } else {
                    // if it is not able to find the property name it could be a function invocation so property reactivity shouldn't filter anything
                    if (firstProp) {
                        if (simpleExpression.indexOf(" contains ") > simpleExpression.indexOf(originalPropertyName)) {
                            // A contains expression could be a rewritten memberOf so give a change also to the part of expression
                            // after the 'contains' keyword to be analyzed before emitting a warning and consider the constraint as class reactive
                            continue;
                        }
                        if (isBoundVariableFromDifferentPattern(originalPropertyName, pattern)) {
                            logger.warn("{} is not relevant to this pattern, so it causes class reactivity." +
                                        " Consider placing this constraint in the original pattern if possible : {}",
                                        originalPropertyName, simpleExpression);
                        }
                        return allSetBitMask();
                    }
                }
                firstProp = false;
            }
        }

        return mask;
    }

    private String findBoundVariable(String variable, Optional<Pattern> pattern) {
        for (Declaration declaration : declarations) {
            if (declaration.getIdentifier().equals(variable) && (!pattern.isPresent() || declaration.getPattern().equals(pattern.get()))) { // if pattern is not given, assume it's the same pattern
                ReadAccessor accessor = declaration.getExtractor();
                if (accessor instanceof ClassFieldReader) {
                    return ((ClassFieldReader) accessor).getFieldName();
                }
            }
        }
        return null;
    }

    private boolean isBoundVariableFromDifferentPattern(String variable, Optional<Pattern> pattern) {
        if (!pattern.isPresent()) {
            return false;
        }
        for (Declaration declaration : declarations) {
            if (declaration.getIdentifier().equals(variable) && !declaration.getPattern().equals(pattern.get())) {
                return true;
            }
        }
        return false;
    }

    private List<String> getPropertyNamesFromSimpleExpression(String expression) {
        List<String> names = new ArrayList<>();
        for (int cursor = 0; cursor < expression.length(); cursor = nextPropertyName(expression, names, cursor));
        return names;
    }

    private int nextPropertyName(String expression, List<String> names, int cursor) {
        StringBuilder propertyNameBuilder = new StringBuilder();
        cursor = extractFirstIdentifier(expression, propertyNameBuilder, cursor);
        if (propertyNameBuilder.length() == 0) {
            return cursor;
        }

        boolean isAccessor = false;
        String propertyName = propertyNameBuilder.toString();
        if (propertyName.equals("this")) {
            cursor = skipBlanks(expression, cursor);
            if (cursor >= expression.length() || expression.charAt(cursor) != '.') {
                names.add("this");
                return cursor;
            }
            propertyNameBuilder = new StringBuilder();
            extractFirstIdentifier(expression, propertyNameBuilder, cursor);
            propertyName = propertyNameBuilder.toString();
        } else if (propertyName.equals("null") || propertyName.equals("true") || propertyName.equals("false")) {
            propertyNameBuilder = new StringBuilder();
            extractFirstIdentifier(expression, propertyNameBuilder, cursor);
            propertyName = propertyNameBuilder.toString();
        }

        if (propertyName.startsWith("is") || propertyName.startsWith("get")) {
            int exprPos = expression.indexOf(propertyName);
            int propNameEnd = exprPos + propertyName.length();
            if (expression.length() > propNameEnd + 1 && expression.charAt(propNameEnd) == '(') {
                int argsEnd = expression.indexOf(')', propNameEnd);
                // the getter has to be used for property reactivity only if it's a true getter (doesn't have any argument)
                if (expression.substring(propNameEnd + 1, argsEnd).trim().isEmpty()) {
                    propertyName = getter2property(propertyName);
                    isAccessor = true;
                }
            }
        }

        if (!isAccessor) {
            Character lookAhead = lookAheadIgnoringSpaces(expression, cursor);
            boolean isMethodInvocation = lookAhead != null && lookAhead.equals('(');
            if (isMethodInvocation) {
                return nextPropertyName(expression, names, cursor);
            }
        }

        if (propertyName != null && propertyName.length() > 0) {
            names.add(propertyName);
        }
        return skipOperator(expression, cursor);
    }

    private int skipOperator(String expression, int cursor) {
        if (cursor < expression.length() && expression.charAt(cursor) == '.') {
            while (cursor < expression.length() && Character.isWhitespace(expression.charAt(++cursor)));
        }

        boolean namedOperator = false;
        int i = cursor;
        for (; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (Character.isJavaIdentifierStart(ch)) {
                namedOperator = true;
            } else if (Character.isWhitespace(ch)) {
                if (namedOperator) {
                    return i + 1;
                }
            } else if (!Character.isJavaIdentifierPart(ch)) {
                return i + 1;
            }
        }
        return i;
    }

    private BitMask calculateMask(ObjectType modifiedType, List<String> settableProperties) {
        BitMask mask = getEmptyPropertyReactiveMask(settableProperties.size());
        if (analyzedCondition instanceof SingleCondition) {
            mask = setPropertyOnReactiveMask( modifiedType, settableProperties, mask, ( SingleCondition ) analyzedCondition );
        } else {
            for (Condition c : ((CombinedCondition) analyzedCondition).getConditions()) {
                mask = setPropertyOnReactiveMask(modifiedType, settableProperties, mask, (SingleCondition) c);
            }
        }
        return mask;
    }

    private BitMask setPropertyOnReactiveMask( ObjectType modifiedType, List<String> settableProperties, BitMask mask, SingleCondition c ) {
        String propertyName = getFirstInvokedPropertyName(modifiedType, c.getLeft());
        return propertyName != null ?
                setPropertyOnMask(modifiedType.getClassName(), mask, settableProperties, propertyName) :
                allSetBitMask();
    }

    private String getFirstInvokedPropertyName(ObjectType modifiedType, Expression expression) {
        if (!(expression instanceof EvaluatedExpression)) {
            return null;
        }
        List<Invocation> invocations = ((EvaluatedExpression) expression).invocations;
        Invocation invocation = invocations.get(0);

        if (invocation instanceof MethodInvocation) {
            Method method = ((MethodInvocation) invocation).getMethod();
            if (method == null) {
                if (invocations.size() > 1) {
                    invocation = invocations.get(1);
                    if (invocation instanceof MethodInvocation) {
                        method = ((MethodInvocation) invocation).getMethod();
                    } else if (invocation instanceof FieldAccessInvocation) {
                        return ((FieldAccessInvocation) invocation).getField().getName();
                    }
                } else {
                    return null;
                }
            }
            return method != null && !Modifier.isStatic(method.getModifiers()) && modifiedType.isAssignableTo(method.getDeclaringClass())
                    ? getter2property(method.getName()) : null;
        }

        if (invocation instanceof FieldAccessInvocation) {
            return ((FieldAccessInvocation) invocation).getField().getName();
        }

        return null;
    }

    // Externalizable

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(packageNames);
        out.writeObject(expression);

        if (extractor instanceof ClassFieldReader) {
            out.writeObject(((ClassFieldReader) extractor).getAccessorKey());
        } else {
            out.writeObject(extractor);
        }

        out.writeObject(leftIndexingDeclaration);
        out.writeObject(declarations);
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
        packageNames = (Set<String>) in.readObject();
        expression = (String) in.readObject();

        if (in instanceof DroolsObjectInputStream) {
            ((DroolsObjectInputStream) in).readExtractor(this::setReadAccessor);
        } else {
            extractor = (ReadAccessor) in.readObject();
        }

        leftIndexingDeclaration = (Declaration) in.readObject();
        declarations            = (Declaration[]) in.readObject();
        constraintType = (ConstraintTypeOperator) in.readObject();
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
    public MVELConstraint cloneIfInUse() {
        MVELConstraint clone = (MVELConstraint) super.cloneIfInUse();
        if (clone != this) {
            clone.conditionEvaluator = null;
        }
        return clone;
    }

    public MVELConstraint clone() {
        Declaration[] clonedDeclarations = new Declaration[declarations.length];
        System.arraycopy(declarations, 0, clonedDeclarations, 0, declarations.length);

        MVELConstraint clone = new MVELConstraint();
        clone.setType(getType());
        clone.packageNames = packageNames;
        clone.expression = expression;
        clone.fieldValue = fieldValue;
        clone.constraintType = constraintType;
        clone.declarations = clonedDeclarations;
        clone.operators = operators;
        if (leftIndexingDeclaration != null) {
            clone.leftIndexingDeclaration = leftIndexingDeclaration.clone();
        }
        clone.extractor = extractor;
        clone.isUnification = isUnification;
        clone.isDynamic = isDynamic;
        clone.conditionEvaluator = conditionEvaluator;
        clone.compilationUnit = compilationUnit != null ? compilationUnit.clone() : null;
        return clone;
    }

    public int hashCode() {
        if (isAlphaHashable()) {
            return 29 * getLeftInExpression(ConstraintTypeOperator.EQUAL).hashCode() + 31 * fieldValue.hashCode();
        }
        return expression.hashCode();
    }

    private String getLeftInExpression(ConstraintTypeOperator constraint) {
        return expression.substring(0, codeAwareIndexOf(expression, constraint.getOperator())).trim();
    }

    private boolean isAlphaHashable() {
        return fieldValue != null && constraintType == ConstraintTypeOperator.EQUAL && getType() == ConstraintType.ALPHA;
    }

    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || object.getClass() != MVELConstraint.class) {
            return false;
        }
        MVELConstraint other = (MVELConstraint) object;
        if (isAlphaHashable()) {
            if (!other.isAlphaHashable() ||
                !getLeftInExpression(ConstraintTypeOperator.EQUAL).equals(other.getLeftInExpression(ConstraintTypeOperator.EQUAL)) ||
                !fieldValue.equals(other.fieldValue)) {
                return false;
            }
        } else {
            if (!equalsIgnoreSpaces(expression, other.expression)) {
                return false;
            }
        }
        if (declarations.length != other.declarations.length) {
            return false;
        }
        for (int i = 0; i < declarations.length; i++) {
            if (!declarations[i].getExtractor().equals(other.declarations[i].getExtractor())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object object, RuleBase kbase) {
        if (!equals(object)) {
            return false;
        }
        String thisPkg = packageNames.iterator().next();
        String otherPkg = (( MVELConstraint ) object).packageNames.iterator().next();
        if (thisPkg.equals( otherPkg )) {
            return true;
        }

        Map<String, Object> thisImports = (( MVELDialectRuntimeData ) ((KnowledgeBaseImpl)kbase).getPackage(thisPkg).getDialectRuntimeRegistry().getDialectData("mvel")).getImports();
        Map<String, Object> otherImports = (( MVELDialectRuntimeData ) ((KnowledgeBaseImpl)kbase).getPackage(otherPkg).getDialectRuntimeRegistry().getDialectData("mvel")).getImports();

                    if (fieldValue != null && constraintType.getOperator() != null) {
            return equalsExpressionTokensInBothImports(getLeftInExpression(constraintType), thisImports, otherImports);
                    } else {
                        return equalsExpressionTokensInBothImports(expression, thisImports, otherImports);
                    }
                }

    private boolean equalsExpressionTokensInBothImports(String expression, Map<String, Object> thisImports, Map<String, Object> otherImports) {
        for (String token : splitExpression(expression)) {
            if (!Objects.equals(thisImports.get(token), otherImports.get(token))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Splits the expression in token (words) ignoring everything that is between quotes
     */
    private static List<String> splitExpression(String expression) {
        List<String> tokens = new ArrayList<>();
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

    protected ParserConfiguration getParserConfiguration(ValueResolver valueResolver) {
        return getMVELDialectRuntimeData(valueResolver).getParserConfiguration();
    }

    protected MVELDialectRuntimeData getMVELDialectRuntimeData(ValueResolver valueResolver) {
        return getMVELDialectRuntimeData(valueResolver.getRuleBase());
    }

    protected MVELDialectRuntimeData getMVELDialectRuntimeData(RuleBase kbase) {
        for (String packageName : packageNames) {
            InternalKnowledgePackage pkg = ((InternalKnowledgeBase)kbase).getPackage(packageName);
            if (pkg != null) {
                return ((MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel"));
            }
        }
        return null;
    }

    // MvelArrayContextEntry

    public static class MvelContextEntry implements ContextEntry {

        protected ContextEntry next;
        protected BaseTuple tuple;
        protected FactHandle rightHandle;
        protected Declaration[] declarations;

        protected transient ValueResolver valueResolver;

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

        public void updateFromTuple(ValueResolver valueResolver, BaseTuple tuple) {
            this.tuple = tuple;
            this.valueResolver = valueResolver;
        }

        public void updateFromFactHandle(ValueResolver valueResolver, FactHandle handle) {
            this.valueResolver = valueResolver;
            rightHandle = handle;
        }

        public void resetTuple() {
            tuple = null;
        }

        public void resetFactHandle() {
            valueResolver = null;
            rightHandle = null;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(tuple);
            out.writeObject(rightHandle);
            out.writeObject(declarations);
            out.writeObject(next);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            tuple = (BaseTuple) in.readObject();
            rightHandle = (FactHandle) in.readObject();
            declarations = (Declaration[]) in.readObject();
            next = (ContextEntry) in.readObject();
        }

        public FactHandle getRight() {
            return rightHandle;
        }

        public Declaration[] getDeclarations() {
            return declarations;
        }
    }

    public static class UnificationContextEntry implements ContextEntry {
        private ContextEntry contextEntry;
        private Declaration declaration;
        private Variable variable;
        private ReadAccessor reader;

        public UnificationContextEntry() { }

        public UnificationContextEntry(ContextEntry contextEntry,
                                       Declaration declaration) {
            this.contextEntry = contextEntry;
            this.declaration = declaration;
            reader = this.declaration.getExtractor();
        }

        public ContextEntry getContextEntry() {
            return this.contextEntry;
        }

        public ReadAccessor getReader() {
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

        public void updateFromFactHandle(ValueResolver valueResolver,
                                         FactHandle handle) {
            this.contextEntry.updateFromFactHandle(valueResolver, handle);
        }

        public void updateFromTuple(ValueResolver valueResolver,
                                    BaseTuple tuple) {
            DroolsQuery query = (DroolsQuery) tuple.getObject(0);
            this.variable = query.getVariables()[this.reader.getIndex()];
            if (this.variable == null) {
                // if there is no Variable, handle it as a normal constraint
                this.contextEntry.updateFromTuple(valueResolver, tuple);
            }
        }

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            this.contextEntry = (ContextEntry) in.readObject();
            this.declaration = (Declaration) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(this.contextEntry);
            out.writeObject(this.declaration);
        }

        public Variable getVariable() {
            return this.variable;
        }

    }

    public void registerEvaluationContext(RuleBuildContext buildContext) {
        evaluationContext.addContext(buildContext);
    }

    @Override
    public void mergeEvaluationContext(Constraint other) {
        if (other instanceof MVELConstraint) {
            evaluationContext.mergeRuleNameMap(((MVELConstraint)other).getEvaluationContext().getRuleNameMap());
        }
    }

    public EvaluationContext getEvaluationContext() {
        return evaluationContext;
    }

    public static class EvaluationContext implements Externalizable {

        private Map<String, Set<String>> ruleNameMap = new HashMap<>();

        public static final int MAX_RULE_DEFS = Integer.getInteger("drools.evaluationContext.maxRuleDefs", 10);
        private boolean moreThanMaxRuleDefs = false;

        public void addContext(RuleBuildContext buildContext) {
            if (moreThanMaxRuleDefs || ruleNameMap.values().stream().mapToLong(Collection::size).sum() >= MAX_RULE_DEFS) {
                moreThanMaxRuleDefs = true;
                return;
            }
            RuleImpl rule = buildContext.getRule();
            String ruleName = defaultToEmptyString(rule.getName());
            String ruleFileName = defaultToEmptyString(rule.getResource() != null ? rule.getResource().getSourcePath() : null);
            ruleNameMap.computeIfAbsent(ruleFileName, k -> new HashSet<>()).add(ruleName);
        }

        public void mergeRuleNameMap(Map<String, Set<String>> otherMap) {
            if (moreThanMaxRuleDefs || ruleNameMap.values().stream().mapToLong(Collection::size).sum() >= MAX_RULE_DEFS) {
                moreThanMaxRuleDefs = true;
                return;
            }
            otherMap.forEach((otherRuleFileName, otherRuleNameSet) -> {
                ruleNameMap.merge(otherRuleFileName, otherRuleNameSet, (thisSet, otherSet) -> {
                    thisSet.addAll(otherSet);
                    return thisSet;
                });
            });
        }

        public Map<String, Set<String>> getRuleNameMap() {
            return ruleNameMap;
        }

        public boolean isMoreThanMaxRuleDefs() {
            return moreThanMaxRuleDefs;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(ruleNameMap);
            out.writeBoolean(moreThanMaxRuleDefs);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            ruleNameMap = (Map<String, Set<String>>) in.readObject();
            moreThanMaxRuleDefs = in.readBoolean();
        }

        @Override
        public String toString() {
            return "EvaluationContext [ruleNameMap=" + ruleNameMap + ", moreThanMaxRuleDefs=" + moreThanMaxRuleDefs + "]";
        }
    }
}
