package org.drools.model;

import org.drools.model.consequences.ConditionalConsequenceBuilder;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Operator;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.Predicate4;
import org.drools.model.functions.Predicate5;
import org.drools.model.functions.temporal.TemporalPredicate;
import org.drools.model.impl.Query0DefImpl;
import org.drools.model.impl.Query1DefImpl;
import org.drools.model.impl.Query2DefImpl;
import org.drools.model.impl.Query3DefImpl;
import org.drools.model.impl.Query4DefImpl;
import org.drools.model.impl.RuleBuilder;
import org.drools.model.impl.ViewBuilder;
import org.drools.model.view.BindViewItem1;
import org.drools.model.view.BindViewItem2;
import org.drools.model.view.Expr1ViewItem;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItem;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.Expr3ViewItemImpl;
import org.drools.model.view.Expr4ViewItemImpl;
import org.drools.model.view.Expr5ViewItemImpl;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.InputViewItem;
import org.drools.model.view.InputViewItemImpl;
import org.drools.model.view.TemporalExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.model.view.ViewItemBuilder;

public class FlowDSL extends DSL {

    private static final ViewBuilder VIEW_BUILDER = ViewBuilder.FLOW;

    // -- LHS --

    public static View view(ViewItemBuilder... viewItemBuilders ) {
        return VIEW_BUILDER.apply( viewItemBuilders );
    }

    public static <T> InputViewItem<T> input( Variable<T> var ) {
        return new InputViewItemImpl<>( var );
    }

    public static <T> Expr1ViewItem<T> expr( Variable<T> var) {
        return new Expr1ViewItemImpl<>( var, new Predicate1.Impl<>(t -> true) );
    }

    public static <T> Expr1ViewItem<T> expr( Variable<T> var, Predicate1<T> predicate ) {
        return new Expr1ViewItemImpl<>( var, new Predicate1.Impl<>(predicate) );
    }

    public static <T, U> Expr2ViewItem<T, U> expr(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return new Expr2ViewItemImpl<>( var1, var2, new Predicate2.Impl<>(predicate) );
    }

    public static <T, U, X> ExprViewItem<T> expr(Variable<T> var1, Variable<U> var2, Variable<X> var3, Predicate3<T, U, X> predicate) {
        return new Expr3ViewItemImpl<>(var1, var2, var3, new Predicate3.Impl<>(predicate));
    }

    public static <T> Expr1ViewItem<T> expr(String exprId, Variable<T> var, Predicate1<T> predicate) {
        return new Expr1ViewItemImpl<>( exprId, var, new Predicate1.Impl<>(predicate));
    }

    public static <T, U> Expr2ViewItem<T, U> expr( String exprId, Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate ) {
        return new Expr2ViewItemImpl<>( exprId, var1, var2, new Predicate2.Impl<>(predicate));
    }

    public static <T, U, X> ExprViewItem<T> expr(String exprId, Variable<T> var1, Variable<U> var2, Variable<X> var3, Predicate3<T, U, X> predicate) {
        return new Expr3ViewItemImpl<>(exprId, var1, var2, var3, new Predicate3.Impl<>(predicate));
    }

    public static <A, B, C, D> ExprViewItem<A> expr(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Predicate4<A, B, C, D> predicate) {
        return new Expr4ViewItemImpl<>(var1, var2, var3, var4, new Predicate4.Impl<>(predicate));
    }

    public static <A, B, C, D> ExprViewItem<A> expr(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Predicate4<A, B, C, D> predicate) {
        return new Expr4ViewItemImpl<>(exprId, var1, var2, var3, var4, new Predicate4.Impl<>(predicate));
    }

    public static <A, B, C, D, E> ExprViewItem<A> expr(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Predicate5<A, B, C, D, E> predicate) {
        return new Expr5ViewItemImpl<>(var1, var2, var3, var4, var5, new Predicate5.Impl<>(predicate));
    }

    public static <A, B, C, D, E> ExprViewItem<A> expr(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Predicate5<A, B, C, D, E> predicate) {
        return new Expr5ViewItemImpl<>(exprId, var1, var2, var3, var4, var5, new Predicate5.Impl<>(predicate));
    }

