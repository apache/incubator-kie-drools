package org.drools.rule.constraint;

import org.drools.RuleBase;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NamedEntryPoint;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.IndexEvaluator;
import org.drools.rule.IndexableConstraint;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.MutableTypeConstraint;
import org.drools.rule.Pattern;
import org.drools.runtime.rule.Variable;
import org.drools.spi.InternalReadAccessor;
import org.drools.util.CompositeClassLoader;
import org.mvel2.ParserConfiguration;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MvelConstraint extends MutableTypeConstraint implements IndexableConstraint {
    private static final boolean TEST_JITTING = false;
    private static final int JIT_THRESOLD = Integer.MAX_VALUE;
//  private static final int JIT_THRESOLD = 2;

    private AtomicInteger invocationCounter = new AtomicInteger(0);
    private boolean jitted = false;

    private String packageName;
    private String expression;
    private String operator;
    private Declaration[] declarations;
    private Declaration indexingDeclaration;
    private InternalReadAccessor extractor;
    private boolean isUnification;

    private transient boolean isJittable;

    private transient ParserConfiguration conf;
    private transient ConditionEvaluator conditionEvaluator;

    public MvelConstraint() {}

    public MvelConstraint(String packageName, String expression, String operator) {
        this(packageName, expression, operator, null, null, null);
    }

    public MvelConstraint(String packageName, String expression, String operator,
                          Declaration[] declarations, Declaration indexingDeclaration, InternalReadAccessor extractor) {
        this.packageName = packageName;
        this.expression = expression;
        this.operator = operator;
        this.declarations = declarations == null ? new Declaration[0] : declarations;
        this.indexingDeclaration = indexingDeclaration;
        this.extractor = extractor;
        isUnification = initUnification();
        init();
    }

    private void init() {
        isJittable = initJittable();
    }

    public boolean isUnification() {
        return isUnification;
    }

    public void unsetUnification() {
        isUnification = false;
    }

    public boolean isIndexable() {
        return indexingDeclaration != null && operator.equals("==");
    }

    public IndexEvaluator getIndexEvaluator() {
        return INDEX_EVALUATOR;
    }

    private boolean initUnification() {
        if (declarations.length == 0) return false;
        Pattern pattern = declarations[0].getPattern();
        if (pattern == null) return false;
        return pattern.getObjectType().equals( new ClassObjectType( DroolsQuery.class ) ) && operator.equals("==");
    }

    private boolean initJittable() {
        return getType() == ConstraintType.ALPHA;
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        if (isUnification) {
            throw new UnsupportedOperationException( "Should not be called" );
        }

        if (TEST_JITTING && isJittable) {
            return isJITAllowed(handle, workingMemory, context);
        }

        if (!jitted) {
            if (conditionEvaluator == null) {
                conditionEvaluator = new MvelConditionEvaluator(getParserConfiguration(workingMemory), expression, operator);
            } else {
                if (isJittable && invocationCounter.getAndIncrement() == JIT_THRESOLD) {
                    CompositeClassLoader classLoader = ((AbstractRuleBase)workingMemory.getRuleBase()).getRootClassLoader();
                    conditionEvaluator = ASMConditionEvaluatorJitter.jit(((MvelConditionEvaluator)conditionEvaluator).getExecutableStatement(), classLoader);
                    jitted = true;
                }
            }
        }

        Map<String, Object> vars = context == null ? null : ((MvelContextEntry)context).getRightVars(workingMemory, handle);
        try {
            return conditionEvaluator.evaluate(handle.getObject(), vars);
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            return false;
        }
    }

    public boolean isJITAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        boolean mvelValue = false;
        if (conditionEvaluator == null) {
            conditionEvaluator = new MvelConditionEvaluator(getParserConfiguration(workingMemory), expression, operator);
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
                throw new RuntimeException("Exception jitting: " + expression, t);
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

//        System.out.println(expression + " => mvel = " + mvelValue + "; asm = " + asmValue);
        return asmValue;
    }

    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        if (isUnification) {
            if (((UnificationContextEntry)context).getVariable() != null) {
                return true;
            }
            context = ((UnificationContextEntry)context).getContextEntry();
        }

        if (conditionEvaluator == null) {
            ParserConfiguration conf = getParserConfiguration(((MvelContextEntry)context).workingMemory);
            conditionEvaluator = new MvelConditionEvaluator(conf, expression, operator);
        }
        return conditionEvaluator.evaluate(handle.getObject(), ((MvelContextEntry) context).vars);
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

        if (conditionEvaluator == null) {
            ParserConfiguration conf = getParserConfiguration(((MvelContextEntry)context).workingMemory);
            conditionEvaluator = new MvelConditionEvaluator(conf, expression, operator);
        }
        MvelContextEntry mvelContextEntry = (MvelContextEntry)context;
        return conditionEvaluator.evaluate(mvelContextEntry.right, mvelContextEntry.getRightVars(tuple));
    }

    public ContextEntry createContextEntry() {
        if (declarations.length == 0) return null;
        ContextEntry contextEntry = new MvelContextEntry(declarations);
        if (isUnification) {
            contextEntry = new UnificationContextEntry(contextEntry, declarations[0]);
        }
        return contextEntry;
    }

    public InternalReadAccessor getFieldExtractor() {
        return extractor;
    }

    public Declaration[] getRequiredDeclarations() {
        return declarations;
    }

    public Declaration getIndexingDeclaration() {
        indexingDeclaration.getPattern().setOffset(declarations[0].getPattern().getOffset());
        return indexingDeclaration;
    }

    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        if (declarations[0].equals(oldDecl)) {
            declarations[0] = newDecl;
        }
        if (indexingDeclaration != null && indexingDeclaration.equals(oldDecl)) {
            indexingDeclaration = newDecl;
        }
    }

    // Externalizable

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(packageName);
        out.writeObject(expression);
        out.writeObject(operator);
        out.writeObject(declarations);
        out.writeObject(indexingDeclaration);
        out.writeObject(extractor);
        out.writeBoolean(isUnification);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        packageName = (String)in.readObject();
        expression = (String)in.readObject();
        operator = (String)in.readObject();
        declarations = (Declaration[]) in.readObject();
        indexingDeclaration = (Declaration) in.readObject();
        extractor = (InternalReadAccessor) in.readObject();
        isUnification = in.readBoolean();
        init();
    }

    public boolean isTemporal() {
        return false;
    }

    public Object clone() {
        return new MvelConstraint(packageName, expression, operator, declarations, indexingDeclaration, extractor);
    }

    public int hashCode() {
        return expression.hashCode();
    }

    public boolean equals(final Object object) {
        if ( this == object ) return true;
        if ( object == null || object.getClass() != MvelConstraint.class ) return false;
        return expression.equals(((MvelConstraint)object).expression);
    }

    private ParserConfiguration getParserConfiguration(InternalWorkingMemory workingMemory) {
        return conf == null ? getParserConfiguration(workingMemory.getRuleBase()) : conf;
    }

    private ParserConfiguration getParserConfiguration(RuleBase ruleBase) {
        MVELDialectRuntimeData data = (MVELDialectRuntimeData)ruleBase.getPackage(packageName).getDialectRuntimeRegistry().getDialectData( "mvel" );
        return data.getParserConfiguration();
    }

    // MvelContextEntry

    public static class MvelContextEntry implements ContextEntry {

        Object left;
        Object right;
        Declaration[] declarations;
        ContextEntry next;

        transient Map<String, Object> vars = new HashMap<String, Object>();
        transient InternalWorkingMemory workingMemory;

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

        public void updateFromTuple(InternalWorkingMemory workingMemory, LeftTuple tuple) {
            this.workingMemory = workingMemory;
            Declaration declaration = declarations[0];
            InternalFactHandle handle = tuple.get(declaration);
            left = declaration.getExtractor().getValue(workingMemory, handle.getObject());
            getRightVars(workingMemory, handle);
        }

        public void updateFromFactHandle(InternalWorkingMemory workingMemory, InternalFactHandle handle) {
            this.workingMemory = workingMemory;
            right = handle.getObject();
        }

        Map<String, Object> getRightVars(LeftTuple tuple) {
            return getRightVars(workingMemory, tuple.get(declarations[0]));
        }

        Map<String, Object> getRightVars(InternalFactHandle handle) {
            return getRightVars(workingMemory, handle);
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
            vars.clear();
        }

        public void resetFactHandle() {
            workingMemory = null;
            right = null;
            vars.clear();
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            left = in.readObject();
            right = in.readObject();
            declarations = (Declaration[])in.readObject();
            next = (ContextEntry)in.readObject();
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
