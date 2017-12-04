package org.drools.model;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.drools.model.consequences.ConditionalConsequenceBuilder;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.datasources.DataStore;
import org.drools.model.datasources.DataStream;
import org.drools.model.datasources.impl.DataStreamImpl;
import org.drools.model.datasources.impl.SetDataStore;
import org.drools.model.functions.Block0;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.accumulate.Average;
import org.drools.model.functions.accumulate.Count;
import org.drools.model.functions.accumulate.Max;
import org.drools.model.functions.accumulate.Min;
import org.drools.model.functions.accumulate.Reduce;
import org.drools.model.functions.accumulate.Sum;
import org.drools.model.functions.temporal.AfterPredicate;
import org.drools.model.functions.temporal.BeforePredicate;
import org.drools.model.functions.temporal.Interval;
import org.drools.model.functions.temporal.TemporalPredicate;
import org.drools.model.impl.AnnotationValueImpl;
import org.drools.model.impl.DataSourceDefinitionImpl;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.impl.EntryPointImpl;
import org.drools.model.impl.FromImpl;
import org.drools.model.impl.GlobalImpl;
import org.drools.model.impl.JavaClassType;
import org.drools.model.impl.Query0DefImpl;
import org.drools.model.impl.Query1DefImpl;
import org.drools.model.impl.Query2DefImpl;
import org.drools.model.impl.Query3DefImpl;
import org.drools.model.impl.Query4DefImpl;
import org.drools.model.impl.RuleBuilder;
import org.drools.model.impl.SourceImpl;
import org.drools.model.impl.TypeMetaDataImpl;
import org.drools.model.impl.ValueImpl;
import org.drools.model.impl.WindowImpl;
import org.drools.model.impl.WindowReferenceImpl;
import org.drools.model.view.AccumulateExprViewItem;
import org.drools.model.view.BindViewItem;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ExistentialExprViewItem;
import org.drools.model.view.Expr1ViewItem;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItem;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.Expr3ViewItemImpl;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.InputViewItem;
import org.drools.model.view.InputViewItemImpl;
import org.drools.model.view.OOPathBuilder;
import org.drools.model.view.OOPathBuilder.OOPathChunkBuilder;
import org.drools.model.view.TemporalExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.model.view.ViewItemBuilder;

import static org.drools.model.impl.ViewBuilder.viewItems2Patterns;

public class DSL {

    // -- DataSource --

    public static <T> DataStore<T> storeOf( T... items ) {
        return SetDataStore.storeOf( items );
    }

    public static DataStore newDataStore() {
        return storeOf();
    }

    public static DataStream newDataStream() {
        return new DataStreamImpl();
    }

    // -- TypeMetaData --

    public static TypeMetaDataImpl typeMetaData(String pkg, String name) {
        return new TypeMetaDataImpl(pkg, name);
    }

    public static AnnotationValue annotationValue(String key, String value) {
        return new AnnotationValueImpl( key, value );
    }

    // -- Variable --

    public static <T> Variable<T> any(Class<T> type) {
        return declarationOf( type( type ) );
    }

    public static <T> Declaration<T> declarationOf( Type<T> type ) {
        return new DeclarationImpl<T>( type );
    }

    public static <T> Declaration<T> declarationOf( Type<T> type, String name ) {
        return new DeclarationImpl<T>( type, name );
    }

    public static <T> Declaration<T> declarationOf( Type<T> type, DeclarationSource source ) {
        return new DeclarationImpl<T>( type ).setSource( source );
    }

    public static <T> Declaration<T> declarationOf( Type<T> type, String name, DeclarationSource source ) {
        return new DeclarationImpl<T>( type, name ).setSource( source );
    }

    public static <T> Declaration<T> declarationOf( Type<T> type, Window window ) {
        return new DeclarationImpl<T>( type ).setWindow( window );
    }

    public static <T> Declaration<T> declarationOf( Type<T> type, String name, Window window ) {
        return new DeclarationImpl<T>( type, name ).setWindow( window );
    }