    public static boolean eval( String op, Object obj, Object... args ) {
        return eval( Operator.Register.getOperator( op ), obj, args );
    }

    public static boolean eval( Operator op, Object obj, Object... args ) {
        return op.test( obj, args );
    }

    public static <T> ExprViewItem<T> not(Variable<T> var) {
        return not( new Expr1ViewItemImpl<>( "true", var, null ) );
    }

    public static <T> ExprViewItem<T> not(InputViewItem<T> view) {
        return not( view.getFirstVariable() );
    }

    public static <T> ExprViewItem<T> not(Variable<T> var, Predicate1<T> predicate) {
        return not(new Expr1ViewItemImpl<T>( var, new Predicate1.Impl<T>(predicate)) );
    }

    public static <T, U> ExprViewItem<T> not(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return not(new Expr2ViewItemImpl<>( var1, var2, new Predicate2.Impl<T, U>(predicate)) );
    }

    public static <T> ExprViewItem<T> exists(Variable<T> var) {
        return exists(new Expr1ViewItemImpl<>( "true", var, null ) );
    }

    public static <T> ExprViewItem<T> exists(InputViewItem<T> view) {
        return exists( view.getFirstVariable() );
    }

    public static <T> ExprViewItem<T> exists(Variable<T> var, Predicate1<T> predicate) {
        return exists(new Expr1ViewItemImpl<>( var, new Predicate1.Impl<>(predicate)) );
    }

    public static <T, U> ExprViewItem<T> exists(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return exists(new Expr2ViewItemImpl<>( var1, var2, new Predicate2.Impl<>(predicate)) );
    }

    public static <T> BindViewItemBuilder<T> bind( Variable<T> var) {
        return new BindViewItemBuilder<>(var);
    }

    public static class BindViewItemBuilder<T> implements ViewItemBuilder<T> {
        private final Variable<T> boundVariable;
        private Function1 function1;
        private Function2 function2;
        private Variable inputVariable1;
        private Variable inputVariable2;
        private String reactOn;

        private BindViewItemBuilder( Variable<T> boundVariable) {
            this.boundVariable = boundVariable;
        }

        public <A> BindViewItemBuilder<T> as( Variable<A> var1, Function1<A, T> f) {
            this.function1 = new Function1.Impl<>(f);
            this.inputVariable1 = var1;
            return this;
        }

        public <A, B> BindViewItemBuilder<T> as( Variable<A> var1, Variable<B> var2, Function2<A, B, T> f) {
            this.function2 = new Function2.Impl<>(f);
            this.inputVariable1 = var1;
            this.inputVariable2 = var2;
            return this;
        }

        public BindViewItemBuilder<T> reactOn( String reactOn ) {
            this.reactOn = reactOn;
            return this;
        }

        @Override
        public ViewItem<T> get() {
            if(function1 != null) {
                return new BindViewItem1<>(boundVariable, function1, inputVariable1, reactOn);
            } else if(function2 != null) {
                return new BindViewItem2<>(boundVariable, function2, inputVariable1, inputVariable2, reactOn);
            }
            throw new UnsupportedOperationException("function1 or function2 needed");
        }
    }

    // -- Temporal Constraints --

    public static <T> TemporalExprViewItem<T> expr( String exprId, Variable<T> var1, Variable<?> var2, TemporalPredicate temporalPredicate ) {
        return new TemporalExprViewItem<>( exprId, var1, var2, temporalPredicate);
    }

    // -- Conditional Named Consequnce --

    public static <A> ConditionalConsequenceBuilder when( Variable<A> var, Predicate1<A> predicate) {
        return when( expr( var, predicate ) );
    }

    public static <A> ConditionalConsequenceBuilder when(String exprId, Variable<A> var, Predicate1<A> predicate) {
        return when( expr( exprId, var, predicate ) );
    }

    public static <A, B> ConditionalConsequenceBuilder when(Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
        return when( expr( var1, var2, predicate ) );
    }

