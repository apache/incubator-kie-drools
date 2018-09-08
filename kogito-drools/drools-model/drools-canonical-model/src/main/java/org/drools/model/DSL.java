/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model;

import java.util.concurrent.TimeUnit;

import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.datasources.DataStore;
import org.drools.model.datasources.DataStream;
import org.drools.model.datasources.impl.DataStreamImpl;
import org.drools.model.datasources.impl.SetDataStore;
import org.drools.model.functions.Block0;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Function0;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Function3;
import org.drools.model.functions.Operator;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate10;
import org.drools.model.functions.Predicate11;
import org.drools.model.functions.Predicate12;
import org.drools.model.functions.Predicate13;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.Predicate4;
import org.drools.model.functions.Predicate5;
import org.drools.model.functions.Predicate6;
import org.drools.model.functions.Predicate7;
import org.drools.model.functions.Predicate8;
import org.drools.model.functions.Predicate9;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.functions.temporal.AbstractTemporalPredicate;
import org.drools.model.functions.temporal.AfterPredicate;
import org.drools.model.functions.temporal.BeforePredicate;
import org.drools.model.functions.temporal.CoincidesPredicate;
import org.drools.model.functions.temporal.DuringPredicate;
import org.drools.model.functions.temporal.FinishedbyPredicate;
import org.drools.model.functions.temporal.FinishesPredicate;
import org.drools.model.functions.temporal.IncludesPredicate;
import org.drools.model.functions.temporal.MeetsPredicate;
import org.drools.model.functions.temporal.MetbyPredicate;
import org.drools.model.functions.temporal.OverlappedbyPredicate;
import org.drools.model.functions.temporal.OverlapsPredicate;
import org.drools.model.functions.temporal.StartedbyPredicate;
import org.drools.model.functions.temporal.StartsPredicate;
import org.drools.model.functions.temporal.TemporalPredicate;
import org.drools.model.impl.AnnotationValueImpl;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.impl.EntryPointImpl;
import org.drools.model.impl.From0Impl;
import org.drools.model.impl.From1Impl;
import org.drools.model.impl.From2Impl;
import org.drools.model.impl.From3Impl;
import org.drools.model.impl.GlobalImpl;
import org.drools.model.impl.PrototypeImpl;
import org.drools.model.impl.PrototypeVariableImpl;
import org.drools.model.impl.TypeMetaDataImpl;
import org.drools.model.impl.UnitDataImpl;
import org.drools.model.impl.ValueImpl;
import org.drools.model.impl.WindowImpl;
import org.drools.model.impl.WindowReferenceImpl;
import org.drools.model.view.AccumulateExprViewItem;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ExistentialExprViewItem;
import org.drools.model.view.Expr10ViewItemImpl;
import org.drools.model.view.Expr11ViewItemImpl;
import org.drools.model.view.Expr12ViewItemImpl;
import org.drools.model.view.Expr13ViewItemImpl;
import org.drools.model.view.Expr1ViewItem;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItem;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.Expr3ViewItemImpl;
import org.drools.model.view.Expr4ViewItemImpl;
import org.drools.model.view.Expr5ViewItemImpl;
import org.drools.model.view.Expr6ViewItemImpl;
import org.drools.model.view.Expr7ViewItemImpl;
import org.drools.model.view.Expr8ViewItemImpl;
import org.drools.model.view.Expr9ViewItemImpl;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.FixedValueItem;
import org.drools.model.view.ViewItem;
import org.drools.model.view.ViewItemBuilder;

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

    public static TypeMetaDataImpl typeMetaData( Class<?> type ) {
        return new TypeMetaDataImpl(type);
    }

    public static AnnotationValue annotationValue(String key, String value) {
        return new AnnotationValueImpl( key, value );
    }

    // -- Prototype --

    public static Prototype prototype(String pkg, String name, Prototype.Field... fields) {
        return new PrototypeImpl( pkg, name, fields );
    }

    public static Prototype.Field field(String name, Class<?> type) {
        return new PrototypeImpl.FieldImpl( name, type );
    }

    public static PrototypeVariable declarationOf( Prototype prototype ) {
        return new PrototypeVariableImpl( prototype );
    }

    // -- Variable --

    public static <T> Variable<T> any(Class<T> type) {
        return declarationOf( type );
    }

    public static <T> Declaration<T> declarationOf( Class<T> type ) {
        return new DeclarationImpl<T>( type );
    }

    public static <T> Declaration<T> declarationOf( Class<T> type, String name ) {
        return new DeclarationImpl<T>( type, name );
    }

    public static <T> Declaration<T> declarationOf( Class<T> type, DeclarationSource source ) {
        return new DeclarationImpl<T>( type ).setSource( source );
    }

    public static <T> Declaration<T> declarationOf( Class<T> type, String name, DeclarationSource source ) {
        return new DeclarationImpl<T>( type, name ).setSource( source );
    }

    public static <T> Declaration<T> declarationOf( Class<T> type, Window window ) {
        return new DeclarationImpl<T>( type ).setWindow( window );
    }

    public static <T> Declaration<T> declarationOf( Class<T> type, String name, Window window ) {
        return new DeclarationImpl<T>( type, name ).setWindow( window );
    }

    public static <T> Declaration<T> declarationOf( Class<T> type, DeclarationSource source, Window window ) {
        return new DeclarationImpl<T>( type ).setSource( source ).setWindow( window );
    }

    public static <T> Declaration<T> declarationOf( Class<T> type, String name, DeclarationSource source, Window window ) {
        return new DeclarationImpl<T>( type, name ).setSource( source ).setWindow( window );
    }

    public static <T> Global<T> globalOf( Class<T> type, String pkg ) {
        return new GlobalImpl<T>( type, pkg );
    }

    public static <T> Global<T> globalOf( Class<T> type, String pkg, String name ) {
        return new GlobalImpl<T>( type, pkg, name );
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
        return new WindowReferenceImpl( type, value, patternType, getPredicateForWindow( predicates ) );
    }

    public static <T> WindowReference<T> window( Window.Type type, long value, TimeUnit timeUnit, Class<T> patternType, Predicate1<T>... predicates ) {
        return new WindowReferenceImpl( type, value, timeUnit, patternType, getPredicateForWindow( predicates ) );
    }

    public static <T> WindowReference<T> window( Window.Type type, long value, Class<T> patternType, EntryPoint entryPoint, Predicate1<T>... predicates ) {
        return new WindowReferenceImpl( type, value, patternType, entryPoint, getPredicateForWindow( predicates ) );
    }

    public static <T> WindowReference<T> window( Window.Type type, long value, TimeUnit timeUnit, Class<T> patternType, EntryPoint entryPoint, Predicate1<T>... predicates ) {
        return new WindowReferenceImpl( type, value, timeUnit, patternType, entryPoint, getPredicateForWindow( predicates ) );
    }

    private static <T> Predicate1<T>[] getPredicateForWindow( Predicate1<T>[] predicates ) {
        Predicate1<T>[] ps = new Predicate1[predicates.length];
        for (int i = 0; i < predicates.length; i++) {
            ps[i] = new Predicate1.Impl<T>( predicates[i] );
        }
        return ps;
    }

    public static UnitData<?> unitData( String name ) {
        return new UnitDataImpl( name );
    }

    public static <T> UnitData<T> unitData( Class<T> type, String name ) {
        return new UnitDataImpl( type, name );
    }

    public static <T> From<T> from( T value ) {
        return from( () -> value );
    }

    public static <T> From<T> from( Variable<T> variable ) {
        return new From1Impl<>( variable );
    }

    public static <T> From<T> from( Function0<T> provider ) {
        return new From0Impl<T>( provider );
    }

    public static <T> From<T> from( Variable<T> variable, Function1<T, ?> provider ) {
        return new From1Impl<>( variable, new Function1.Impl<>(provider) );
    }

    public static <A,B> From<A> from( Variable<A> var1, Variable<B> var2, Function2<A, B, ?> provider ) {
        return new From2Impl<>( var1, var2, new Function2.Impl<>(provider) );
    }

    public static <A,B,C> From<A> from( Variable<A> var1, Variable<B> var2, Variable<C> var3, Function3<A, B, C, ?> provider ) {
        return new From3Impl<>( var1, var2, var3, new Function3.Impl<>(provider) );
    }

    public static <T> From<T> reactiveFrom( Variable<T> variable, Function1<T, ?> provider ) {
        return new From1Impl<>( variable, provider, true );
    }

    public static ViewItem or( ViewItemBuilder<?> expression, ViewItemBuilder<?>... expressions) {
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

    // -- Expressions --

    public static Expr1ViewItem<Boolean> expr( String exprId, Variable<Boolean> var ) {
        return new Expr1ViewItemImpl<>( exprId, var, new Predicate1.Impl<>( x -> x != null ? x : false ) );
    }

    public static <T> Expr1ViewItem<T> expr( Variable<T> var, Predicate1<T> predicate ) {
        return new Expr1ViewItemImpl<>( var, new Predicate1.Impl<>(predicate) );
    }

    public static <T, U> Expr2ViewItem<T, U> expr( Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate) {
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

    public static <A, B, C, D, E, F> ExprViewItem<A> expr(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Predicate6<A, B, C, D, E, F> predicate) {
        return new Expr6ViewItemImpl<>(var1, var2, var3, var4, var5, var6, new Predicate6.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F> ExprViewItem<A> expr(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Predicate6<A, B, C, D, E, F> predicate) {
        return new Expr6ViewItemImpl<>(exprId, var1, var2, var3, var4, var5, var6, new Predicate6.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G> ExprViewItem<A> expr(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7, Predicate7<A, B, C, D, E, F, G> predicate) {
        return new Expr7ViewItemImpl<>(var1, var2, var3, var4, var5, var6, var7, new Predicate7.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G> ExprViewItem<A> expr(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7, Predicate7<A, B, C, D, E, F, G> predicate) {
        return new Expr7ViewItemImpl<>(exprId, var1, var2, var3, var4, var5, var6, var7, new Predicate7.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H> ExprViewItem<A> expr(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                   Variable<H> var8,
                                                                   Predicate8<A, B, C, D, E, F, G, H> predicate) {
        return new Expr8ViewItemImpl<>(var1, var2, var3, var4, var5, var6, var7, var8, new Predicate8.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H> ExprViewItem<A> expr(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                   Variable<H> var8,
                                                                   Predicate8<A, B, C, D, E, F, G, H> predicate) {
        return new Expr8ViewItemImpl<>(exprId, var1, var2, var3, var4, var5, var6, var7, var8, new Predicate8.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H, I> ExprViewItem<A> expr(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                      Variable<H> var8, Variable<I> var9,
                                                                      Predicate9<A, B, C, D, E, F, G, H, I> predicate) {
        return new Expr9ViewItemImpl<>(var1, var2, var3, var4, var5, var6, var7, var8, var9, new Predicate9.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H, I> ExprViewItem<A> expr(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                      Variable<H> var8, Variable<I> var9,
                                                                      Predicate9<A, B, C, D, E, F, G, H, I> predicate) {
        return new Expr9ViewItemImpl<>(exprId, var1, var2, var3, var4, var5, var6, var7, var8, var9, new Predicate9.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H, I, J> ExprViewItem<A> expr(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                         Variable<H> var8, Variable<I> var9, Variable<J> var10,
                                                                         Predicate10<A, B, C, D, E, F, G, H, I, J> predicate) {
        return new Expr10ViewItemImpl<>(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, new Predicate10.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H, I, J> ExprViewItem<A> expr(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                         Variable<H> var8, Variable<I> var9, Variable<J> var10,
                                                                         Predicate10<A, B, C, D, E, F, G, H, I, J> predicate) {
        return new Expr10ViewItemImpl<>(exprId, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, new Predicate10.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H, I, J, K> ExprViewItem<A> expr(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                            Variable<H> var8, Variable<I> var9, Variable<J> var10, Variable<K> var11,
                                                                            Predicate11<A, B, C, D, E, F, G, H, I, J, K> predicate) {
        return new Expr11ViewItemImpl<>(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, new Predicate11.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H, I, J, K> ExprViewItem<A> expr(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                            Variable<H> var8, Variable<I> var9, Variable<J> var10, Variable<K> var11,
                                                                            Predicate11<A, B, C, D, E, F, G, H, I, J, K> predicate) {
        return new Expr11ViewItemImpl<>(exprId, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, new Predicate11.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H, I, J, K, L> ExprViewItem<A> expr(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                               Variable<H> var8, Variable<I> var9, Variable<J> var10, Variable<K> var11, Variable<L> var12,
                                                                               Predicate12<A, B, C, D, E, F, G, H, I, J, K, L> predicate) {
        return new Expr12ViewItemImpl<>(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, new Predicate12.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H, I, J, K, L> ExprViewItem<A> expr(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                               Variable<H> var8, Variable<I> var9, Variable<J> var10, Variable<K> var11, Variable<L> var12,
                                                                               Predicate12<A, B, C, D, E, F, G, H, I, J, K, L> predicate) {
        return new Expr12ViewItemImpl<>(exprId, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, new Predicate12.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H, I, J, K, L, M> ExprViewItem<A> expr(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                               Variable<H> var8, Variable<I> var9, Variable<J> var10, Variable<K> var11, Variable<L> var12, Variable<M> var13,
                                                                               Predicate13<A, B, C, D, E, F, G, H, I, J, K, L, M> predicate) {
        return new Expr13ViewItemImpl<>(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, new Predicate13.Impl<>(predicate));
    }

    public static <A, B, C, D, E, F, G, H, I, J, K, L, M> ExprViewItem<A> expr(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                                                                               Variable<H> var8, Variable<I> var9, Variable<J> var10, Variable<K> var11, Variable<L> var12, Variable<M> var13,
                                                                               Predicate13<A, B, C, D, E, F, G, H, I, J, K, L, M> predicate) {
        return new Expr13ViewItemImpl<>(exprId, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, new Predicate13.Impl<>(predicate));
    }

    public static FixedValueItem expr( boolean value ) {
        return new FixedValueItem( null, value );
    }

    public static FixedValueItem expr( String exprId, boolean value ) {
        return new FixedValueItem( exprId, value );
    }

    // -- Existential operator --

    public static ExprViewItem not( ViewItemBuilder<?> expression, ViewItemBuilder<?>... expressions) {
        return new ExistentialExprViewItem( Condition.Type.NOT, and( expression, expressions) );
    }

    public static ExprViewItem exists(ViewItemBuilder<?> expression, ViewItemBuilder<?>... expressions) {
        return new ExistentialExprViewItem( Condition.Type.EXISTS, and( expression, expressions) );
    }

    public static ExprViewItem forall(ViewItemBuilder<?> expression, ViewItemBuilder<?>... expressions) {
        return new ExistentialExprViewItem( Condition.Type.FORALL, and( expression, expressions) );
    }

    // -- Accumulate Functions --

    public static <T> ExprViewItem<T> accumulate(ViewItem<?> viewItem, AccumulateFunction firstFunction, AccumulateFunction... otherFunctions) {
        AccumulateFunction[] functions = new AccumulateFunction[otherFunctions.length+1];
        functions[0] = firstFunction;
        System.arraycopy( otherFunctions, 0, functions, 1, otherFunctions.length );
        return new AccumulateExprViewItem(viewItem, functions);
    }

    // Legay case - source is defined in the generated Invoker class
    public static AccumulateFunction accFunction( Class<?> accFunctionClass) {
        return new AccumulateFunction(null, accFunctionClass);
    }

    public static AccumulateFunction accFunction( Class<?> accFunctionClass, Argument source) {
        return new AccumulateFunction(source, accFunctionClass);
    }

    // -- Temporal Constraints --

    public static TemporalPredicate not(TemporalPredicate predicate) {
        return (( AbstractTemporalPredicate ) predicate).setNegated( true );
    }

    public static TemporalPredicate after() {
        return new AfterPredicate();
    }

    public static TemporalPredicate after( long lowerBound, long upperBound ) {
        return after(lowerBound, TimeUnit.MILLISECONDS, upperBound, TimeUnit.MILLISECONDS);
    }

    public static TemporalPredicate after( long lowerBound, TimeUnit lowerUnit ) {
        return after(lowerBound, lowerUnit, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public static TemporalPredicate after( long lowerBound, TimeUnit lowerUnit, long upperBound, TimeUnit upperUnit ) {
        return new AfterPredicate( lowerBound, lowerUnit, upperBound, upperUnit );
    }

    public static TemporalPredicate before() {
        return new BeforePredicate();
    }

    public static TemporalPredicate before(long lowerBound, long upperBound) {
        return before(lowerBound, TimeUnit.MILLISECONDS, upperBound, TimeUnit.MILLISECONDS);
    }

    public static TemporalPredicate before( long lowerBound, TimeUnit lowerUnit ) {
        return before(lowerBound, lowerUnit, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public static TemporalPredicate before( long lowerBound, TimeUnit lowerUnit, long upperBound, TimeUnit upperUnit ) {
        return new BeforePredicate( lowerBound, lowerUnit, upperBound, upperUnit );
    }

    public static TemporalPredicate coincides() {
        return coincides( 0, TimeUnit.MILLISECONDS );
    }

    public static TemporalPredicate coincides( long dev, TimeUnit devUnit ) {
        return new CoincidesPredicate( dev, devUnit );
    }

    public static TemporalPredicate coincides( long startDev, TimeUnit startDevUnit, long endDev, TimeUnit endDevUnit ) {
        return new CoincidesPredicate( startDev, startDevUnit, endDev, endDevUnit );
    }

    public static TemporalPredicate during() {
        return new DuringPredicate();
    }

    public static TemporalPredicate during(long max, TimeUnit maxUnit) {
        return new DuringPredicate(max, maxUnit);
    }

    public static TemporalPredicate during(long min, TimeUnit minUnit, long max, TimeUnit maxUnit) {
        return new DuringPredicate(min, minUnit, max, maxUnit);
    }

    public static TemporalPredicate finishedby() {
        return new FinishedbyPredicate();
    }

    public static TemporalPredicate finishedby( long dev, TimeUnit devUnit) {
        return new FinishedbyPredicate(dev, devUnit );
    }

    public static TemporalPredicate finishes() {
        return new FinishesPredicate();
    }

    public static TemporalPredicate finishes( long dev, TimeUnit devUnit) {
        return new FinishesPredicate(dev, devUnit);
    }

    public static TemporalPredicate includes() {
        return new IncludesPredicate();
    }

    public static TemporalPredicate includes(long max, TimeUnit maxUnit) {
        return new IncludesPredicate(max, maxUnit);
    }

    public static TemporalPredicate includes(long min, TimeUnit minUnit, long max, TimeUnit maxUnit) {
        return new IncludesPredicate(min, minUnit, max, maxUnit);
    }

    public static TemporalPredicate metby() {
        return new MetbyPredicate();
    }

    public static TemporalPredicate metby( long dev, TimeUnit devUnit ) {
        return new MetbyPredicate(dev, devUnit );
    }

    public static TemporalPredicate meets() {
        return new MeetsPredicate();
    }

    public static TemporalPredicate meets( long dev, TimeUnit devUnit ) {
        return new MeetsPredicate(dev, devUnit );
    }

    public static TemporalPredicate overlappedby() {
        return new OverlappedbyPredicate();
    }

    public static TemporalPredicate overlappedby( long dev, TimeUnit devUnit ) {
        return new OverlappedbyPredicate(dev, devUnit);
    }

    public static TemporalPredicate overlappedby( long minDev, TimeUnit minDevTimeUnit, long maxDev, TimeUnit maxDevTimeUnit ) {
        return new OverlappedbyPredicate(minDev, minDevTimeUnit, maxDev, maxDevTimeUnit);
    }

    public static TemporalPredicate overlaps() {
        return new OverlapsPredicate();
    }

    public static TemporalPredicate overlaps( long maxDev, TimeUnit maxDevTimeUnit ) {
        return new OverlapsPredicate(maxDev, maxDevTimeUnit);
    }

    public static TemporalPredicate overlaps( long minDev, TimeUnit minDevTimeUnit, long maxDev, TimeUnit maxDevTimeUnit ) {
        return new OverlapsPredicate(minDev, minDevTimeUnit, maxDev, maxDevTimeUnit);
    }

    public static TemporalPredicate startedby() {
        return new StartedbyPredicate();
    }

    public static TemporalPredicate startedby( long dev, TimeUnit devUnit) {
        return new StartedbyPredicate(dev, devUnit);
    }

    public static TemporalPredicate starts() {
        return new StartsPredicate();
    }

    public static TemporalPredicate starts( long dev, TimeUnit devUnit) {
        return new StartsPredicate(dev, devUnit);
    }

    // -- RHS --

    public static ConsequenceBuilder._0 execute( Block0 block) {
        return new ConsequenceBuilder._0(block);
    }

    public static ConsequenceBuilder._0 execute(Block1<Drools> block) {
        return new ConsequenceBuilder._0(block);
    }

    public static ConsequenceBuilder._0 executeScript(String language, Class<?> ruleClass, String script) {
        return new ConsequenceBuilder._0(language, ruleClass, script);
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

    public static <A, B, C, D> ConsequenceBuilder._4<A, B, C, D> on(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4) {
        return new ConsequenceBuilder._4(decl1, decl2, decl3, decl4);
    }

    public static <A, B, C, D, E> ConsequenceBuilder._5<A, B, C, D, E> on(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5) {
        return new ConsequenceBuilder._5(decl1, decl2, decl3, decl4, decl5);
    }

    public static <A, B, C, D, E, F> ConsequenceBuilder._6<A, B, C, D, E, F> on(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6) {
        return new ConsequenceBuilder._6(decl1, decl2, decl3, decl4, decl5, decl6);
    }

    public static <A, B, C, D, E, F, G> ConsequenceBuilder._7<A, B, C, D, E, F, G> on(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                                                                                      Variable<G> decl7) {
        return new ConsequenceBuilder._7(decl1, decl2, decl3, decl4, decl5, decl6, decl7);
    }

    public static <A, B, C, D, E, F, G, H> ConsequenceBuilder._8<A, B, C, D, E, F, G, H> on(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                                                                                            Variable<G> decl7, Variable<H> decl8) {
        return new ConsequenceBuilder._8(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8);
    }

    public static <A, B, C, D, E, F, G, H, I> ConsequenceBuilder._9<A, B, C, D, E, F, G, H, I> on(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                                                                                                  Variable<G> decl7, Variable<H> decl8, Variable<I> decl9) {
        return new ConsequenceBuilder._9(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8, decl9);
    }

    public static <A, B, C, D, E, F, G, H, I, J> ConsequenceBuilder._10<A, B, C, D, E, F, G, H, I, J> on(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                                                                                                         Variable<G> decl7, Variable<H> decl8, Variable<I> decl9, Variable<J> decl10) {
        return new ConsequenceBuilder._10(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8, decl9, decl10);
    }

    public static <A, B, C, D, E, F, G, H, I, J, K> ConsequenceBuilder._11<A, B, C, D, E, F, G, H, I, J, K> on(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                                                                                                               Variable<G> decl7, Variable<H> decl8, Variable<I> decl9, Variable<J> decl10, Variable<K> decl11) {
        return new ConsequenceBuilder._11(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8, decl9, decl10, decl11);
    }

    public static <A, B, C, D, E, F, G, H, I, J, K, L> ConsequenceBuilder._12<A, B, C, D, E, F, G, H, I, J, K, L> on(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                                                                                                                     Variable<G> decl7, Variable<H> decl8, Variable<I> decl9, Variable<J> decl10, Variable<K> decl11, Variable<L> decl12) {
        return new ConsequenceBuilder._12(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8, decl9, decl10, decl11, decl12);
    }

    public static ConsequenceBuilder._N on(Variable... declarations) {
        return new ConsequenceBuilder._N(declarations);
    }

    public static <T> Value<T> valueOf(T value) {
        return new ValueImpl<>( value );
    }


    public static boolean eval( String op, Object obj, Object... args ) {
        return eval(Operator.Register.getOperator(op ), obj, args );
    }

    public static boolean eval( Operator op, Object obj, Object... args ) {
        try {
            return op.test( obj, args );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    public static <A, R> DynamicValueSupplier<R> supply( Variable<A> var1, Function1<A, R> f ) {
        return new DynamicValueSupplier._1( var1, f );
    }

    public static <A, B, R> DynamicValueSupplier<R> supply( Variable<A> var1, Variable<B> var2, Function2<A, B, R> f ) {
        return new DynamicValueSupplier._2( var1, var2, f );
    }
}
