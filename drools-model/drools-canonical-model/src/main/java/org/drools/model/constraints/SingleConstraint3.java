package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr3ViewItemImpl;

import static org.drools.model.functions.LambdaIntrospector.getLambdaFingerprint;

public class SingleConstraint3<A, B, C> extends AbstractSingleConstraint {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Predicate3<A, B, C> predicate;

    public SingleConstraint3(Variable<A> var1, Variable<B> var2, Variable<C> var3, Predicate3<A, B, C> predicate) {
        super(getLambdaFingerprint(predicate));
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.predicate = predicate;
    }

    public SingleConstraint3(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Predicate3<A, B, C> predicate) {
        super(exprId);
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.predicate = predicate;
    }

    public SingleConstraint3(Expr3ViewItemImpl<A, B, C> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getSecondVariable(), expr.getThirdVariable(), expr.getPredicate());
        setReactiveProps( expr.getReactiveProps() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[]{var1, var2, var3};
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> {
            return predicate.test((A) objs[0], (B) objs[1], (C) objs[2]);
        };
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint3<?, ?, ?> that = ( SingleConstraint3<?, ?, ?> ) o;

        if ( !ModelComponent.areEqualInModel( var1, that.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var2, that.var2 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var3, that.var3 ) ) return false;
        return predicate.equals( that.predicate );
    }
}
