package org.drools.model.patterns;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.drools.model.Condition;
import org.drools.model.Consequence;
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.consequences.ConditionalNamedConsequenceImpl;
import org.drools.model.consequences.NamedConsequenceImpl;
import org.drools.model.impl.ModelComponent;
import org.drools.model.impl.RuleImpl;

public class CompositePatterns implements Condition, View, ModelComponent {

    private final Type type;
    private final List<Condition> patterns;
    private final Set<Variable<?>> usedVars;
    private final Map<String, Consequence> consequences;

    public CompositePatterns( Type type, List<Condition> patterns ) {
        this( type, patterns, null, null);
    }

    public CompositePatterns( Type type, List<Condition> patterns, Map<String, Consequence> consequences ) {
        this( type, patterns, null, consequences);
    }

    public CompositePatterns( Type type, List<Condition> patterns, Set<Variable<?>> usedVars, Map<String, Consequence> consequences ) {
        this.type = type;
        this.patterns = patterns;
        this.usedVars = usedVars;
        this.consequences = consequences;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        return patterns.stream()
                     .flatMap( c -> Stream.of(c.getBoundVariables()) )
                     .distinct()
                     .toArray(Variable[]::new );
    }

    public Map<String, Consequence> getConsequences() {
        return consequences;
    }

    @Override
    public List<Condition> getSubConditions() {
        return patterns;
    }

    public void addCondition(int index, Condition condition) {
        patterns.add(index, condition);
    }

    @Override
    public Type getType() {
        return type;
    }

    public void ensureVariablesDeclarationInView() {
        getConsequences().forEach( this::ensureVariablesDeclarationInView );
    }

    public void ensureVariablesDeclarationInView(String name, Consequence consequence) {
        for (Variable variable : consequence.getDeclarations()) {
            if ( usedVars.add( variable ) ) {
                patterns.add( getConsequencePosition(name), new PatternImpl( variable ) );
            }
        }
    }

    private int getConsequencePosition(String name) {
        if ( RuleImpl.DEFAULT_CONSEQUENCE_NAME.equals( name ) ) {
            return patterns.size()-1;
        }
        int result = 0;
        for (Condition condition : patterns) {
            if (condition instanceof NamedConsequenceImpl && (( NamedConsequenceImpl ) condition).getName().equals( name )) {
                return result;
            }
            if (condition instanceof ConditionalNamedConsequenceImpl && (( ConditionalNamedConsequenceImpl ) condition).getThenConsequence().getName().equals( name )) {
                return result-1;
            }
            result++;
        }
        throw new IllegalArgumentException( "Cannot find consequence with name " + name );
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof CompositePatterns) ) return false;

        CompositePatterns patterns1 = ( CompositePatterns ) o;

        if ( type != patterns1.type ) return false;
        if ( !ModelComponent.areEqualInModel( patterns, patterns1.patterns ) ) return false;
        return ModelComponent.areEqualInModel( consequences, patterns1.consequences );
    }

    @Override
    public String toString() {
        return "CompositePatterns of " + type + " (" +
                "vars: " + usedVars + ", " +
                "patterns: " + patterns + ", " +
                "consequences: " + consequences + ")";
    }
}
