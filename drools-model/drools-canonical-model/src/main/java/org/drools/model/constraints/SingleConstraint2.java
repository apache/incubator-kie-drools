package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.PredicateN;
import org.drools.model.view.Expr2ViewItemImpl;

import static org.drools.model.functions.LambdaIntrospector.getLambdaFingerprint;

public class SingleConstraint2<A, B> extends AbstractSingleConstraint {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Predicate2<A, B> predicate;

    public SingleConstraint2(Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
        super(getLambdaFingerprint(predicate, var1, var2));
        this.var1 = var1;
        this.var2 = var2;
        this.predicate = predicate;
    }

    public SingleConstraint2(String exprId, Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
        super(exprId);
        this.var1 = var1;
        this.var2 = var2;
        this.predicate = predicate;
    }

    public SingleConstraint2(Expr2ViewItemImpl<A, B> expr) {
        this( expr.getExprId(), expr.getFirstVariable(), expr.getSecondVariable(), expr.getPredicate() );
        setIndex( expr.getIndex() );
        setReactiveProps( expr.getReactiveProps() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[] { var1, var2 };
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> predicate.test( (A)objs[0], (B)objs[1] );
    }
}
