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
import java.util.stream.Stream;

import org.drools.model.consequences.ConditionalConsequenceBuilder;
import org.drools.model.constraints.AndConstraints;
import org.drools.model.constraints.FixedTemporalConstraint;
import org.drools.model.constraints.OrConstraints;
import org.drools.model.constraints.ReactivitySpecs;
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
import org.drools.model.functions.Function0;
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
import org.drools.model.impl.Exchange;
import org.drools.model.impl.Query0DefImpl;
import org.drools.model.impl.Query10DefImpl;
import org.drools.model.impl.Query1DefImpl;
import org.drools.model.impl.Query2DefImpl;
import org.drools.model.impl.Query3DefImpl;
import org.drools.model.impl.Query4DefImpl;
import org.drools.model.impl.Query5DefImpl;
import org.drools.model.impl.Query6DefImpl;
import org.drools.model.impl.Query7DefImpl;
import org.drools.model.impl.Query8DefImpl;
import org.drools.model.impl.Query9DefImpl;
import org.drools.model.impl.RuleBuilder;
import org.drools.model.impl.ViewBuilder;
import org.drools.model.index.AlphaIndexImpl;
import org.drools.model.index.BetaIndexImpl;
import org.drools.model.view.BindViewItem1;
import org.drools.model.view.BindViewItem2;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.ViewItem;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class PatternDSL extends DSL {

    private static final ViewBuilder VIEW_BUILDER = ViewBuilder.PATTERN;

    public static <T> PatternDef<T> pattern( Variable<T> var ) {
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

    public static ReactOn reactOn( String... reactOn ) {
        return new ReactOn( reactOn );
    }

    public interface PatternDef<T> extends ViewItem<T> {
        PatternDef<T> and();
        PatternDef<T> or();
        PatternDef<T> endAnd();
        PatternDef<T> endOr();

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

        <A, B> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate );

        <A, B> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate, ReactOn reactOn );

        <A, B, C> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate );

        <A, B, C> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate, ReactOn reactOn );

        <A, B, C, D> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate );

        <A, B, C, D> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate, ReactOn reactOn );

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
        PatternDef<T> expr( String exprId, Function1<T,?> fThis, Function1<T,?> fVar, TemporalPredicate temporalPredicate );
        <U> PatternDef<T> expr( String exprId, Function1<T,?> fThis, Variable<U> var1, Function1<U,?> fVar, TemporalPredicate temporalPredicate );

        <A> PatternDef<T> bind( Variable<A> boundVar, Function1<T, A> f );

        <A> PatternDef<T> bind( Variable<A> boundVar, Function1<T, A> f, ReactOn reactOn );

        <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function1<U, A> f );

        <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function1<U, A> f, ReactOn reactOn );

        <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f );

        <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f, ReactOn reactOn );

        PatternDef<T> watch( String... watch );
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

        public DomainClassMetadata getDomainClassMetadata() {
            return variable instanceof Declaration ? (( Declaration<T> ) variable).getMetadata() : null;
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
        public PatternDef<T> and() {
            return new SubPatternDefImpl<>( this, LogicalCombiner.AND );
        }

        @Override
        public PatternDef<T> or() {
            return new SubPatternDefImpl<>( this, LogicalCombiner.OR );
        }

        @Override
        public PatternDef<T> endAnd() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PatternDef<T> endOr() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PatternDef<T> expr( Predicate1<T> predicate ) {
            items.add( new PatternExpr1<>( new Predicate1.Impl<>( predicate ), null, null ) );
            return this;
        }

        @Override
        public PatternDef<T> expr( String exprId, Predicate1<T> predicate ) {
            items.add( new PatternExpr1<>( exprId, new Predicate1.Impl<>( predicate ), null, null ) );
            return this;
        }

        @Override
        public PatternDef<T> expr( String exprId, Predicate1<T> predicate, AlphaIndex<T, ?> index ) {
            items.add( new PatternExpr1<>( exprId, new Predicate1.Impl<>( predicate ), index, null ) );
            return this;
        }

        @Override
        public PatternDef<T> expr( String exprId, Predicate1<T> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr1<>( exprId, new Predicate1.Impl<>( predicate ), null, reactOn ) );
            return this;
        }

        @Override
        public PatternDef<T> expr( String exprId, Predicate1<T> predicate, AlphaIndex<T, ?> index, ReactOn reactOn ) {
            items.add( new PatternExpr1<>( exprId, new Predicate1.Impl<>( predicate ), index, reactOn ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( Variable<U> var2, Predicate2<T, U> predicate ) {
            items.add( new PatternExpr2<>( var2, new Predicate2.Impl<>( predicate ), null, null ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate ) {
            items.add( new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>( predicate ), null, null ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, BetaIndex<T, U, ?> index ) {
            items.add( new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>( predicate ), index, null ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>( predicate ), null, reactOn ) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, BetaIndex<T, U, ?> index, ReactOn reactOn ) {
            items.add( new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>( predicate ), index, reactOn ) );
            return this;
        }

        @Override
        public <A, B> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate ) {
            items.add( new PatternExpr3<>( exprId, var2, var3, new Predicate3.Impl<>( predicate ), null ) );
            return this;
        }

        @Override
        public <A, B> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr3<>( exprId, var2, var3, new Predicate3.Impl<>( predicate ), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate ) {
            items.add( new PatternExpr4<>( exprId, var2, var3, var4, new Predicate4.Impl<>( predicate ), null ) );
            return this;
        }

        @Override
        public <A, B, C> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr4<>( exprId, var2, var3, var4, new Predicate4.Impl<>( predicate ), reactOn ) );
            return this;
        }

        @Override
        public <A, B, C, D> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate ) {
            items.add( new PatternExpr5<>( exprId, var2, var3, var4, var5, new Predicate5.Impl<>( predicate ), null ) );
            return this;
        }

        @Override
        public <A, B, C, D> PatternDef<T> expr( String exprId, Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate, ReactOn reactOn ) {
            items.add( new PatternExpr5<>( exprId, var2, var3, var4, var5, new Predicate5.Impl<>( predicate ), reactOn ) );
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
        public PatternDef<T> expr( String exprId, Function1<T, ?> fThis, Function1<T, ?> fVar, TemporalPredicate temporalPredicate ) {
            items.add( new TemporalPatternExpr<>( exprId, new Function1.Impl<>( fThis ), getFirstVariable(), new Function1.Impl<>( fVar ), temporalPredicate) );
            return this;
        }

        @Override
        public <U> PatternDef<T> expr( String exprId, Function1<T, ?> fThis, Variable<U> var1, Function1<U, ?> fVar, TemporalPredicate temporalPredicate ) {
            items.add( new TemporalPatternExpr<>( exprId, new Function1.Impl<>( fThis ), var1, new Function1.Impl<>( fVar ), temporalPredicate) );
            return this;
        }

        @Override
        public <A> PatternDef<T> bind( Variable<A> boundVar, Function1<T, A> f ) {
            items.add( new PatternBinding1<>( boundVar, f, null ) );
            return this;
        }

        @Override
        public <A> PatternDef<T> bind( Variable<A> boundVar, Function1<T, A> f, ReactOn reactOn ) {
            items.add( new PatternBinding1<>( boundVar, f, reactOn ) );
            return this;
        }

        @Override
        public <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function1<U, A> f ) {
            items.add( new PatternBinding2<>( boundVar, otherVar, ( t, u ) -> f.apply( u ), null ) );
            return this;
        }

        @Override
        public <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function1<U, A> f, ReactOn reactOn ) {
            items.add( new PatternBinding2<>( boundVar, otherVar, ( t, u ) -> f.apply( u ), reactOn ) );
            return this;
        }

        @Override
        public <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f ) {
            items.add( new PatternBinding2<>( boundVar, otherVar, f, null ) );
            return this;
        }

        @Override
        public <A, U> PatternDef<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f, ReactOn reactOn ) {
            items.add( new PatternBinding2<>( boundVar, otherVar, f, reactOn ) );
            return this;
        }

        @Override
        public PatternDef<T> watch( String... watch ) {
            this.watch = watch;
            return this;
        }
    }

    public enum LogicalCombiner {
        AND( "&&" ), OR( "||" );

        private final String symbol;

        LogicalCombiner( String symbol ) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public static class SubPatternDefImpl<T> extends PatternDefImpl<T> {
        private final PatternDefImpl<T> parent;
        private final LogicalCombiner combiner;

        public SubPatternDefImpl( PatternDefImpl<T> parent, LogicalCombiner combiner ) {
            super(parent.variable);
            this.parent = parent;
            this.combiner = combiner;
        }

        @Override
        public PatternDef<T> endAnd() {
            if (combiner == LogicalCombiner.OR) {
                throw new UnsupportedOperationException();
            }
            parent.items.add( new CombinedPatternExprItem<>( combiner, this.getItems() ));
            return parent;
        }

        @Override
        public PatternDef<T> endOr() {
            if (combiner == LogicalCombiner.AND) {
                throw new UnsupportedOperationException();
            }
            parent.items.add( new CombinedPatternExprItem<>( combiner, this.getItems() ));
            return parent;
        }
    }

    public interface PatternItem<T> { }

    public static abstract class PatternExprImpl<T> implements PatternItem<T> {
        private final String exprId;
        private final ReactOn reactOn;

        public PatternExprImpl( String exprId, ReactOn reactOn ) {
            this.exprId = exprId;
            this.reactOn = reactOn;
        }

        public String getExprId() {
            return exprId;
        }

        private String[] getReactOn() {
            return reactOn != null ? reactOn.getStrings() : null;
        }

        public ReactivitySpecs getReactivitySpecs(DomainClassMetadata metadata) {
            return reactOn != null ? new ReactivitySpecs( metadata, reactOn.getStrings() ) : ReactivitySpecs.EMPTY;
        }

        public abstract Constraint asConstraint( PatternDefImpl patternDef );
    }

    public static class CombinedPatternExprItem<T> extends PatternExprImpl<T> {
        private final LogicalCombiner combiner;
        private final List<PatternItem<T>> items;

        public CombinedPatternExprItem( LogicalCombiner combiner, List<PatternItem<T>> items ) {
            super(null, null);
            this.combiner = combiner;
            this.items = items;
        }

        @Override
        public String getExprId() {
            return items.stream().map( PatternExprImpl.class::cast ).map( PatternExprImpl::getExprId ).collect( joining( combiner.getSymbol() ) );
        }

        @Override
        public ReactivitySpecs getReactivitySpecs(DomainClassMetadata metadata) {
            String[] strings = items.stream().map( PatternExprImpl.class::cast ).flatMap( e -> Stream.of( e.getReactOn() ) ).toArray( String[]::new );
            return new ReactivitySpecs(metadata, strings);
        }

        @Override
        public Constraint asConstraint( PatternDefImpl patternDef ) {
            List<Constraint> combined = items.stream().map( PatternExprImpl.class::cast ).map( e -> e.asConstraint( patternDef ) ).collect( toList() );
            return combiner == LogicalCombiner.OR ? new OrConstraints( combined ) : new AndConstraints( combined );
        }
    }

    public static class PatternExpr1<T> extends PatternExprImpl<T> {

        private final Predicate1<T> predicate;

        private final AlphaIndex<T, ?> index;

        public PatternExpr1( Predicate1<T> predicate, AlphaIndex<T, ?> index, ReactOn reactOn ) {
            this( randomUUID().toString(), predicate, index, reactOn );
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
        public Constraint asConstraint( PatternDefImpl patternDef ) {
            SingleConstraint1 constraint = new SingleConstraint1( getExprId(), patternDef.getFirstVariable(), getPredicate() );
            constraint.setIndex( getIndex() );
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
            return constraint;
        }
    }

    public static class PatternExpr2<T, U> extends PatternExprImpl<T> {
        private final Variable<U> var2;
        private final Predicate2<T, U> predicate;

        private final BetaIndex<T, U, ?> index;

        public PatternExpr2( Variable<U> var2, Predicate2<T, U> predicate, BetaIndexImpl<T, U, ?> index, ReactOn reactOn ) {
            this( randomUUID().toString(), var2, predicate, index, reactOn );
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
        public Constraint asConstraint( PatternDefImpl patternDef ) {
            SingleConstraint2 constraint = new SingleConstraint2( getExprId(), patternDef.getFirstVariable(), var2, getPredicate() );
            constraint.setIndex( getIndex() );
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
            return constraint;
        }
    }

    public static class PatternExpr3<T, A, B> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Predicate3<T, A, B> predicate;

        public PatternExpr3( Variable<A> var2, Variable<B> var3, Predicate3<T, A, B> predicate, ReactOn reactOn ) {
            this( randomUUID().toString(), var2, var3, predicate, reactOn );
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
        public Constraint asConstraint( PatternDefImpl patternDef ) {
            SingleConstraint3 constraint = new SingleConstraint3( getExprId(), patternDef.getFirstVariable(), var2, var3, getPredicate() );
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
            return constraint;
        }
    }

    public static class PatternExpr4<T, A, B, C> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Predicate4<T, A, B, C> predicate;

        public PatternExpr4( Variable<A> var2, Variable<B> var3, Variable<C> var4, Predicate4<T, A, B, C> predicate, ReactOn reactOn ) {
            this( randomUUID().toString(), var2, var3, var4, predicate, reactOn );
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
        public Constraint asConstraint( PatternDefImpl patternDef ) {
            SingleConstraint4 constraint = new SingleConstraint4( getExprId(), patternDef.getFirstVariable(), var2, var3, var4, getPredicate() );
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
            return constraint;
        }
    }

    public static class PatternExpr5<T, A, B, C, D> extends PatternExprImpl<T> {
        private final Variable<A> var2;
        private final Variable<B> var3;
        private final Variable<C> var4;
        private final Variable<D> var5;
        private final Predicate5<T, A, B, C, D> predicate;

        public PatternExpr5( Variable<A> var2, Variable<B> var3, Variable<C> var4, Variable<D> var5, Predicate5<T, A, B, C, D> predicate, ReactOn reactOn ) {
            this( randomUUID().toString(), var2, var3, var4, var5, predicate, reactOn );
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
        public Constraint asConstraint( PatternDefImpl patternDef ) {
            SingleConstraint5 constraint = new SingleConstraint5( getExprId(), patternDef.getFirstVariable(), var2, var3, var4, var5, getPredicate() );
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
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
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
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
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
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
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
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
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
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
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
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
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
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
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
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
            constraint.setReactivitySpecs( getReactivitySpecs( patternDef.getDomainClassMetadata() ) );
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
        private final String[] strings;

        public ReactOn( String... strings ) {
            this.strings = strings;
        }

        private String[] getStrings() {
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
            this.f = new Function1.Impl<>( f );
        }

        @Override
        public Binding asBinding( PatternDefImpl patternDef ) {
            return new BindViewItem1( boundVar, f, patternDef.getFirstVariable(), getReactOn(), null );
        }
    }

    public static class PatternBinding2<T, U, A> extends PatternBindingImpl<T, A> {
        private final Variable<U> otherVar;
        private final Function2<T, U, A> f;

        public PatternBinding2( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f, ReactOn reactOn ) {
            super( boundVar, reactOn );
            this.f = new Function2.Impl<>( f );
            this.otherVar = otherVar;
        }

        @Override
        public Binding asBinding( PatternDefImpl patternDef ) {
            return new BindViewItem2( boundVar, f, patternDef.getFirstVariable(), otherVar, getReactOn(), null );
        }
    }

    // -- Conditional Named Consequnce --

    public static <A> ConditionalConsequenceBuilder when( Variable<A> var, Predicate1<A> predicate ) {
        return when( FlowDSL.expr( var, predicate ) );
    }

    public static <A> ConditionalConsequenceBuilder when( String exprId, Variable<A> var, Predicate1<A> predicate ) {
        return when( FlowDSL.expr( exprId, var, predicate ) );
    }

    public static <A, B> ConditionalConsequenceBuilder when( Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate ) {
        return when( FlowDSL.expr( var1, var2, predicate ) );
    }

    public static <A, B> ConditionalConsequenceBuilder when( String exprId, Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate ) {
        return when( FlowDSL.expr( exprId, var1, var2, predicate ) );
    }

    public static ConditionalConsequenceBuilder when( ExprViewItem expr ) {
        return new ConditionalConsequenceBuilder( expr );
    }

    // -- rule --

    public static RuleBuilder rule( String name ) {
        return new RuleBuilder( VIEW_BUILDER, name );
    }

    public static RuleBuilder rule( String pkg, String name ) {
        return new RuleBuilder( VIEW_BUILDER, pkg, name );
    }

    // -- query --

    public static Query0Def query( String name ) {
        return new Query0DefImpl( VIEW_BUILDER, name );
    }

    public static Query0Def query( String pkg, String name ) {
        return new Query0DefImpl( VIEW_BUILDER, pkg, name );
    }

    public static <T1> Query1Def<T1> query(String name, Class<T1> type1) {
        return new Query1DefImpl<>(VIEW_BUILDER, name, type1);
    }

    public static <T1> Query1Def<T1> query(String pkg, String name, Class<T1> type1) {
        return new Query1DefImpl<>(VIEW_BUILDER, pkg, name, type1);
    }

    public static <T1> Query1Def<T1> query(String name, Class<T1> type1, String arg1name) {
        return new Query1DefImpl<>(VIEW_BUILDER, name, type1, arg1name);
    }

    public static <T1> Query1Def<T1> query(String pkg, String name, Class<T1> type1, String arg1name) {
        return new Query1DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name);
    }

    public static <T1, T2> Query2Def<T1, T2> query(String name, Class<T1> type1, Class<T2> type2) {
        return new Query2DefImpl<>(VIEW_BUILDER, name, type1, type2);
    }

    public static <T1, T2> Query2Def<T1, T2> query(String pkg, String name, Class<T1> type1, Class<T2> type2) {
        return new Query2DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2);
    }

    public static <T1, T2> Query2Def<T1, T2> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name) {
        return new Query2DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name);
    }

    public static <T1, T2> Query2Def<T1, T2> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name) {
        return new Query2DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name);
    }

    public static <T1, T2, T3> Query3Def<T1, T2, T3> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3) {
        return new Query3DefImpl<>(VIEW_BUILDER, name, type1, type2, type3);
    }

    public static <T1, T2, T3> Query3Def<T1, T2, T3> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3) {
        return new Query3DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3);
    }

    public static <T1, T2, T3> Query3Def<T1, T2, T3> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name) {
        return new Query3DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name);
    }

    public static <T1, T2, T3> Query3Def<T1, T2, T3> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name) {
        return new Query3DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name);
    }

    public static <T1, T2, T3, T4> Query4Def<T1, T2, T3, T4> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
        return new Query4DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4);
    }

    public static <T1, T2, T3, T4> Query4Def<T1, T2, T3, T4> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
        return new Query4DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4);
    }

    public static <T1, T2, T3, T4> Query4Def<T1, T2, T3, T4> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name) {
        return new Query4DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name);
    }

    public static <T1, T2, T3, T4> Query4Def<T1, T2, T3, T4> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name) {
        return new Query4DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name);
    }

    public static <T1, T2, T3, T4, T5> Query5Def<T1, T2, T3, T4, T5> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5) {
        return new Query5DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5);
    }

    public static <T1, T2, T3, T4, T5> Query5Def<T1, T2, T3, T4, T5> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5) {
        return new Query5DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5);
    }

    public static <T1, T2, T3, T4, T5> Query5Def<T1, T2, T3, T4, T5> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name) {
        return new Query5DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name);
    }

    public static <T1, T2, T3, T4, T5> Query5Def<T1, T2, T3, T4, T5> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name) {
        return new Query5DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name);
    }

    public static <T1, T2, T3, T4, T5, T6> Query6Def<T1, T2, T3, T4, T5, T6> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6) {
        return new Query6DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5, type6);
    }

    public static <T1, T2, T3, T4, T5, T6> Query6Def<T1, T2, T3, T4, T5, T6> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6) {
        return new Query6DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5, type6);
    }

    public static <T1, T2, T3, T4, T5, T6> Query6Def<T1, T2, T3, T4, T5, T6> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name) {
        return new Query6DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name);
    }

    public static <T1, T2, T3, T4, T5, T6> Query6Def<T1, T2, T3, T4, T5, T6> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name) {
        return new Query6DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Query7Def<T1, T2, T3, T4, T5, T6, T7> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7) {
        return new Query7DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5, type6, type7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Query7Def<T1, T2, T3, T4, T5, T6, T7> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7) {
        return new Query7DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5, type6, type7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Query7Def<T1, T2, T3, T4, T5, T6, T7> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name) {
        return new Query7DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Query7Def<T1, T2, T3, T4, T5, T6, T7> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name) {
        return new Query7DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Query8Def<T1, T2, T3, T4, T5, T6, T7, T8> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8) {
        return new Query8DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5, type6, type7, type8);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Query8Def<T1, T2, T3, T4, T5, T6, T7, T8> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8) {
        return new Query8DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5, type6, type7, type8);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Query8Def<T1, T2, T3, T4, T5, T6, T7, T8> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name) {
        return new Query8DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Query8Def<T1, T2, T3, T4, T5, T6, T7, T8> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name) {
        return new Query8DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Query9Def<T1, T2, T3, T4, T5, T6, T7, T8, T9> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8, Class<T9> type9) {
        return new Query9DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5, type6, type7, type8, type9);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Query9Def<T1, T2, T3, T4, T5, T6, T7, T8, T9> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8, Class<T9> type9) {
        return new Query9DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5, type6, type7, type8, type9);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Query9Def<T1, T2, T3, T4, T5, T6, T7, T8, T9> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name, Class<T9> type9, String arg9name) {
        return new Query9DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name, type9, arg9name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Query9Def<T1, T2, T3, T4, T5, T6, T7, T8, T9> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name, Class<T9> type9, String arg9name) {
        return new Query9DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name, type9, arg9name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Query10Def<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8, Class<T9> type9, Class<T10> type10) {
        return new Query10DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5, type6, type7, type8, type9, type10);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Query10Def<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8, Class<T9> type9, Class<T10> type10) {
        return new Query10DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5, type6, type7, type8, type9, type10);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Query10Def<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name, Class<T9> type9, String arg9name, Class<T10> type10, String arg10name) {
        return new Query10DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name, type9, arg9name, type10, arg10name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Query10Def<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name, Class<T9> type9, String arg9name, Class<T10> type10, String arg10name) {
        return new Query10DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name, type9, arg9name, type10, arg10name);
    }

    // -- async --

    public static <T> PatternDSL.ExchangeDef<T> send( Exchange<T> exchange ) {
        return new ExchangeDefImpl<>( exchange );
    }

    public interface ExchangeDef<T> extends ViewItem<T> {
        ExchangeDef<T> message( Function0<T> supplier );
    }

    public static class ExchangeDefImpl<T> implements ExchangeDef<T> {

        private final Exchange<T> exchange;

        public ExchangeDefImpl( Exchange<T> exchange ) {
            this.exchange = exchange;
        }

        @Override
        public ExchangeDef<T> message( Function0<T> supplier ) {
            exchange.setMessageSupplier( supplier );
            return this;
        }

        @Override
        public Exchange<T> getFirstVariable() {
            return exchange;
        }

        @Override
        public Variable<?>[] getVariables() {
            throw new UnsupportedOperationException();
        }
    }

    public static <T> PatternDSL.PatternDef<T> receive(Exchange<T> exchange) {
        return pattern(exchange);
    }
}