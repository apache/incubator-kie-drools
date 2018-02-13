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

import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.constraints.SingleConstraint2;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.impl.DeclarationImpl;
import org.drools.model.impl.RuleBuilder;
import org.drools.model.index.AlphaIndexImpl;
import org.drools.model.index.BetaIndexImpl;
import org.drools.model.view.BindViewItem1;
import org.drools.model.view.BindViewItem2;

import static java.util.UUID.randomUUID;

public class PatternDSL extends DSL {

    public static <T> Declaration<T> declarationOf( Class<T> type ) {
        return new DeclarationImpl<T>( type );
    }

    public static <T> PatternDef<T> pattern( Variable<T> var, PatternItem<T>... items ) {
        return new PatternDef<>( var, items );
    }

    public static <T> PatternItem<T> expr( Predicate1<T> predicate ) {
        return new PatternExpr1<>( new Predicate1.Impl<>(predicate), null, null );
    }

    public static <T> PatternItem<T> expr( String exprId, Predicate1<T> predicate ) {
        return new PatternExpr1<>( exprId, new Predicate1.Impl<>(predicate), null, null );
    }

    public static <T> PatternItem<T> expr( String exprId, Predicate1<T> predicate, AlphaIndex<T, ?> index ) {
        return new PatternExpr1<>( exprId, new Predicate1.Impl<>(predicate), index, null );
    }

    public static <T> PatternItem<T> expr( String exprId, Predicate1<T> predicate, ReactOn reactOn ) {
        return new PatternExpr1<>( exprId, new Predicate1.Impl<>(predicate), null, reactOn );
    }

    public static <T> PatternItem<T> expr( String exprId, Predicate1<T> predicate, AlphaIndex<T, ?> index, ReactOn reactOn ) {
        return new PatternExpr1<>( exprId, new Predicate1.Impl<>(predicate), index, reactOn );
    }

    public static <T, U> PatternItem<T> expr( Variable<U> var2, Predicate2<T, U> predicate ) {
        return new PatternExpr2<>( var2, new Predicate2.Impl<>(predicate), null, null );
    }

