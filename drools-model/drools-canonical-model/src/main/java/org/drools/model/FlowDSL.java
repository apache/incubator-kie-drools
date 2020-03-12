/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model;

import org.drools.model.consequences.ConditionalConsequenceBuilder;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Function3;
import org.drools.model.functions.Function4;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.temporal.TemporalPredicate;
import org.drools.model.impl.DeclarationImpl;
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
import org.drools.model.view.BindViewItem1;
import org.drools.model.view.BindViewItem2;
import org.drools.model.view.BindViewItem3;
import org.drools.model.view.BindViewItem4;
import org.drools.model.view.Expr1ViewItem;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.FixedTemporalExprViewItem;
import org.drools.model.view.InputViewItem;
import org.drools.model.view.InputViewItemImpl;
import org.drools.model.view.TemporalExprViewItem;
import org.drools.model.view.VariableTemporalExprViewItem;
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

    public static <T> InputViewItem<T> input(Variable<T> var, DeclarationSource source) {
        (( DeclarationImpl<T> ) var).setSource( source );
        return new InputViewItemImpl<>( var );
    }

    public static <T> Expr1ViewItem<T> expr( Variable<T> var) {
        return new Expr1ViewItemImpl<>( var, new Predicate1.Impl<>(t -> true) );
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

    public static class BindViewItemBuilder<T> implements ViewItem<T> {
        private final Variable<T> boundVariable;
        private Function1 function1;
        private Function2 function2;
        private Function3 function3;
        private Function4 function4;
        private Variable inputVariable1;
        private Variable inputVariable2;
        private Variable inputVariable3;
        private Variable inputVariable4;
        private String[] reactOn;
        private String[] watchedProps;

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

        public <A, B, C> BindViewItemBuilder<T> as( Variable<A> var1, Variable<B> var2, Variable<C> var3, Function3<A, B, C, T> f) {
            this.function3 = new Function3.Impl<>(f);
            this.inputVariable1 = var1;
            this.inputVariable2 = var2;
            this.inputVariable3 = var3;
            return this;
        }

        public <A, B, C, D> BindViewItemBuilder<T> as( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Function4<A, B, C, D, T> f) {
            this.function4 = new Function4.Impl<>(f);
            this.inputVariable1 = var1;
            this.inputVariable2 = var2;
            this.inputVariable3 = var3;
            this.inputVariable4 = var4;
            return this;
        }

        public BindViewItemBuilder<T> reactOn( String... reactOn ) {
            this.reactOn = reactOn;
            return this;
        }

        public BindViewItemBuilder<T> watch(String... props) {
            this.watchedProps = props;
            return this;
        }

        public String[] getWatchedProps() {
            return watchedProps;
        }

        @Override
        public Variable<?>[] getVariables() {
            if (function1 != null) {
                return new Variable[]{inputVariable1};
            } else if (function2 != null) {
                return new Variable[] {inputVariable1, inputVariable2};
            } else if (function3 != null) {
                return new Variable[] {inputVariable1, inputVariable2, inputVariable3};
            } else if (function4 != null) {
                return new Variable[] {inputVariable1, inputVariable2, inputVariable3, inputVariable4};
            }
            throw new UnsupportedOperationException("function needed");
        }

        @Override
        public ViewItem<T> get() {
            if(function1 != null) {
                return new BindViewItem1<>(boundVariable, function1, inputVariable1, reactOn, watchedProps);
            } else if(function2 != null) {
                return new BindViewItem2<>(boundVariable, function2, inputVariable1, inputVariable2, reactOn, watchedProps);
            } else if(function3 != null) {
                return new BindViewItem3<>(boundVariable, function3, inputVariable1, inputVariable2, inputVariable3, reactOn, watchedProps);
            } else if(function4 != null) {
                return new BindViewItem4<>(boundVariable, function4, inputVariable1, inputVariable2, inputVariable3, inputVariable4, reactOn, watchedProps);
            }
            throw new UnsupportedOperationException("function needed");
        }
    }

    // -- Temporal Constraints --

    public static <T,U> TemporalExprViewItem<T> expr( String exprId, Variable<T> var1, Variable<U> var2, TemporalPredicate temporalPredicate ) {
        return new VariableTemporalExprViewItem<>( exprId, var1, null, var2, null, temporalPredicate);
    }
    public static <T,U> TemporalExprViewItem<T> expr( String exprId, Variable<T> var1, Variable<U> var2, Function1<U,?> f2, TemporalPredicate temporalPredicate ) {
        return new VariableTemporalExprViewItem<>( exprId, var1, null, var2, new Function1.Impl<>( f2 ), temporalPredicate);
    }
    public static <T,U> TemporalExprViewItem<T> expr( String exprId, Variable<T> var1, Function1<T,?> f1, Variable<U> var2, TemporalPredicate temporalPredicate ) {
        return new VariableTemporalExprViewItem<>( exprId, var1, new Function1.Impl<>( f1 ), var2, null, temporalPredicate);
    }
    public static <T> TemporalExprViewItem<T> expr( String exprId, Variable<T> var1, Function1<T,?> f1, Function1<T,?> f2, TemporalPredicate temporalPredicate ) {
        return new VariableTemporalExprViewItem<>( exprId, var1, new Function1.Impl<>( f1 ), var1, new Function1.Impl<>( f2 ), temporalPredicate);
    }
    public static <T,U> TemporalExprViewItem<T> expr( String exprId, Variable<T> var1, Function1<T,?> f1, Variable<U> var2, Function1<U,?> f2, TemporalPredicate temporalPredicate ) {
        return new VariableTemporalExprViewItem<>( exprId, var1, new Function1.Impl<>( f1 ), var2, new Function1.Impl<>( f2 ), temporalPredicate);
    }

    public static <T> TemporalExprViewItem<T> expr( String exprId, Variable<T> var1, long value, TemporalPredicate temporalPredicate ) {
        return new FixedTemporalExprViewItem<>( exprId, var1, null, value, temporalPredicate);
    }
    public static <T> TemporalExprViewItem<T> expr( String exprId, Variable<T> var1, Function1<?,?> func, long value, TemporalPredicate temporalPredicate ) {
        return new FixedTemporalExprViewItem<>( exprId, var1, new Function1.Impl<>( func ), value, temporalPredicate);
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

    public static <T1> Query1Def<T1> query(String name, Class<T1> type1) {
        return new Query1DefImpl<>(VIEW_BUILDER, name, type1);
    }

    public static <T1> Query1Def<T1> query(String name, Class<T1> type1, String arg1name) {
        return new Query1DefImpl<>(VIEW_BUILDER, name, type1, arg1name);
    }

    public static <T1> Query1Def<T1> query(String pkg, String name, Class<T1> type1) {
        return new Query1DefImpl<>(VIEW_BUILDER, pkg, name, type1);
    }

    public static <T1> Query1Def<T1> query(String pkg, String name, Class<T1> type1, String arg1name) {
        return new Query1DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name);
    }

    public static <T1, T2> Query2Def<T1, T2> query(String name, Class<T1> type1, Class<T2> type2) {
        return new Query2DefImpl<>(VIEW_BUILDER, name, type1, type2);
    }

    public static <T1, T2> Query2Def<T1, T2> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name) {
        return new Query2DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name);
    }

    public static <T1, T2> Query2Def<T1, T2> query(String pkg, String name, Class<T1> type1, Class<T2> type2) {
        return new Query2DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2);
    }

    public static <T1, T2> Query2Def<T1, T2> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name) {
        return new Query2DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name);
    }

    public static <T1, T2, T3> Query3Def<T1, T2, T3> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3) {
        return new Query3DefImpl<>(VIEW_BUILDER, name, type1, type2, type3);
    }

    public static <T1, T2, T3> Query3Def<T1, T2, T3> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name) {
        return new Query3DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name);
    }

    public static <T1, T2, T3> Query3Def<T1, T2, T3> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3) {
        return new Query3DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3);
    }

    public static <T1, T2, T3> Query3Def<T1, T2, T3> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name) {
        return new Query3DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name);
    }

    public static <T1, T2, T3, T4> Query4Def<T1, T2, T3, T4> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
        return new Query4DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4);
    }

    public static <T1, T2, T3, T4> Query4Def<T1, T2, T3, T4> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name) {
        return new Query4DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name);
    }

    public static <T1, T2, T3, T4> Query4Def<T1, T2, T3, T4> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
        return new Query4DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4);
    }

    public static <T1, T2, T3, T4> Query4Def<T1, T2, T3, T4> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name) {
        return new Query4DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name);
    }

    public static <T1, T2, T3, T4, T5> Query5Def<T1, T2, T3, T4, T5> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5) {
        return new Query5DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5);
    }

    public static <T1, T2, T3, T4, T5> Query5Def<T1, T2, T3, T4, T5> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name) {
        return new Query5DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name);
    }

    public static <T1, T2, T3, T4, T5> Query5Def<T1, T2, T3, T4, T5> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5) {
        return new Query5DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5);
    }

    public static <T1, T2, T3, T4, T5> Query5Def<T1, T2, T3, T4, T5> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name) {
        return new Query5DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name);
    }

    public static <T1, T2, T3, T4, T5, T6> Query6Def<T1, T2, T3, T4, T5, T6> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6) {
        return new Query6DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5, type6);
    }

    public static <T1, T2, T3, T4, T5, T6> Query6Def<T1, T2, T3, T4, T5, T6> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name) {
        return new Query6DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name);
    }

    public static <T1, T2, T3, T4, T5, T6> Query6Def<T1, T2, T3, T4, T5, T6> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6) {
        return new Query6DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5, type6);
    }

    public static <T1, T2, T3, T4, T5, T6> Query6Def<T1, T2, T3, T4, T5, T6> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name) {
        return new Query6DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Query7Def<T1, T2, T3, T4, T5, T6, T7> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7) {
        return new Query7DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5, type6, type7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Query7Def<T1, T2, T3, T4, T5, T6, T7> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name) {
        return new Query7DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Query7Def<T1, T2, T3, T4, T5, T6, T7> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7) {
        return new Query7DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5, type6, type7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Query7Def<T1, T2, T3, T4, T5, T6, T7> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name) {
        return new Query7DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Query8Def<T1, T2, T3, T4, T5, T6, T7, T8> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8) {
        return new Query8DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5, type6, type7, type8);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Query8Def<T1, T2, T3, T4, T5, T6, T7, T8> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name) {
        return new Query8DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Query8Def<T1, T2, T3, T4, T5, T6, T7, T8> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8) {
        return new Query8DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5, type6, type7, type8);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Query8Def<T1, T2, T3, T4, T5, T6, T7, T8> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name) {
        return new Query8DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Query9Def<T1, T2, T3, T4, T5, T6, T7, T8, T9> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8, Class<T9> type9) {
        return new Query9DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5, type6, type7, type8, type9);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Query9Def<T1, T2, T3, T4, T5, T6, T7, T8, T9> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name, Class<T9> type9, String arg9name) {
        return new Query9DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name, type9, arg9name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Query9Def<T1, T2, T3, T4, T5, T6, T7, T8, T9> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8, Class<T9> type9) {
        return new Query9DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5, type6, type7, type8, type9);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Query9Def<T1, T2, T3, T4, T5, T6, T7, T8, T9> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name, Class<T9> type9, String arg9name) {
        return new Query9DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name, type9, arg9name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Query10Def<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> query(String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8, Class<T9> type9, Class<T10> type10) {
        return new Query10DefImpl<>(VIEW_BUILDER, name, type1, type2, type3, type4, type5, type6, type7, type8, type9, type10);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Query10Def<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> query(String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name, Class<T9> type9, String arg9name, Class<T10> type10, String arg10name) {
        return new Query10DefImpl<>(VIEW_BUILDER, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name, type9, arg9name, type10, arg10name);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Query10Def<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> query(String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8, Class<T9> type9, Class<T10> type10) {
        return new Query10DefImpl<>(VIEW_BUILDER, pkg, name, type1, type2, type3, type4, type5, type6, type7, type8, type9, type10);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Query10Def<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> query(String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name, Class<T9> type9, String arg9name, Class<T10> type10, String arg10name) {
        return new Query10DefImpl<>(VIEW_BUILDER, pkg, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name, type9, arg9name, type10, arg10name);
    }
}
