package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.LambdaPrinter;
import org.drools.model.functions.Predicate10;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr10ViewItemImpl;

public class SingleConstraint10<A, B, C, D, E, F, G, H, I, J> extends AbstractSingleConstraint {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Variable<F> var6;
    private final Variable<G> var7;
    private final Variable<H> var8;
    private final Variable<I> var9;
    private final Variable<J> var10;
    private final Predicate10<A, B, C, D, E, F, G, H, I, J> predicate;

    public SingleConstraint10( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                               Variable<H> var8, Variable<I> var9, Variable<J> var10,
                               Predicate10<A, B, C, D, E, F, G, H, I, J> predicate) {
        super( LambdaPrinter.print(predicate) );
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.var8 = var8;
        this.var9 = var9;
        this.var10 = var10;
        this.predicate = predicate;
    }

    public SingleConstraint10( String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                               Variable<H> var8, Variable<I> var9, Variable<J> var10,
                               Predicate10<A, B, C, D, E, F, G, H, I, J> predicate) {
        super(exprId);
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.var8 = var8;
        this.var9 = var9;
        this.var10 = var10;
        this.predicate = predicate;
    }

    public SingleConstraint10( Expr10ViewItemImpl<A, B, C, D, E, F, G, H, I, J> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getVar2(), expr.getVar3(), expr.getVar4(), expr.getVar5(), expr.getVar6(), expr.getVar7(),
                expr.getVar8(), expr.getVar9(), expr.getVar10(), expr.getPredicate());
        setReactiveProps( expr.getReactiveProps() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[]{var1, var2, var3, var4, var5, var6, var7, var8, var9, var10};
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> {
            return predicate.test((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3], (E) objs[4], (F) objs[5], (G) objs[6], (H) objs[7], (I) objs[8], (J) objs[9]);
        };
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint10<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> that = ( SingleConstraint10<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> ) o;

        if ( !ModelComponent.areEqualInModel( var1, that.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var2, that.var2 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var3, that.var3 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var4, that.var4 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var5, that.var5 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var6, that.var6 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var7, that.var7 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var8, that.var8 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var9, that.var9 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var10, that.var10 ) ) return false;
        return predicate.equals( that.predicate );
    }
}