    public static <T, U> PatternItem<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate ) {
        return new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>(predicate), null, null );
    }

    public static <T, U> PatternItem<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, BetaIndexImpl<T, U, ?> index ) {
        return new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>(predicate), index, null );
    }

    public static <T, U> PatternItem<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, ReactOn reactOn ) {
        return new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>(predicate), null, reactOn );
    }

    public static <T, U> PatternItem<T> expr( String exprId, Variable<U> var2, Predicate2<T, U> predicate, BetaIndexImpl<T, U, ?> index, ReactOn reactOn ) {
        return new PatternExpr2<>( exprId, var2, new Predicate2.Impl<>(predicate), index, reactOn );
    }

    public static <T, U> AlphaIndex<T, U> alphaIndexedBy( Class<U> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, U> leftOperandExtractor, U rightValue ) {
        return new AlphaIndexImpl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightValue );
    }

    public static <T, U, V> BetaIndexImpl<T, U, V> betaIndexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, V> leftOperandExtractor, Function1<U, V> rightOperandExtractor ) {
        return new BetaIndexImpl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor );
    }

    public static ReactOn reactOn(String... reactOn) {
        return new ReactOn( reactOn );
    }

    public static class PatternDef<T> implements RuleItem, RuleItemBuilder<PatternDef<T>> {
        private final Variable<T> variable;
        private final PatternItem<T>[] items;

        public PatternDef( Variable<T> variable, PatternItem<T>... items ) {
            this.variable = variable;
            this.items = items;
        }

        @Override
        public PatternDef<T> get() {
            return this;
        }

        public Variable<T> getVariable() {
            return variable;
        }

        public PatternItem<T>[] getItems() {
            return items;
        }
    }

    public interface PatternItem<T> { }

    public interface PatternBinding<T> extends PatternItem<T> { }

    public interface PatternExpr<T> extends PatternItem<T> { }

    public static abstract class PatternExprImpl<T> implements PatternExpr<T>  {
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
            return reactOn != null ? reactOn.getStrings() : new String[0];
        }

        public abstract Constraint asConstraint( PatternDef patternDef );
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
        public Constraint asConstraint(PatternDef patternDef) {
            SingleConstraint1 constraint = new SingleConstraint1(getExprId(), patternDef.getVariable(), getPredicate());
            constraint.setIndex( getIndex() );
            constraint.setReactiveProps( getReactOn() );
            return constraint;
        }
    }

    public static class PatternExpr2<T, U> extends PatternExprImpl<T> {
        private final Variable<U> var2;
        private final Predicate2<T, U> predicate;

        private final BetaIndexImpl<T, U, ?> index;

        public PatternExpr2(Variable<U> var2, Predicate2<T, U> predicate, BetaIndexImpl<T, U, ?> index, ReactOn reactOn) {
            this(randomUUID().toString(), var2, predicate, index, reactOn);
        }

        public PatternExpr2( String exprId, Variable<U> var2, Predicate2<T, U> predicate, BetaIndexImpl<T, U, ?> index, ReactOn reactOn ) {
            super( exprId, reactOn );
            this.var2 = var2;
            this.predicate = predicate;
            this.index = index;
        }

        public Variable<U> getVar2() {
            return var2;
        }

        public Predicate2<T, U> getPredicate() {
            return predicate;
        }

        public BetaIndexImpl<T, U, ?> getIndex() {
            return index;
        }

        @Override
        public Constraint asConstraint(PatternDef patternDef) {
            SingleConstraint2 constraint = new SingleConstraint2(getExprId(), patternDef.getVariable(), getVar2(), getPredicate());
            constraint.setIndex( getIndex() );
            constraint.setReactiveProps( getReactOn() );
            return constraint;
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

    public static <A, T> PatternItem<T> bind( Variable<A> boundVar, Function1<T, A> f ) {
        return new PatternBinding1<>(boundVar, f, null);
    }

    public static <A, T> PatternItem<T> bind( Variable<A> boundVar, Function1<T, A> f, ReactOn reactOn ) {
        return new PatternBinding1<>(boundVar, f, reactOn);
    }

    public static <A, T, U> PatternItem<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f ) {
        return new PatternBinding2<>(boundVar, otherVar, f, null);
    }

    public static <A, T, U> PatternItem<T> bind( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f, ReactOn reactOn ) {
        return new PatternBinding2<>(boundVar, otherVar, f, reactOn);
    }

    public static abstract class PatternBindingImpl<T, A> implements PatternBinding<T> {
        protected final Variable<A> boundVar;
        protected final ReactOn reactOn;

        public PatternBindingImpl( Variable<A> boundVar, ReactOn reactOn ) {
            this.boundVar = boundVar;
            this.reactOn = reactOn;
        }

        public String[] getReactOn() {
            return reactOn != null ? reactOn.getStrings() : new String[0];
        }

        public abstract Binding asBinding( PatternDef patternDef );
    }

    public static class PatternBinding1<T, A> extends PatternBindingImpl<T, A> {
        private final Function1<T, A> f;

        public PatternBinding1( Variable<A> boundVar, Function1<T, A> f, ReactOn reactOn ) {
            super( boundVar, reactOn );
            this.f = f;
        }

        @Override
        public Binding asBinding( PatternDef patternDef ) {
            return new BindViewItem1(boundVar, f, patternDef.getVariable(), getReactOn()[0]);
        }
    }

    public static class PatternBinding2<T, U, A> extends PatternBindingImpl<T, A> {
        private final Variable<U> otherVar;
        private final Function2<T, U, A> f;

        public PatternBinding2( Variable<A> boundVar, Variable<U> otherVar, Function2<T, U, A> f, ReactOn reactOn ) {
            super( boundVar, reactOn );
            this.f = f;
            this.otherVar = otherVar;
        }

        @Override
        public Binding asBinding( PatternDef patternDef ) {
            return new BindViewItem2(boundVar, f, patternDef.getVariable(), otherVar, getReactOn()[0]);
        }
    }

    // -- rule --

    public static RuleBuilder rule( String name) {
        return new RuleBuilder.ForPattern(name);
    }

    public static RuleBuilder rule(String pkg, String name) {
        return new RuleBuilder.ForPattern(pkg, name);
    }
}