    public static <T> Declaration<T> declarationOf( Type<T> type, DeclarationSource source, Window window ) {
        return new DeclarationImpl<T>( type ).setSource( source ).setWindow( window );
    }

    public static <T> Declaration<T> declarationOf( Type<T> type, String name, DeclarationSource source, Window window ) {
        return new DeclarationImpl<T>( type, name ).setSource( source ).setWindow( window );
    }

    public static <T> Global<T> globalOf( Type<T> type, String pkg ) {
        return new GlobalImpl<T>( type, pkg );
    }

    public static <T> Global<T> globalOf( Type<T> type, String pkg, String name ) {
        return new GlobalImpl<T>( type, pkg, name );
    }

    public static <T> Source<T> sourceOf( String name, Type<T> type ) {
        return new SourceImpl<T>( name, type);
    }

    public static <T> Type<T> type( Class<T> type ) {
        return new JavaClassType<T>(type);
    }

    public static EntryPoint entryPoint( String name ) {
        return new EntryPointImpl( name );
    }

    public static Window window( Window.Type type, long value ) {
        return new WindowImpl(type, value);
    }

    public static Window window( Window.Type type, long value, TimeUnit timeUnit ) {
        return new WindowImpl(type, value, timeUnit);
    }

    public static <T> WindowReference<T> window( Window.Type type, long value, Class<T> patternType, Predicate1<T>... predicates ) {
        Predicate1<T>[] ps = new Predicate1[predicates.length];
        for (int i = 0; i < predicates.length; i++) {
            ps[i] = new Predicate1.Impl<T>( predicates[i] );
        }
        return new WindowReferenceImpl( type, value, patternType, ps );
    }

    public static <T> WindowReference<T> window( Window.Type type, long value, TimeUnit timeUnit, Class<T> patternType, Predicate1<T>... predicates ) {
        Predicate1<T>[] ps = new Predicate1[predicates.length];
        for (int i = 0; i < predicates.length; i++) {
            ps[i] = new Predicate1.Impl<T>( predicates[i] );
        }
        return new WindowReferenceImpl( type, value, timeUnit, patternType, ps );
    }

    public static <T> From<T> from( Variable<T> variable, Function1<T, ?> provider ) {
        return new FromImpl<>( variable, provider );
    }

    public static <T> From<T> reactiveFrom( Variable<T> variable, Function1<T, ?> provider ) {
        return new FromImpl<>( variable, provider, true );
    }

    // -- LHS --

    public static View view(ViewItemBuilder... viewItemBuilders ) {
        return viewItems2Patterns( viewItemBuilders );
    }

    public static <T> InputViewItem<T> input( Variable<T> var ) {
        return new InputViewItemImpl<T>( var, DataSourceDefinitionImpl.DEFAULT);
    }

    public static <T> InputViewItem<T> input(Variable<T> var, String dataSourceName) {
        return new InputViewItemImpl<T>( var, new DataSourceDefinitionImpl( dataSourceName, false));
    }

    public static <T> ViewItem<T> subscribe(Variable<T> var, String dataSourceName) {
        return new InputViewItemImpl<T>( var, new DataSourceDefinitionImpl( dataSourceName, true));
    }

    public static <T> Expr1ViewItem<T> expr( Variable<T> var, Predicate1<T> predicate ) {
        return new Expr1ViewItemImpl<T>( var, new Predicate1.Impl<T>(predicate) );
    }

    public static <T, U> Expr2ViewItem<T, U> expr(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return new Expr2ViewItemImpl<T, U>( var1, var2, new Predicate2.Impl<T, U>(predicate) );
    }

    public static <T, U, X> ExprViewItem<T> expr(Variable<T> var1, Variable<U> var2, Variable<X> var3, Predicate3<T, U, X> predicate) {
        return new Expr3ViewItemImpl<T, U, X>(var1, var2, var3, new Predicate3.Impl<T, U, X>(predicate));
    }

