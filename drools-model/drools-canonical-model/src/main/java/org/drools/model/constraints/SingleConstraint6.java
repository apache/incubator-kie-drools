package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.LambdaPrinter;
import org.drools.model.functions.Predicate6;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr6ViewItemImpl;

public class SingleConstraint6<A, B, C, D, E, F> extends AbstractSingleConstraint {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Variable<F> var6;
    private final Predicate6<A, B, C, D, E, F> predicate;

    public SingleConstraint6( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Predicate6<A, B, C, D, E, F> predicate) {
        super( LambdaPrinter.print(predicate) );
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.predicate = predicate;
    }

    public SingleConstraint6( String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Predicate6<A, B, C, D, E, F> predicate) {
        super(exprId);
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.predicate = predicate;
    }

    public SingleConstraint6( Expr6ViewItemImpl<A, B, C, D, E, F> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getSecondVariable(), expr.getThirdVariable(), expr.getForthVariable(), expr.getFifthVariable(), expr.getSixthVariable(), expr.getPredicate());
        setReactiveProps( expr.getReactiveProps() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[]{var1, var2, var3, var4, var5};
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> {
            return predicate.test((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3], (E) objs[4], (F) objs[5]);
        };
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint6<?, ?, ?, ?, ?, ?> that = ( SingleConstraint6<?, ?, ?, ?, ?, ?> ) o;

        if ( !ModelComponent.areEqualInModel( var1, that.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var2, that.var2 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var3, that.var3 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var4, that.var4 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var4, that.var4 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var5, that.var5 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var6, that.var6 ) ) return false;
        return predicate.equals( that.predicate );
    }
}
