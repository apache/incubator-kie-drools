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

import java.util.ArrayList;
import java.util.List;

import org.drools.model.consequences.ConditionalConsequenceBuilder;
import org.drools.model.constraints.FixedTemporalConstraint;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.constraints.SingleConstraint10;
import org.drools.model.constraints.SingleConstraint11;
import org.drools.model.constraints.SingleConstraint12;
import org.drools.model.constraints.SingleConstraint13;
import org.drools.model.constraints.SingleConstraint2;
import org.drools.model.constraints.SingleConstraint3;
import org.drools.model.constraints.SingleConstraint4;
import org.drools.model.constraints.SingleConstraint5;
import org.drools.model.constraints.SingleConstraint6;
import org.drools.model.constraints.SingleConstraint7;
import org.drools.model.constraints.SingleConstraint8;
import org.drools.model.constraints.SingleConstraint9;
import org.drools.model.constraints.VariableTemporalConstraint;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
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
import org.drools.model.functions.temporal.TemporalPredicate;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.impl.Query0DefImpl;
import org.drools.model.impl.Query1DefImpl;
import org.drools.model.impl.Query2DefImpl;
import org.drools.model.impl.Query3DefImpl;
import org.drools.model.impl.Query4DefImpl;
import org.drools.model.impl.RuleBuilder;
import org.drools.model.impl.ViewBuilder;
import org.drools.model.index.AlphaIndexImpl;
import org.drools.model.index.BetaIndexImpl;
import org.drools.model.view.BindViewItem1;
import org.drools.model.view.BindViewItem2;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.ViewItem;

import static java.util.UUID.randomUUID;

public class PatternDSL extends DSL {

    private static final ViewBuilder VIEW_BUILDER = ViewBuilder.PATTERN;

    public static <T> PatternDef<T> pattern(Variable<T> var) {
        return new PatternDefImpl<>( var );
    }

    public static <T> PatternDef<T> pattern(Variable<T> var, DeclarationSource source) {
        (( DeclarationImpl<T> ) var).setSource( source );
        return new PatternDefImpl<>( var );
    }

