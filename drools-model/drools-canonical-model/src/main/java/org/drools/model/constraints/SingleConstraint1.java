package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.PredicateN;
import org.drools.model.view.Expr1ViewItemImpl;

import static org.drools.model.functions.LambdaIntrospector.getLambdaFingerprint;

public class SingleConstraint1<A> extends AbstractSingleConstraint {

    private final Variable<A> variable;
    private final Predicate1<A> predicate;

    public SingleConstraint1(Variable<A> variable, Predicate1<A> predicate) {
        super(getLambdaFingerprint(predicate));
        this.variable = variable;
        this.predicate = predicate;
    }

    public SingleConstraint1(String exprId, Variable<A> variable, Predicate1<A> predicate) {
        super(exprId);
        this.variable = variable;
        this.predicate = predicate;
    }

    public SingleConstraint1(Expr1ViewItemImpl<A> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getPredicate());
        setIndex( expr.getIndex() );
        setReactiveProps( expr.getReactiveProps() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[] { variable };
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> predicate.test( (A)objs[0] );
    }
}