    public static <T> Expr1ViewItem<T> expr(String exprId, Variable<T> var, Predicate1<T> predicate) {
        return new Expr1ViewItemImpl<T>( exprId, var, new Predicate1.Impl<T>(predicate));
    }

    public static <T, U> Expr2ViewItem<T, U> expr( String exprId, Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate ) {
        return new Expr2ViewItemImpl<T, U>( exprId, var1, var2, new Predicate2.Impl<T, U>(predicate));
    }

    public static <T, U, X> ExprViewItem<T> expr(String exprId, Variable<T> var1, Variable<U> var2, Variable<X> var3, Predicate3<T, U, X> predicate) {
        return new Expr3ViewItemImpl<T, U, X>(exprId, var1, var2, var3, new Predicate3.Impl<T, U, X>(predicate));
    }

    public static <T> OOPathChunkBuilder<T, T> from( Source<T> source ) {
        return new OOPathBuilder<T>(source).firstChunk();
    }

    public static ExprViewItem not(ExprViewItem expression, ExprViewItem... expressions) {
        return new ExistentialExprViewItem( Condition.Type.NOT, and( expression, expressions) );
    }

    public static <T> ExprViewItem<T> not(Variable<T> var) {
        return not( new Expr1ViewItemImpl<T>( "true", var, null ) );
    }

    public static <T> ExprViewItem<T> not(InputViewItem<T> view) {
        return not( view.getFirstVariable() );
    }

    public static <T> ExprViewItem<T> not(Variable<T> var, Predicate1<T> predicate) {
        return not(new Expr1ViewItemImpl<T>( var, new Predicate1.Impl<T>(predicate)) );
    }

    public static <T, U> ExprViewItem<T> not(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return not(new Expr2ViewItemImpl<T, U>( var1, var2, new Predicate2.Impl<T, U>(predicate)) );
    }

    public static ExprViewItem exists(ExprViewItem expression, ExprViewItem... expressions) {
        return new ExistentialExprViewItem( Condition.Type.EXISTS, and( expression, expressions) );
    }

    public static <T> ExprViewItem<T> exists(Variable<T> var) {
        return exists(new Expr1ViewItemImpl<T>( "true", var, null ) );
    }

    public static <T> ExprViewItem<T> exists(InputViewItem<T> view) {
        return exists( view.getFirstVariable() );
    }

    public static <T> ExprViewItem<T> exists(Variable<T> var, Predicate1<T> predicate) {
        return exists(new Expr1ViewItemImpl<T>( var, new Predicate1.Impl<T>(predicate)) );
    }

    public static <T, U> ExprViewItem<T> exists(Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
        return exists(new Expr2ViewItemImpl<T, U>( var1, var2, new Predicate2.Impl<T, U>(predicate)) );
    }

    public static ExprViewItem forall(ViewItem expression, ViewItem... expressions) {
        return new ExistentialExprViewItem( Condition.Type.FORALL, and( expression, expressions) );
    }

    public static <T> ExprViewItem<T> accumulate(ViewItem<?> viewItem, AccumulateFunction<?, ?, ?>... functions) {
        return new AccumulateExprViewItem(viewItem, functions);
    }

    public static ViewItem or(ViewItemBuilder<?> expression, ViewItemBuilder<?>... expressions) {
        if (expressions == null || expressions.length == 0) {
            return expression.get();
        }
        return new CombinedExprViewItem(Condition.Type.OR, combineExprs( expression, expressions ) );
    }

    public static ViewItem and(ViewItemBuilder<?> expression, ViewItemBuilder<?>... expressions) {
        if (expressions == null || expressions.length == 0) {
            return expression.get();
        }
        return new CombinedExprViewItem(Condition.Type.AND, combineExprs( expression, expressions ) );
    }

    private static ViewItem[] combineExprs( ViewItemBuilder<?> expression, ViewItemBuilder<?>... expressions ) {
        ViewItem[] andExprs = new ViewItem[expressions.length+1];
        andExprs[0] = expression.get();
        for (int i = 0; i < expressions.length; i++) {
            andExprs[i+1] = expressions[i].get();
        }
        return andExprs;
    }

