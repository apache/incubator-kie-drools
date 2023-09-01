package org.drools.base.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.reteoo.SortDeclarations;
import org.drools.base.rule.accessor.Accumulator;
import org.kie.api.runtime.rule.FactHandle;

/**
 * A class to represent the Accumulate CE
 */
public abstract class Accumulate extends ConditionalElement
    implements
    PatternSource {

    private static final long    serialVersionUID = 510l;

    protected RuleConditionElement source;
    protected Declaration[]        requiredDeclarations;
    protected Declaration[]        innerDeclarationCache;

    protected List<Accumulate>     cloned           = Collections.emptyList();

    public Accumulate() { }

    public Accumulate(final RuleConditionElement source,
                      final Declaration[] requiredDeclarations) {

        this.source = source;
        this.requiredDeclarations = requiredDeclarations;
        initInnerDeclarationCache();
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        source = (RuleConditionElement) in.readObject();
        requiredDeclarations = (Declaration[]) in.readObject();
        this.cloned = (List<Accumulate>) in.readObject();
        initInnerDeclarationCache();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.source );
        out.writeObject( this.requiredDeclarations );
        out.writeObject( this.cloned );
    }

    public abstract Accumulator[] getAccumulators();

    public abstract Object createFunctionContext();

    /**
     * Executes the initialization block of code
     */
    public abstract Object init(final Object workingMemoryContext,
                                final Object accContext,
                                Object funcContext, final BaseTuple leftTuple,
                                final ValueResolver valueResolver);

    /**
     * Executes the accumulate (action) code for the given fact handle
     */
    public abstract Object accumulate(final Object workingMemoryContext,
                                      final Object context,
                                      final BaseTuple match,
                                      final FactHandle handle,
                                      final ValueResolver valueResolver);

    /**
     * Executes the reverse (action) code for the given fact handle
     */
    public abstract boolean tryReverse(final Object workingMemoryContext,
                                       final Object context,
                                       final BaseTuple leftTuple,
                                       final FactHandle handle,
                                       final BaseTuple match,
                                       final ValueResolver valueResolver);

    /**
     * Gets the result of the accumulation
     */
    public abstract Object getResult(final Object workingMemoryContext,
                                     final Object context,
                                     final BaseTuple leftTuple,
                                     final ValueResolver valueResolver);

    /**
     * Returns true if this accumulate supports reverse
     */
    public abstract boolean supportsReverse();

    public abstract Accumulate clone();

    protected void registerClone(Accumulate clone) {
        if ( this.cloned == Collections.EMPTY_LIST ) {
            this.cloned = new ArrayList<>( 1 );
        }
        this.cloned.add( clone );
    }

    public RuleConditionElement getSource() {
        return this.source;
    }

    public Map<String, Declaration> getInnerDeclarations() {
        return this.source.getInnerDeclarations();
    }

    public Map<String, Declaration> getOuterDeclarations() {
        return Collections.emptyMap();
    }
    
    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return this.source.getInnerDeclarations().get( identifier );
    }

    public abstract Object createWorkingMemoryContext();

    public List<RuleConditionElement> getNestedElements() {
        return Collections.singletonList( this.source );
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }

    public abstract boolean isMultiFunction();

    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved) {
        for ( int i = 0; i < this.requiredDeclarations.length; i++ ) {
            if ( this.requiredDeclarations[i].equals( declaration ) ) {
                this.requiredDeclarations[i] = resolved;
            }
        }
        replaceAccumulatorDeclaration(declaration, resolved);
    }

    public abstract void replaceAccumulatorDeclaration(Declaration declaration,
                                                          Declaration resolved);
    
    protected Declaration[] getInnerDeclarationCache() {
        return this.innerDeclarationCache;
    }

    private void initInnerDeclarationCache() {
        Map<String, Declaration> innerDeclarations = this.source.getInnerDeclarations();
        this.innerDeclarationCache = innerDeclarations.values().toArray( new Declaration[innerDeclarations.size()] );
        Arrays.sort(this.innerDeclarationCache, SortDeclarations.instance);
    }

    public Declaration[] getRequiredDeclarations() {
        return requiredDeclarations;
    }

    public boolean hasRequiredDeclarations() {
        return requiredDeclarations != null && requiredDeclarations.length > 0;
    }

    @Override
    public boolean requiresLeftActivation() {
        return true;
    }


    public boolean isGroupBy() {
        return false;
    }

    public abstract Object accumulate(Object workingMemoryContext, BaseTuple match, FactHandle childHandle,
                                      Object groupByContext,
                                      Object tupleList,
                                      ValueResolver valueResolver);
}
