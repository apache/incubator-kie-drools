package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.LambdaPrinter;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr1ViewItemImpl;

public class SingleConstraint1<A> extends AbstractSingleConstraint {

    private final Variable<A> variable;
    private final Predicate1<A> predicate;

    public SingleConstraint1(Variable<A> variable, Predicate1<A> predicate) {
        super( LambdaPrinter.print(predicate) );
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
        setReactivitySpecs( expr.getReactivitySpecs() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[] { variable };
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> predicate.test( (A)objs[0] );
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint1<?> that = ( SingleConstraint1<?> ) o;

        if ( !ModelComponent.areEqualInModel( variable, that.variable ) ) return false;
        return predicate.equals( that.predicate );
    }

    @Override
    public SingleConstraint1<A> negate() {
        return negate( new SingleConstraint1<>("!" + getExprId(), variable, predicate.negate()) );
    }

    @Override
    public SingleConstraint1<A> replaceVariable( Variable oldVar, Variable newVar ) {
        if (variable == oldVar) {
            return new SingleConstraint1<>(getExprId(), newVar, predicate);
        }
        return this;
    }
}
