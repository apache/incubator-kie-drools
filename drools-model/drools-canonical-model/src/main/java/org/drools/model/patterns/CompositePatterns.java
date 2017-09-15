package org.drools.model.patterns;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.drools.model.Condition;
import org.drools.model.Consequence;
import org.drools.model.Constraint;
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.impl.DataSourceDefinitionImpl;

public class CompositePatterns implements Condition, View {

    private final Type type;
    private final List<Condition> patterns;
    private final Set<Variable<?>> usedVars;
    private final Map<String, Consequence> consequences;

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

    @Override
    public Type getType() {
        return type;
    }

    public void ensureVariablesDeclarationInView(Variable[] variables) {
        for (Variable variable : variables) {
            if ( usedVars.add( variable ) ) {
                patterns.add( 0, new PatternImpl( variable, Constraint.EMPTY, DataSourceDefinitionImpl.DEFAULT ) );
            }
        }
    }
}