    public static <A, B> ConditionalConsequenceBuilder when(String exprId, Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
        return when( expr( exprId, var1, var2, predicate ) );
    }

    public static ConditionalConsequenceBuilder when(ExprViewItem expr) {
        return new ConditionalConsequenceBuilder( expr );
    }

    // -- rule --

    public static RuleBuilder rule(String name) {
        return new RuleBuilder( VIEW_BUILDER, name );
    }

    public static RuleBuilder rule(String pkg, String name) {
        return new RuleBuilder( VIEW_BUILDER, pkg, name);
    }

    // -- query --

    public static Query0Def query( String name ) {
        return new Query0DefImpl( VIEW_BUILDER, name );
    }

    public static Query0Def query( String pkg, String name ) {
        return new Query0DefImpl( VIEW_BUILDER, pkg, name );
    }

    public static <A> Query1Def<A> query( String name, Class<A> type1 ) {
        return new Query1DefImpl<>( VIEW_BUILDER, name, type1 );
    }

    public static <A> Query1Def<A> query( String name, Class<A> type1, String arg1name ) {
        return new Query1DefImpl<>( VIEW_BUILDER, name, type1, arg1name);
    }

    public static <A> Query1Def<A> query( String pkg, String name, Class<A> type1 ) {
        return new Query1DefImpl<>( VIEW_BUILDER, pkg, name, type1 );
    }

    public static <A,B> Query2Def<A,B> query( String name, Class<A> type1, Class<B> type2 ) {
        return new Query2DefImpl<>( VIEW_BUILDER, name, type1, type2 );
    }

    public static <A,B> Query2Def<A,B> query( String pkg, String name, Class<A> type1, Class<B> type2 ) {
        return new Query2DefImpl<>( VIEW_BUILDER, pkg, name, type1, type2 );
    }

    public static <A,B,C> Query3Def<A,B,C> query( String name, Class<A> type1, Class<B> type2, Class<C> type3 ) {
        return new Query3DefImpl<>(VIEW_BUILDER, name, type1, type2, type3 );
    }

    public static <A,B,C> Query3Def<A,B,C> query( String pkg, String name, Class<A> type1, Class<B> type2, Class<C> type3 ) {
        return new Query3DefImpl<>( VIEW_BUILDER, pkg, name, type1, type2, type3 );
    }

    public static <A,B,C, D> Query4Def<A,B,C,D> query( String name, Class<A> type1, Class<B> type2, Class<C> type3, Class<D> type4) {
        return new Query4DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4 );
    }

    public static <A,B,C, D> Query4Def<A,B,C,D> query( String pkg, String name, Class<A> type1, Class<B> type2, Class<C> type3, Class<D> type4) {
        return new Query4DefImpl<>( VIEW_BUILDER, pkg, name, type1, type2, type3, type4 );
    }

    public static <A> Query1Def<A> query( String pkg, String name, Class<A> type1, String arg1name ) {
        return new Query1DefImpl<>( VIEW_BUILDER, pkg, name, type1, arg1name);
    }

    public static <A,B> Query2Def<A,B> query( String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name ) {
        return new Query2DefImpl<>( VIEW_BUILDER, name, type1, arg1name, type2 ,arg2name);
    }

    public static <A,B> Query2Def<A,B> query( String pkg, String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name ) {
        return new Query2DefImpl<>( VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name);
    }

    public static <A,B,C> Query3Def<A,B,C> query( String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name, Class<C> type3, String arg3name ) {
        return new Query3DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name);
    }

    public static <A,B,C> Query3Def<A,B,C> query( String pkg, String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name, Class<C> type3, String arg3name ) {
        return new Query3DefImpl<>( VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name);
    }

    public static <A,B,C,D> Query4Def<A,B,C,D> query( String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name, Class<C> type3, String arg3name, Class<D> type4, String arg4name) {
        return new Query4DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name );
    }

    public static <A,B,C,D> Query4Def<A,B,C,D> query( String pkg, String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name, Class<C> type3, String arg3name, Class<D> type4, String arg4name) {
        return new Query4DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name );
    }
}