    public static <T> BindViewItemBuilder<T> bind( Variable<T> var) {
        return new BindViewItemBuilder<T>(var);
    }

    public static class BindViewItemBuilder<T> implements ViewItemBuilder<T> {
        private final Variable<T> boundVariable;
        private Function1 function;
        private Variable inputVariable;
        private String reactOn;

        private BindViewItemBuilder( Variable<T> boundVariable) {
            this.boundVariable = boundVariable;
        }

        public <A> BindViewItemBuilder<T> as( Variable<A> var1, Function1<A, T> f) {
            this.function = new Function1.Impl<>(f);
            this.inputVariable = var1;
            return this;
        }

        public BindViewItemBuilder<T> reactOn( String reactOn ) {
            this.reactOn = reactOn;
            return this;
        }

        @Override
        public ViewItem<T> get() {
            return new BindViewItem<T>(boundVariable, function, inputVariable, reactOn);
        }
    }

    // -- Temporal Constraints --

    public static <T> TemporalExprViewItem<T> expr( String exprId, Variable<T> var1, Variable<?> var2, TemporalPredicate temporalPredicate ) {
        return new TemporalExprViewItem<T>( exprId, var1, var2, temporalPredicate);
    }

    public static TemporalPredicate after() {
        return new AfterPredicate();
    }

    public static TemporalPredicate after( long lowerBound, long upperBound ) {
        return new AfterPredicate( new Interval( lowerBound, upperBound ) );
    }

    public static TemporalPredicate after( long lowerBound, TimeUnit lowerUnit, long upperBound, TimeUnit upperUnit ) {
        return new AfterPredicate( new Interval( lowerBound, lowerUnit, upperBound, upperUnit ) );
    }

    public static TemporalPredicate before() {
        return new BeforePredicate();
    }

    public static TemporalPredicate before(long lowerBound, long upperBound) {
        return new BeforePredicate( new Interval( lowerBound, upperBound ) );
    }

    public static TemporalPredicate before( long lowerBound, TimeUnit lowerUnit, long upperBound, TimeUnit upperUnit ) {
        return new BeforePredicate( new Interval( lowerBound, lowerUnit, upperBound, upperUnit ) );
    }

    // -- Accumulate Functions --

    public static <N extends Number> Sum<N> sum(Variable<N> source) {
        return new Sum<>(source, Optional.empty());
    }

    public static <T> Count<T> count(Variable<T> source) {
        return new Count<>(source);
    }

    public static <T extends Number>  Min<T> min(Variable<T> source) {
        return new Min<T>(source, x -> x.doubleValue(), Optional.empty());
    }

    public static <T extends Number>  Max<T> max(Variable<T> source) {
        return new Max<T>(source, x -> x.doubleValue(), Optional.empty());
    }

    public static <N extends Number> Average<N> average(Variable<N> source) {
        return new Average<N>(source, x -> x, Optional.empty());
    }

    // -- Conditional Named Consequnce --