    public static <T, U> AlphaIndex<T, U> alphaIndexedBy( Class<U> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, U> leftOperandExtractor, U rightValue ) {
        return new AlphaIndexImpl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightValue );
    }

    public static <T, U, V> BetaIndex<T, U, V> betaIndexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, V> leftOperandExtractor, Function1<U, V> rightOperandExtractor ) {
        return new BetaIndexImpl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor );
    }

    public static ReactOn reactOn(String... reactOn) {
        return new ReactOn( reactOn );
    }

    public interface PatternDef<T> extends ViewItem<T> {
        PatternDef<T> expr( Predicate1<T> predicate );
        PatternDef<T> expr( String exprId, Predicate1<T> predicate );
        PatternDef<T> expr( String exprId, Predicate1<T> predicate, AlphaIndex<T, ?> index );
        PatternDef<T> expr( String exprId, Predicate1<T> predicate, ReactOn reactOn );
        PatternDef<T> expr( String exprId, Predicate1<T> predicate, AlphaIndex<T, ?> index, ReactOn reactOn );

        <U> PatternDef<T> expr( Variable<U> var2, Predicate2<T, U> predicate );
        <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate );
        <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, BetaIndex<T, U, ?> index );
        <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, ReactOn reactOn );
        <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, BetaIndex<T, U, ?> index, ReactOn reactOn );

        <A, B> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate );
        <A, B> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate, ReactOn reactOn );

        <A, B, C> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate );
        <A, B, C> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate, ReactOn reactOn );

        <A, B, C, D> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate );
        <A, B, C, D> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate, ReactOn reactOn );

        <A, B, C, D, E> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Predicate6<T, A, B, C, D, E> predicate );
        <A, B, C, D, E> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Predicate6<T, A, B, C, D, E> predicate, ReactOn reactOn );

        <A, B, C, D, E, F> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7, Predicate7<T, A, B, C, D, E, F> predicate );
        <A, B, C, D, E, F> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7, Predicate7<T, A, B, C, D, E, F> predicate, ReactOn reactOn );

        <A, B, C, D, E, F, G> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                    Variable<G> var8,
                                                    Predicate8<T, A, B, C, D, E, F, G> predicate );

        <A, B, C, D, E, F, G> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                    Variable<G> var8,
                                                    Predicate8<T, A, B, C, D, E, F, G> predicate, ReactOn reactOn );

        <A, B, C, D, E, F, G, H> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                       Variable<G> var8, Variable<H> var9,
                                                       Predicate9<T, A, B, C, D, E, F, G, H> predicate );

        <A, B, C, D, E, F, G, H> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                       Variable<G> var8, Variable<H> var9,
                                                       Predicate9<T, A, B, C, D, E, F, G, H> predicate, ReactOn reactOn );

        <A, B, C, D, E, F, G, H, I> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                          Variable<G> var8, Variable<H> var9, Variable<I> var10,
                                                          Predicate10<T, A, B, C, D, E, F, G, H, I> predicate );

        <A, B, C, D, E, F, G, H, I> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                          Variable<G> var8, Variable<H> var9, Variable<I> var10,
                                                          Predicate10<T, A, B, C, D, E, F, G, H, I> predicate, ReactOn reactOn );

        <A, B, C, D, E, F, G, H, I, J> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                             Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11,
                                                             Predicate11<T, A, B, C, D, E, F, G, H, I, J> predicate );

        <A, B, C, D, E, F, G, H, I, J> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                             Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11,
                                                             Predicate11<T, A, B, C, D, E, F, G, H, I, J> predicate, ReactOn reactOn );

        <A, B, C, D, E, F, G, H, I, J, K> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12,
                                                                Predicate12<T, A, B, C, D, E, F, G, H, I, J, K> predicate );

        <A, B, C, D, E, F, G, H, I, J, K> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12,
                                                                Predicate12<T, A, B, C, D, E, F, G, H, I, J, K> predicate, ReactOn reactOn );

        <A, B, C, D, E, F, G, H, I, J, K, L> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12, Variable<L> var13,
                                                                Predicate13<T, A, B, C, D, E, F, G, H, I, J, K, L> predicate );

        <A, B, C, D, E, F, G, H, I, J, K, L> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12, Variable<L> var13,
                                                                Predicate13<T, A, B, C, D, E, F, G, H, I, J, K, L> predicate, ReactOn reactOn );

        <U> PatternDef<T> expr( String exprId, Variable<U> var1, TemporalPredicate temporalPredicate );
        PatternDef<T> expr( String exprId, long value, TemporalPredicate temporalPredicate );
        PatternDef<T> expr( String exprId, Function1<T, ?> f, long value, TemporalPredicate temporalPredicate );

        <U> PatternDef<T> expr( String exprId, Variable<U> var1, Function1<U,?> fVar, TemporalPredicate temporalPredicate );
        <U> PatternDef<T> expr( String exprId, Function1<T,?> fThis, Variable<U> var1, TemporalPredicate temporalPredicate );
        <U> PatternDef<T> expr( String exprId, Function1<T,?> fThis, Variable<U> var1, Function1<U,?> fVar, TemporalPredicate temporalPredicate );

        <A> PatternDef<T> bind( Variable<A> boundVar, Function1<T, A> f );
        <A> PatternDef<T> bind( Variable<A> boundVar, Function1<T, A> f, ReactOn reactOn );

        <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function1<U, A> f );
        <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function1<U, A> f, ReactOn reactOn );

        <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f );
        <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f, ReactOn reactOn );

        PatternDef<T> watch(String... watch);
    }

    public static class PatternDefImpl<T> implements PatternDef<T> {
        private final Variable<T> variable;
        private final List<PatternItem<T>> items = new ArrayList<>();

        private String[] watch;

        public PatternDefImpl( Variable<T> variable ) {
            this.variable = variable;
        }

        @Override
        public Variable<T> getFirstVariable() {
            return variable;
        }

        public List<PatternItem<T>> getItems() {
            return items;
        }

        public String[] getWatch() {
            return watch;
        }

        @Override
        public Variable<?>[] getVariables() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PatternDef<T> expr( Predicate1<T> predicate ) {
            items.add( new PatternExpr1<>( new Predicate1.Impl<>(predicate), null, null ) );
            return this;
        }

        @Override
        public PatternDef<T> expr( String exprId, Predicate1<T> predicate ) {
            items.add( new PatternExpr1<>( exprId, new Predicate1.Impl<>(predicate), null, null ) );
            return this;
        }

        @Override
        public PatternDef<T> expr( String exprId, Predicate1<T> predicate, AlphaIndex<T, ?> index ) {
            items.add( new PatternExpr1<>( exprId, new Predicate1.Impl<>(predicate), index, null ) );
            return this;
        }

        @Override
        public PatternDef<T> expr( String exprId, Predicate1<T> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr1<>( exprId, new Predicate1.Impl<>(predicate), null, reactOn ) );
            return this;
        }

        @Override
        public PatternDef<T> expr( String exprId, Predicate1<T> predicate, AlphaIndex<T, ?> index, ReactOn reactOn ) {
            items.add( new PatternExpr1<>( exprId, new Predicate1.Impl<>(predicate), index, reactOn ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( Variable<U> var2, Predicate2<T, U> predicate ) {
            items.add( new PatternExpr2<>( var2, new Predicate2.Impl<>(predicate), null, null ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate ) {
            items.add( new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>(predicate), null, null ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, BetaIndex<T, U, ?> index ) {
            items.add( new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>(predicate), index, null ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>(predicate), null, reactOn ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, BetaIndex<T, U, ?> index, ReactOn reactOn ) {
            items.add( new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>(predicate), index, reactOn ) );
            return this;
        }

        @Override
        public <A, B> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate ) {
            items.add( new PatternExpr3<>( exprId, var2, var3, new Predicate3.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr3<>( exprId, var2, var3, new Predicate3.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate ) {
            items.add( new PatternExpr4<>( exprId, var2, var3, var4, new Predicate4.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B, C> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr4<>( exprId, var2, var3, var4, new Predicate4.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C, D> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate ) {
            items.add( new PatternExpr5<>( exprId, var2, var3, var4, var5, new Predicate5.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B, C, D> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr5<>( exprId, var2, var3, var4, var5, new Predicate5.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C, D, E> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Predicate6<T, A, B, C, D, E> predicate) {
            items.add( new PatternExpr6<>( exprId, var2, var3, var4, var5, var6, new Predicate6.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B, C, D, E> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Predicate6<T, A, B, C, D, E> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr6<>( exprId, var2, var3, var4, var5, var6, new Predicate6.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7, Predicate7<T, A, B, C, D, E, F> predicate) {
            items.add( new PatternExpr7<>( exprId, var2, var3, var4, var5, var6, var7, new Predicate7.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7, Predicate7<T, A, B, C, D, E, F> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr7<>( exprId, var2, var3, var4, var5, var6, var7, new Predicate7.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                           Variable<G> var8,
                                                           Predicate8<T, A, B, C, D, E, F, G> predicate ) {
            items.add( new PatternExpr8<>( exprId, var2, var3, var4, var5, var6, var7, var8, new Predicate8.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                           Variable<G> var8,
                                                           Predicate8<T, A, B, C, D, E, F, G> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr8<>( exprId, var2, var3, var4, var5, var6, var7, var8, new Predicate8.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G, H> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                              Variable<G> var8, Variable<H> var9,
                                                              Predicate9<T, A, B, C, D, E, F, G, H> predicate ) {
            items.add( new PatternExpr9<>( exprId, var2, var3, var4, var5, var6, var7, var8, var9, new Predicate9.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G, H> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                              Variable<G> var8, Variable<H> var9,
                                                              Predicate9<T, A, B, C, D, E, F, G, H> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr9<>( exprId, var2, var3, var4, var5, var6, var7, var8, var9, new Predicate9.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G, H, I> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                 Variable<G> var8, Variable<H> var9, Variable<I> var10,
                                                                 Predicate10<T, A, B, C, D, E, F, G, H, I> predicate ) {
            items.add( new PatternExpr10<>( exprId, var2, var3, var4, var5, var6, var7, var8, var9, var10, new Predicate10.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G, H, I> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                 Variable<G> var8, Variable<H> var9, Variable<I> var10,
                                                                 Predicate10<T, A, B, C, D, E, F, G, H, I> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr10<>( exprId, var2, var3, var4, var5, var6, var7, var8, var9, var10, new Predicate10.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G, H, I, J> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                    Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11,
                                                                    Predicate11<T, A, B, C, D, E, F, G, H, I, J> predicate ) {
            items.add( new PatternExpr11<>( exprId, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, new Predicate11.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G, H, I, J> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                    Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11,
                                                                    Predicate11<T, A, B, C, D, E, F, G, H, I, J> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr11<>( exprId, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, new Predicate11.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G, H, I, J, K> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                       Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12,
                                                                       Predicate12<T, A, B, C, D, E, F, G, H, I, J, K> predicate ) {
            items.add( new PatternExpr12<>( exprId, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, new Predicate12.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G, H, I, J, K> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                       Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12,
                                                                       Predicate12<T, A, B, C, D, E, F, G, H, I, J, K> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr12<>( exprId, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, new Predicate12.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G, H, I, J, K, L> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                       Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12, Variable<L> var13,
                                                                       Predicate13<T, A, B, C, D, E, F, G, H, I, J, K, L> predicate ) {
            items.add( new PatternExpr13<>( exprId, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, new Predicate13.Impl<>(predicate), null ) );
            return this;
        }

        @Override
        public <A, B, C, D, E, F, G, H, I, J, K, L> PatternDef<T> expr(String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                                                                       Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12, Variable<L> var13,
                                                                       Predicate13<T, A, B, C, D, E, F, G, H, I, J, K, L> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr13<>( exprId, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, new Predicate13.Impl<>(predicate), reactOn ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Variable<U> var1, TemporalPredicate temporalPredicate ) {
            items.add( new TemporalPatternExpr<>( exprId, null, var1, null, temporalPredicate) );
            return this;
        }

        @Override
        public PatternDef<T> expr( String exprId, long value, TemporalPredicate temporalPredicate ) {
            items.add( new FixedTemporalPatternExpr<>( exprId, null, value, temporalPredicate) );
            return this;
        }

        @Override
        public PatternDef<T> expr( String exprId, Function1<T, ?> f, long value, TemporalPredicate temporalPredicate ) {
            items.add( new FixedTemporalPatternExpr<>( exprId, new Function1.Impl<>( f ), value, temporalPredicate) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Variable<U> var1, Function1<U, ?> fVar, TemporalPredicate temporalPredicate ) {
            items.add( new TemporalPatternExpr<>( exprId, null, var1, new Function1.Impl<>( fVar ), temporalPredicate) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Function1<T, ?> fThis, Variable<U> var1, TemporalPredicate temporalPredicate ) {
            items.add( new TemporalPatternExpr<>( exprId, new Function1.Impl<>( fThis ), var1, null, temporalPredicate) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Function1<T, ?> fThis, Variable<U> var1, Function1<U, ?> fVar, TemporalPredicate temporalPredicate ) {
            items.add( new TemporalPatternExpr<>( exprId, new Function1.Impl<>( fThis ), var1, new Function1.Impl<>( fVar ), temporalPredicate) );
            return this;
        }

        @Override
        public <A> PatternDef<T> bind( Variable<A> boundVar, Function1<T, A> f ) {
            items.add( new PatternBinding1<>(boundVar, f, null) );
            return this;
        }

        @Override
        public <A> PatternDef<T> bind( Variable<A> boundVar, Function1<T, A> f, ReactOn reactOn ) {
            items.add( new PatternBinding1<>(boundVar, f, reactOn) );
            return this;
        }

        @Override
        public <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function1<U, A> f ) {
            items.add( new PatternBinding2<>(boundVar, otherVar, (t,u) -> f.apply( u ), null) );
            return this;
        }

        @Override
        public <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function1<U, A> f, ReactOn reactOn ) {
            items.add( new PatternBinding2<>(boundVar, otherVar, (t,u) -> f.apply( u ), reactOn) );
            return this;
        }

        @Override
        public <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f ) {
            items.add( new PatternBinding2<>(boundVar, otherVar, f, null) );
            return this;
        }

        @Override
        public <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f, ReactOn reactOn ) {
            items.add( new PatternBinding2<>(boundVar, otherVar, f, reactOn) );
            return this;
        }

        @Override
        public PatternDef<T> watch(String... watch) {
            this.watch = watch;
            return this;
        }
    }

    public interface PatternItem<T> { }

    public static abstract class PatternExprImpl<T> implements PatternItem<T>  {
        private final String exprId;
        private final ReactOn reactOn;

        public PatternExprImpl( String exprId, ReactOn reactOn ) {
            this.exprId = exprId;
            this.reactOn = reactOn;
        }

        public String getExprId() {
            return exprId;
        }

        public String[] getReactOn() {
            return reactOn != null ? reactOn.getStrings() : null;
        }

        public abstract Constraint asConstraint( PatternDefImpl patternDef );
    }

    public static class PatternExpr1<T> extends PatternExprImpl<T> {

        private final Predicate1<T> predicate;

        private final AlphaIndex<T, ?> index;

        public PatternExpr1(Predicate1<T> predicate, AlphaIndex<T, ?> index, ReactOn reactOn) {
            this(randomUUID().toString(), predicate, index, reactOn);
        }

        public PatternExpr1( String exprId, Predicate1<T> predicate, AlphaIndex<T, ?> index, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.predicate = predicate;
            this.index = index;
        }

        public Predicate1<T> getPredicate() {
            return predicate;
        }

        public AlphaIndex<T, ?> getIndex() {
            return index;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint1 constraint = new SingleConstraint1(getExprId(), patternDef.getFirstVariable(), getPredicate());
            constraint.setIndex( getIndex() );
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr2<T, U> extends PatternExprImpl<T> {
        private final Variable<U> var2;
        private final Predicate2<T, U> predicate;

        private final BetaIndex<T, U, ?> index;

        public PatternExpr2(Variable<U> var2, Predicate2<T, U> predicate, BetaIndexImpl<T, U, ?> index, ReactOn reactOn) {
            this(randomUUID().toString(), var2, predicate, index, reactOn);
        }

        public PatternExpr2( String exprId, Variable<U> var2, Predicate2<T, U> predicate, BetaIndex<T, U, ?> index, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.predicate = predicate;
            this.index = index;
        }

        public Predicate2<T, U> getPredicate() {
            return predicate;
        }

        public BetaIndex<T, U, ?> getIndex() {
            return index;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint2 constraint = new SingleConstraint2(getExprId(), patternDef.getFirstVariable(), var2, getPredicate());
            constraint.setIndex( getIndex() );
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr3<T, A, B> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Predicate3<T, A, B> predicate;

        public PatternExpr3(Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, predicate, reactOn);
        }

        public PatternExpr3( String exprId, Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.var3 = var3;
            this.predicate = predicate;
        }

        public Predicate3<T, A, B> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint3 constraint = new SingleConstraint3(getExprId(), patternDef.getFirstVariable(), var2, var3, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr4<T, A, B, C> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Predicate4<T, A, B, C> predicate;

        public PatternExpr4(Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, var4, predicate, reactOn);
        }

        public PatternExpr4( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.var3 = var3;
            this.var4 = var4;
            this.predicate = predicate;
        }

        public Predicate4<T, A, B, C> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint4 constraint = new SingleConstraint4(getExprId(), patternDef.getFirstVariable(), var2, var3, var4, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr5<T, A, B, C, D> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Variable<D> var5;
        private final Predicate5<T, A, B, C, D> predicate;

        public PatternExpr5(Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, var4, var5, predicate, reactOn);
        }

        public PatternExpr5( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.var3 = var3;
            this.var4 = var4;
            this.var5 = var5;
            this.predicate = predicate;
        }

        public Predicate5<T, A, B, C, D> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint5 constraint = new SingleConstraint5(getExprId(), patternDef.getFirstVariable(), var2, var3, var4, var5, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr6<T, A, B, C, D, E> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Variable<D> var5;
        private final Variable<E> var6;
        private final Predicate6<T, A, B, C, D, E> predicate;

        public PatternExpr6(Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Predicate6<T, A, B, C, D, E> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, var4, var5, var6, predicate, reactOn);
        }

        public PatternExpr6( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Predicate6<T, A, B, C, D, E> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.var3 = var3;
            this.var4 = var4;
            this.var5 = var5;
            this.var6 = var6;
            this.predicate = predicate;
        }

        public Predicate6<T, A, B, C, D, E> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint6 constraint = new SingleConstraint6(getExprId(), patternDef.getFirstVariable(), var2, var3, var4, var5, var6, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr7<T, A, B, C, D, E, F> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Variable<D> var5;
        private final Variable<E> var6;
        private final Variable<F> var7;
        private final Predicate7<T, A, B, C, D, E, F> predicate;

        public PatternExpr7(Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7, Predicate7<T, A, B, C, D, E, F> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, var4, var5, var6, var7, predicate, reactOn);
        }

        public PatternExpr7( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7, Predicate7<T, A, B, C, D, E, F> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.var3 = var3;
            this.var4 = var4;
            this.var5 = var5;
            this.var6 = var6;
            this.var7 = var7;
            this.predicate = predicate;
        }

        public Predicate7<T, A, B, C, D, E, F> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint7 constraint = new SingleConstraint7(getExprId(), patternDef.getFirstVariable(), var2, var3, var4, var5, var6, var7, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr8<T, A, B, C, D, E, F, G, H> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Variable<D> var5;
        private final Variable<E> var6;
        private final Variable<F> var7;
        private final Variable<G> var8;
        private final Predicate8<T, A, B, C, D, E, F, G> predicate;

        public PatternExpr8( Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                              Variable<G> var8,
                              Predicate8<T, A, B, C, D, E, F, G> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, var4, var5, var6, var7, var8, predicate, reactOn);
        }

        public PatternExpr8( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                             Variable<G> var8,
                             Predicate8<T, A, B, C, D, E, F, G> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.var3 = var3;
            this.var4 = var4;
            this.var5 = var5;
            this.var6 = var6;
            this.var7 = var7;
            this.var8 = var8;
            this.predicate = predicate;
        }

        public Predicate8<T, A, B, C, D, E, F, G> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint8 constraint = new SingleConstraint8(getExprId(), patternDef.getFirstVariable(), var2, var3, var4, var5, var6, var7, var8, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr9<T, A, B, C, D, E, F, G, H> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Variable<D> var5;
        private final Variable<E> var6;
        private final Variable<F> var7;
        private final Variable<G> var8;
        private final Variable<H> var9;
        private final Predicate9<T, A, B, C, D, E, F, G, H> predicate;

        public PatternExpr9( Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                              Variable<G> var8, Variable<H> var9,
                              Predicate9<T, A, B, C, D, E, F, G, H> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, var4, var5, var6, var7, var8, var9, predicate, reactOn);
        }

        public PatternExpr9( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                             Variable<G> var8, Variable<H> var9,
                             Predicate9<T, A, B, C, D, E, F, G, H> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.var3 = var3;
            this.var4 = var4;
            this.var5 = var5;
            this.var6 = var6;
            this.var7 = var7;
            this.var8 = var8;
            this.var9 = var9;
            this.predicate = predicate;
        }

        public Predicate9<T, A, B, C, D, E, F, G, H> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint9 constraint = new SingleConstraint9(getExprId(), patternDef.getFirstVariable(), var2, var3, var4, var5, var6, var7, var8, var9, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr10<T, A, B, C, D, E, F, G, H, I> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Variable<D> var5;
        private final Variable<E> var6;
        private final Variable<F> var7;
        private final Variable<G> var8;
        private final Variable<H> var9;
        private final Variable<I> var10;
        private final Predicate10<T, A, B, C, D, E, F, G, H, I> predicate;

        public PatternExpr10( Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                              Variable<G> var8, Variable<H> var9, Variable<I> var10,
                              Predicate10<T, A, B, C, D, E, F, G, H, I> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, var4, var5, var6, var7, var8, var9, var10, predicate, reactOn);
        }

        public PatternExpr10( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                             Variable<G> var8, Variable<H> var9, Variable<I> var10,
                             Predicate10<T, A, B, C, D, E, F, G, H, I> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
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

        public Predicate10<T, A, B, C, D, E, F, G, H, I> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint10 constraint = new SingleConstraint10(getExprId(), patternDef.getFirstVariable(), var2, var3, var4, var5, var6, var7, var8, var9, var10, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr11<T, A, B, C, D, E, F, G, H, I, J> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Variable<D> var5;
        private final Variable<E> var6;
        private final Variable<F> var7;
        private final Variable<G> var8;
        private final Variable<H> var9;
        private final Variable<I> var10;
        private final Variable<J> var11;
        private final Predicate11<T, A, B, C, D, E, F, G, H, I, J> predicate;

        public PatternExpr11(Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                             Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11,
                             Predicate11<T, A, B, C, D, E, F, G, H, I, J> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, predicate, reactOn);
        }

        public PatternExpr11( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                             Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11,
                             Predicate11<T, A, B, C, D, E, F, G, H, I, J> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.var3 = var3;
            this.var4 = var4;
            this.var5 = var5;
            this.var6 = var6;
            this.var7 = var7;
            this.var8 = var8;
            this.var9 = var9;
            this.var10 = var10;
            this.var11 = var11;
            this.predicate = predicate;
        }

        public Predicate11<T, A, B, C, D, E, F, G, H, I, J> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint11 constraint = new SingleConstraint11(getExprId(), patternDef.getFirstVariable(), var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr12<T, A, B, C, D, E, F, G, H, I, J, K> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Variable<D> var5;
        private final Variable<E> var6;
        private final Variable<F> var7;
        private final Variable<G> var8;
        private final Variable<H> var9;
        private final Variable<I> var10;
        private final Variable<J> var11;
        private final Variable<K> var12;
        private final Predicate12<T, A, B, C, D, E, F, G, H, I, J, K> predicate;

        public PatternExpr12(Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                             Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12,
                             Predicate12<T, A, B, C, D, E, F, G, H, I, J, K> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, predicate, reactOn);
        }

        public PatternExpr12( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                             Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12,
                             Predicate12<T, A, B, C, D, E, F, G, H, I, J, K> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.var3 = var3;
            this.var4 = var4;
            this.var5 = var5;
            this.var6 = var6;
            this.var7 = var7;
            this.var8 = var8;
            this.var9 = var9;
            this.var10 = var10;
            this.var11 = var11;
            this.var12 = var12;
            this.predicate = predicate;
        }

        public Predicate12<T, A, B, C, D, E, F, G, H, I, J, K> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint12 constraint = new SingleConstraint12(getExprId(), patternDef.getFirstVariable(), var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr13<T, A, B, C, D, E, F, G, H, I, J, K, L> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Variable<D> var5;
        private final Variable<E> var6;
        private final Variable<F> var7;
        private final Variable<G> var8;
        private final Variable<H> var9;
        private final Variable<I> var10;
        private final Variable<J> var11;
        private final Variable<K> var12;
        private final Variable<L> var13;
        private final Predicate13<T, A, B, C, D, E, F, G, H, I, J, K, L> predicate;

        public PatternExpr13(Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                             Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12, Variable<L> var13,
                             Predicate13<T, A, B, C, D, E, F, G, H, I, J, K, L> predicate, ReactOn reactOn) {
            this(randomUUID().toString(), var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, predicate, reactOn);
        }

        public PatternExpr13( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Variable<E> var6, Variable<F> var7,
                             Variable<G> var8, Variable<H> var9, Variable<I> var10, Variable<J> var11, Variable<K> var12, Variable<L> var13,
                             Predicate13<T, A, B, C, D, E, F, G, H, I, J, K, L> predicate, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.var3 = var3;
            this.var4 = var4;
            this.var5 = var5;
            this.var6 = var6;
            this.var7 = var7;
            this.var8 = var8;
            this.var9 = var9;
            this.var10 = var10;
            this.var11 = var11;
            this.var12 = var12;
            this.var13 = var13;
            this.predicate = predicate;
        }

        public Predicate13<T, A, B, C, D, E, F, G, H, I, J, K, L> getPredicate() {
            return predicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            SingleConstraint13 constraint = new SingleConstraint13(getExprId(), patternDef.getFirstVariable(), var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, getPredicate());
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class TemporalPatternExpr<T, U> extends PatternExprImpl<T> {
        private final Function1<T, ?> fThis;
        private final Function1<U, ?> fVar;
        private final Variable<U> var1;
        private final TemporalPredicate temporalPredicate;

        public TemporalPatternExpr(Variable<U> var2, TemporalPredicate temporalPredicate) {
            this(randomUUID().toString(), null, var2, null, temporalPredicate);
        }

        public TemporalPatternExpr( String exprId, Function1<T, ?> fThis, Variable<U> var1, Function1<U, ?> fVar, TemporalPredicate temporalPredicate) {
            super( exprId, null );
            this.fThis = fThis;
            this.var1 = var1;
            this.fVar = fVar;
            this.temporalPredicate = temporalPredicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            return new VariableTemporalConstraint(getExprId(), patternDef.getFirstVariable(), fThis, var1, fVar, temporalPredicate);
        }
    }

    public static class FixedTemporalPatternExpr<T> extends PatternExprImpl<T> {
        private final Function1<?,?> func;
        private final long value;
        private final TemporalPredicate temporalPredicate;

        public FixedTemporalPatternExpr(Function1<?,?> func, long value, TemporalPredicate temporalPredicate) {
            this(randomUUID().toString(), func, value, temporalPredicate);
        }

        public FixedTemporalPatternExpr( String exprId, Function1<?,?> func, long value, TemporalPredicate temporalPredicate) {
            super( exprId, null );
            this.func = func;
            this.value = value;
            this.temporalPredicate = temporalPredicate;
        }

        @Override
        public Constraint asConstraint(PatternDefImpl patternDef) {
            return new FixedTemporalConstraint(getExprId(), patternDef.getFirstVariable(), func, value, temporalPredicate);
        }
    }

    public static class ReactOn {
        public final String[] strings;

        public ReactOn( String... strings ) {
            this.strings = strings;
        }

        public String[] getStrings() {
            return strings;
        }
    }

    public static abstract class PatternBindingImpl<T, A> implements PatternItem<T> {
        protected final Variable<A> boundVar;
        protected final ReactOn reactOn;

        public PatternBindingImpl( Variable<A> boundVar, ReactOn reactOn ) {
            this.boundVar = boundVar;
            this.reactOn = reactOn;
        }

        public String[] getReactOn() {
            return reactOn != null ? reactOn.getStrings() : new String[0];
        }

        public abstract Binding asBinding( PatternDefImpl patternDef );
    }

    public static class PatternBinding1<T, A> extends PatternBindingImpl<T, A> {
        private final Function1<T, A> f;

        public PatternBinding1( Variable<A> boundVar, Function1<T, A> f, ReactOn reactOn ) {
            super( boundVar, reactOn );
            this.f = new Function1.Impl<>(f);
        }

        @Override
        public Binding asBinding( PatternDefImpl patternDef ) {
            return new BindViewItem1(boundVar, f, patternDef.getFirstVariable(), getReactOn(), null);
        }
    }

    public static class PatternBinding2<T, U, A> extends PatternBindingImpl<T, A> {
        private final Variable<U> otherVar;
        private final Function2<T, U, A> f;

        public PatternBinding2( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f, ReactOn reactOn ) {
            super( boundVar, reactOn );
            this.f = new Function2.Impl<>(f);
            this.otherVar = otherVar;
        }

        @Override
        public Binding asBinding( PatternDefImpl patternDef ) {
            return new BindViewItem2(boundVar, f, patternDef.getFirstVariable(), otherVar, getReactOn(), null);
        }
    }

    // -- Conditional Named Consequnce --

    public static <A> ConditionalConsequenceBuilder when( Variable<A> var, Predicate1<A> predicate) {
        return when( FlowDSL.expr( var, predicate ) );
    }

    public static <A> ConditionalConsequenceBuilder when(String exprId, Variable<A> var, Predicate1<A> predicate) {
        return when( FlowDSL.expr( exprId, var, predicate ) );
    }

    public static <A, B> ConditionalConsequenceBuilder when(Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
        return when( FlowDSL.expr( var1, var2, predicate ) );
    }

    public static <A, B> ConditionalConsequenceBuilder when(String exprId, Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
        return when( FlowDSL.expr( exprId, var1, var2, predicate ) );
    }

    public static ConditionalConsequenceBuilder when(ExprViewItem expr) {
        return new ConditionalConsequenceBuilder( expr );
    }

    // -- rule --

    public static RuleBuilder rule( String name) {
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
    }}
