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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private MVELCompilationUnit compilationUnit;

    private transient ConditionEvaluator conditionEvaluator;
    private transient Condition analyzedCondition;

    public MvelConstraint() {}

    public MvelConstraint(String packageName, String expression, boolean isIndexable) {
        this.packageName = packageName;
        this.expression = expression;
        this.isIndexable = isIndexable;
        this.declarations = new Declaration[0];
    }

    public MvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, boolean isDynamic) {
        this.packageName = packageName;
        this.expression = expression;
        this.declarations = declarations;
        this.compilationUnit = compilationUnit;
        this.isDynamic = isDynamic;
    }

    public MvelConstraint(String packageName, String expression, boolean isIndexable, Declaration[] declarations,
                          Declaration indexingDeclaration, InternalReadAccessor extractor, boolean isUnification) {
        this.packageName = packageName;
        this.expression = expression;
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

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (isUnification) {
            throw new UnsupportedOperationException( "Should not be called" );
        }

        Map<String, Object> vars = context == null ? null : ((MvelContextEntry)context).getRightVars(workingMemory, handle);
        return evaluate(handle.getObject(), workingMemory, vars, null);
    }

    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        if (isUnification) {
            if (((UnificationContextEntry)context).getVariable() != null) {
                return true;
            }
            context = ((UnificationContextEntry)context).getContextEntry();
        }

        MvelContextEntry mvelContextEntry = (MvelContextEntry)context;
        Object object = handle.getObject();
        return evaluate(object, mvelContextEntry.workingMemory, mvelContextEntry.getVars(object), mvelContextEntry.leftTuple);
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
        return evaluate(mvelContextEntry.right, mvelContextEntry.workingMemory, mvelContextEntry.getRightVars(tuple), tuple);
    }

    private boolean evaluate(Object object, InternalWorkingMemory workingMemory, Map<String, Object> vars, LeftTuple tuple) {
        if (isDynamic) {
            if (conditionEvaluator == null) {
                createMvelConditionEvaluator(workingMemory);
            }
            return ((MvelConditionEvaluator)conditionEvaluator).evaluateDynamic(object, workingMemory, tuple);
        }

        if (!jitted) {
            if (conditionEvaluator == null) {
                createMvelConditionEvaluator(workingMemory);
                if (TEST_JITTING) { // Only for test purposes
                    boolean mvelValue = forceJitEvaluator(workingMemory, object, vars);
                }
            } else if (invocationCounter.getAndIncrement() == JIT_THRESOLD) {
                jitEvaluator(workingMemory, object, vars);
            }
        }

        return conditionEvaluator.evaluate(object, vars);
    }

    private void createMvelConditionEvaluator(InternalWorkingMemory workingMemory) {
        if (compilationUnit != null) {
            MVELDialectRuntimeData data = getMVELDialectRuntimeData(workingMemory);
            ExecutableStatement statement = (ExecutableStatement)compilationUnit.getCompiledExpression(data);
            ParserContext context = statement instanceof CompiledExpression ?
                    ((CompiledExpression)statement).getParserContext() :
                    new ParserContext(data.getParserConfiguration());
            conditionEvaluator = new MvelConditionEvaluator(compilationUnit, context, statement);
        } else {
            conditionEvaluator = new MvelConditionEvaluator(getParserConfiguration(workingMemory), expression);
        }
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
        if (propertyName != null) {
            int pos = settableProperties.indexOf(propertyName);
            if (pos < 0) {
                throw new RuntimeException("Unknown property: " + propertyName);
            }
            return 1L << pos;
        }

        // Invocation of a non-getter => cannot calculate the mask
        return Long.MAX_VALUE;
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

    // MvelContextEntry

    public static class MvelContextEntry implements ContextEntry {

        private Object left;
        private Object right;
        private Declaration[] declarations;
        private ContextEntry next;
        private LeftTuple leftTuple;

        private transient Map<String, Object> vars;
        private transient InternalWorkingMemory workingMemory;
        private transient List<Declaration> localDeclarations;

        public MvelContextEntry() { }

        public MvelContextEntry(Declaration[] declarations) {
            this.declarations = declarations;
            if (declarations.length == 1) {
                vars = new SingleValueMap<String, Object>(declarations[0].getBindingName());
            } else if (declarations.length == 2) {
                vars = new DoubleValueMap<String, Object>(declarations[0].getBindingName(), declarations[1].getBindingName());
            } else {
                vars = new HashMap<String, Object>();
            }
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
            Declaration declaration = declarations[0];
            InternalFactHandle handle = leftTuple.get(declaration);
            left = declaration.getExtractor().getValue(workingMemory, handle.getObject());
            if (declarations.length == 1) {
                vars.put(declaration.getBindingName(), left);
            } else {
                getRightVars(leftTuple);
            }
        }

        public void updateFromFactHandle(InternalWorkingMemory workingMemory, InternalFactHandle handle) {
            this.workingMemory = workingMemory;
            right = handle.getObject();
        }

        Map<String, Object> getRightVars(LeftTuple tuple) {
            for (Declaration declaration : declarations) {
                if (localDeclarations == null || !localDeclarations.contains(declaration)) {
                    InternalFactHandle fact = tuple.get(declaration);
                    if (fact != null) {
                        vars.put(declaration.getBindingName(), declaration.getExtractor().getValue(workingMemory, fact.getObject()));
                    } else {
                        if (localDeclarations == null) {
                            localDeclarations = new ArrayList<Declaration>();
                        }
                        localDeclarations.add(declaration);
                    }
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

        Map<String, Object> getVars(Object object) {
            if (localDeclarations != null) {
                for (Declaration localDeclaration : localDeclarations) {
                    vars.put(localDeclaration.getBindingName(), localDeclaration.getExtractor().getValue(workingMemory, object));
                }
            }
            return vars;
        }

        public void resetTuple() {
            leftTuple = null;
            left = null;
            if (vars != null) {
                vars.clear();
            }
        }

        public void resetFactHandle() {
            workingMemory = null;
            right = null;
            if (vars != null) {
                vars.clear();
            }
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            left = in.readObject();
            leftTuple = (LeftTuple)in.readObject();
            right = in.readObject();
            declarations = (Declaration[])in.readObject();
            next = (ContextEntry)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(left);
            out.writeObject(leftTuple);
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

    static class MapAdapter<K, V> implements Map<K, V> {
        public int size() {
            throw new UnsupportedOperationException();
        }
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }
        public boolean containsKey(Object key) {
            throw new UnsupportedOperationException();
        }
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException();
        }
        public V get(Object key) {
            throw new UnsupportedOperationException();
        }
        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }
        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }
        public void putAll(Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException();
        }
        public void clear() {
            throw new UnsupportedOperationException();
        }
        public Set<K> keySet() {
            throw new UnsupportedOperationException();
        }
        public Collection<V> values() {
            throw new UnsupportedOperationException();
        }
        public Set<Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }

    static class SingleValueMap<K, V> extends MapAdapter<K, V> {
        private final K key;
        private V value;

        SingleValueMap(K key) {
            this.key = key;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.key.equals(key);
        }

        @Override
        public V get(Object key) {
            return this.key.equals(key) ? value : null;
        }

        @Override
        public V put(K key, V value) {
            this.value = value;
            return value;
        }

        @Override
        public void clear() {
            value = null;
        }
    }

    static class DoubleValueMap<K, V> extends MapAdapter<K, V> {
        private final K key1;
        private final K key2;
        private V value1;
        private V value2;

        DoubleValueMap(K key1, K key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return key1.equals(key) || key2.equals(key);
        }

        @Override
        public V get(Object key) {
            if (key1.equals(key)) {
                return value1;
            }
            if (key2.equals(key)) {
                return value2;
            }
            return null;
        }

        @Override
        public V put(K key, V value) {
            if (key1.equals(key)) {
                value1 = value;
            } else if (key2.equals(key)) {
                value2 = value;
            }
            return value;
        }

        @Override
        public void clear() {
            value1 = null;
            value2 = null;
        }
    }
}