    public static <A> ConditionalConsequenceBuilder when(Variable<A> var, Predicate1<A> predicate) {
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

    // -- RHS --

    public static ConsequenceBuilder._0 execute(Block0 block) {
        return new ConsequenceBuilder._0(block);
    }

    public static ConsequenceBuilder._0 execute(Block1<Drools> block) {
        return new ConsequenceBuilder._0(block);
    }

    public static ConsequenceBuilder._0 executeScript(String language, String script) {
        return new ConsequenceBuilder._0(language, script);
    }

    public static <A> ConsequenceBuilder._1<A> on(Variable<A> dec1) {
        return new ConsequenceBuilder._1(dec1);
    }

    public static <A, B> ConsequenceBuilder._2<A, B> on(Variable<A> decl1, Variable<B> decl2) {
        return new ConsequenceBuilder._2(decl1, decl2);
    }

    public static <A, B, C> ConsequenceBuilder._3<A, B, C> on(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3) {
        return new ConsequenceBuilder._3(decl1, decl2, decl3);
    }

    // -- rule --

    public static RuleBuilder rule(String name) {
        return new RuleBuilder(name);
    }

    public static RuleBuilder rule(String pkg, String name) {
        return new RuleBuilder(pkg, name);
    }

    // -- query --

    public static <A> Query0Def query( String name ) {
        return new Query0DefImpl( name );
    }

    public static <A> Query0Def query( String pkg, String name ) {
        return new Query0DefImpl( pkg, name );
    }

    public static <A> Query1Def<A> query( String name, Class<A> type1 ) {
        return new Query1DefImpl<A>( name, type1 );
    }

    public static <A> Query1Def<A> query( String name, Class<A> type1, String arg1name ) {
        return new Query1DefImpl<A>( name, type1, arg1name);
    }

    public static <A> Query1Def<A> query( String pkg, String name, Class<A> type1 ) {
        return new Query1DefImpl<A>( pkg, name, type1 );
    }

    public static <A,B> Query2Def<A,B> query( String name, Class<A> type1, Class<B> type2 ) {
        return new Query2DefImpl<A,B>( name, type1, type2 );
    }

    public static <A,B> Query2Def<A,B> query( String pkg, String name, Class<A> type1, Class<B> type2 ) {
        return new Query2DefImpl<A,B>( pkg, name, type1, type2 );
    }

    public static <A,B,C> Query3Def<A,B,C> query( String name, Class<A> type1, Class<B> type2, Class<C> type3 ) {
        return new Query3DefImpl<A,B,C>(name, type1, type2, type3 );
    }

    public static <A,B,C> Query3Def<A,B,C> query( String pkg, String name, Class<A> type1, Class<B> type2, Class<C> type3 ) {
        return new Query3DefImpl<A,B,C>( pkg, name, type1, type2, type3 );
    }

    public static <A,B,C, D> Query4Def<A,B,C,D> query( String name, Class<A> type1, Class<B> type2, Class<C> type3, Class<D> type4) {
        return new Query4DefImpl<A,B,C,D>(name, type1, type2, type3, type4 );
    }

    public static <A,B,C, D> Query4Def<A,B,C,D> query( String pkg, String name, Class<A> type1, Class<B> type2, Class<C> type3, Class<D> type4) {
        return new Query4DefImpl<A,B,C,D>( pkg, name, type1, type2, type3, type4 );
    }

    public static <A> Query1Def<A> query( String pkg, String name, Class<A> type1, String arg1name ) {
        return new Query1DefImpl<A>( pkg, name, type1, arg1name);
    }

    public static <A,B> Query2Def<A,B> query( String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name ) {
        return new Query2DefImpl<A,B>( name, type1, arg1name, type2 ,arg2name);
    }

    public static <A,B> Query2Def<A,B> query( String pkg, String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name ) {
        return new Query2DefImpl<A,B>( pkg, name, type1, arg1name, type2, arg2name);
    }

    public static <A,B,C> Query3Def<A,B,C> query( String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name, Class<C> type3, String arg3name ) {
        return new Query3DefImpl<A,B,C>(name, type1, arg1name, type2, arg2name, type3, arg3name);
    }

    public static <A,B,C> Query3Def<A,B,C> query( String pkg, String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name, Class<C> type3, String arg3name ) {
        return new Query3DefImpl<A,B,C>( pkg, name, type1, arg1name, type2, arg2name, type3, arg3name);
    }

    public static <A,B,C, D> Query4Def<A,B,C,D> query( String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name, Class<C> type3, String arg3name, Class<D> type4, String arg4name) {
        return new Query4DefImpl<A,B,C,D>(name, type1, type2, type3, type4 );
    }

    public static <A,B,C, D> Query4Def<A,B,C,D> query( String pkg, String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name, Class<C> type3, String arg3name, Class<D> type4, String arg4name) {
        return new Query4DefImpl<A,B,C,D>( pkg, name, type1, type2, type3, type4 );
    }

    public static <T> Value<T> valueOf(T value) {
        return new ValueImpl<>( value );
    }
}
