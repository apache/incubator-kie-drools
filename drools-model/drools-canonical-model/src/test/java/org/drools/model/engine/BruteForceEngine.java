package org.drools.model.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.drools.model.Condition;
import org.drools.model.Constraint;
import org.drools.model.Pattern;
import org.drools.model.Rule;
import org.drools.model.SingleConstraint;
import org.drools.model.TupleHandle;
import org.drools.model.Type;
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.constraints.AndConstraints;
import org.drools.model.constraints.OrConstraints;
import org.drools.model.datasources.DataSource;
import org.drools.model.datasources.DataStore;
import org.drools.model.impl.TupleHandleImpl;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class BruteForceEngine {

    private final Map<String, DataSource> dataSources = new HashMap<String, DataSource>();

    public BruteForceEngine bind(String name, DataSource dataSource) {
        dataSources.put(name, dataSource);
        return this;
    }

    public void evaluate(Rule... rules) {
        List<Rule> firedRules = stream(rules).filter(rule -> {
            List<TupleHandle> matches = evaluate(rule.getView());
            matches.forEach(match -> {
                rule.getDefaultConsequence().getBlock().execute(
                        stream(rule.getDefaultConsequence().getDeclarations() ).map( match::get ).toArray() );
            });
            return !matches.isEmpty();
        }).collect(toList());

        // TODO: implement conflict resulution strategy (?)

        if ( firedRules.stream()
                       .filter(rule -> rule.getDefaultConsequence().isChangingWorkingMemory() )
                       .findFirst().isPresent() ) {
            evaluate(rules);
        }
    }

    public List<TupleHandle> evaluate(View view) {
        return evaluateCondition(view, initialBindings()).toTupleHandles();
    }

    public List<TupleHandle> evaluate(Condition condition) {
        return evaluateCondition(condition, initialBindings()).toTupleHandles();
    }

    private DataStore getPatternDataStore( Pattern pattern ) {
        DataStore dataStore = (DataStore) dataSources.get(pattern.getDataSourceDefinition().getName());
        if (dataStore == null) {
            throw new RuntimeException("Unknonw DataSource: " + pattern.getDataSourceDefinition().getName());
        }
        return dataStore;
    }

    private Bindings evaluateCondition(Condition condition, Bindings bindings) {
        switch (condition.getType()) {
            case PATTERN:
                return evaluateSinglePattern((Pattern)condition, bindings);
            case NOT:
            case EXISTS:
                return evaluateExistential(condition.getType(), (Pattern)condition.getSubConditions().get(0), bindings);
            case AND:
                return condition.getSubConditions().stream()
                                .reduce(bindings,
                                        (b, p) -> evaluateCondition(p, b),
                                        (b1, b2) -> null);
            case OR:
                return condition.getSubConditions().stream()
                                .reduce(new Bindings(),
                                        (b, p) -> b.append(evaluateCondition(p, bindings)),
                                        (b1, b2) -> null);
        }
        return null;
    }

    private Bindings evaluateSinglePattern(Pattern pattern, Bindings bindings) {
        Stream<Object> objects = getObjectsOfType(getPatternDataStore(pattern), pattern.getPatternVariable().getType());
        List<BoundTuple> tuples =
                objects.flatMap(obj -> generateMatches(pattern, bindings, obj))
                       .collect(toList());
        return new Bindings(tuples);
    }

    private Bindings evaluateExistential(Condition.Type existentialType, Pattern pattern, Bindings bindings) {
        List<Object> objects = getObjectsOfType(getPatternDataStore(pattern), pattern.getPatternVariable().getType()).collect(toList());
        Predicate<BoundTuple> existentialPredicate =
                tuple -> objects.stream()
                                 .map(obj -> tuple.bind(pattern.getPatternVariable(), obj))
                                 .anyMatch(t -> match(pattern.getConstraint(), t));
        if (existentialType == Condition.Type.NOT) {
            existentialPredicate = existentialPredicate.negate();
        }
        List<BoundTuple> tuples =
            bindings.tuples.parallelStream()
                    .filter(existentialPredicate)
                    .collect(toList());
        return new Bindings(tuples);
    }

    private Stream<Object> getObjectsOfType(DataStore dataStore, Type type) {
        return dataStore.getObjects().parallelStream()
                .filter(type::isInstance);
    }

    private Stream<BoundTuple> generateMatches(Pattern pattern, Bindings bindings, Object obj) {
        return bindings.tuples.parallelStream()
                        .map(t -> t.bind(pattern.getPatternVariable(), obj))
                        .filter(t -> match(pattern.getConstraint(), t));
    }

    private boolean match(Constraint constraint, BoundTuple tuple) {
        return match(constraint, tuple.getTupleHandle());
    }
    
    private boolean match(Constraint constraint, TupleHandle tuple) {
        switch (constraint.getType()) {
            case SINGLE:
                SingleConstraint singleCon = (SingleConstraint)constraint;
                Variable[] vars = singleCon.getVariables();
                switch (vars.length) {
                    case 0:
                        return singleCon.getPredicate().test();
                    case 1:
                        Object obj = tuple.get(vars[0]);
                        return singleCon.getPredicate().test(obj);
                    case 2:
                        Object obj1 = tuple.get(vars[0]);
                        Object obj2 = tuple.get(vars[1]);
                        return singleCon.getPredicate().test(obj1, obj2);
                }
            case AND:
                AndConstraints andCon = (AndConstraints)constraint;
                return andCon.getChildren().stream().allMatch(con -> match(con, tuple));
            case OR:
                OrConstraints orCon = (OrConstraints)constraint;
                return orCon.getChildren().stream().anyMatch(con -> match(con, tuple));
        }
        return false;
    }

    private static class Bindings {
        private final List<BoundTuple> tuples;

        Bindings() {
            tuples = new ArrayList<>();
        }

        Bindings(List<BoundTuple> tuples) {
            this.tuples = tuples;
        }

        List<TupleHandle> toTupleHandles() {
            return tuples.parallelStream()
                    .map(BoundTuple::getTupleHandle)
                    .collect(toList());
        }

        public Bindings append(Bindings other) {
            return new Bindings(new ArrayList<BoundTuple>() {{
                addAll(tuples);
                addAll(other.tuples);
            }});
        }

        @Override
        public String toString() {
            return tuples.toString();
        }

    }

    static Bindings initialBindings() {
        Bindings bindings = new Bindings();
        bindings.tuples.add(new BoundTuple());
        return bindings;
    }

    private static class BoundTuple {
        private final TupleHandle tuple;

        BoundTuple() {
            this(null);
        }

        BoundTuple(TupleHandleImpl tuple) {
            this.tuple = tuple;
        }

        BoundTuple bind(Variable var, Object obj) {
            return new BoundTuple(new TupleHandleImpl(tuple, obj, var));
        }

        TupleHandle getTupleHandle() {
            return tuple;
        }

        @Override
        public String toString() {
            return tuple.toString();
        }
    }
}
