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

package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.temporal.TemporalPredicate;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.VariableTemporalExprViewItem;

public class VariableTemporalConstraint<A> extends TemporalConstraint {

    private final Function1<?,?> f1;
    private final Variable<?> var2;
    private final Function1<?,?> f2;

    public VariableTemporalConstraint( String exprId, Variable<A> var1, Function1<?,?> f1, Variable<?> var2, Function1<?,?> f2, TemporalPredicate temporalPredicate ) {
        super(exprId, var1, temporalPredicate);
        this.f1 = f1;
        this.var2 = var2;
        this.f2 = f2;
    }

    public VariableTemporalConstraint( VariableTemporalExprViewItem<A> expr ) {
        this( expr.getExprId(), expr.getFirstVariable(), expr.getF1(), expr.getSecondVariable(), expr.getF2(), expr.getTemporalPredicate() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[] { var1, var2 };
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        VariableTemporalConstraint<?> that = ( VariableTemporalConstraint<?> ) o;

        if ( !ModelComponent.areEqualInModel( var1, that.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( f1, that.f1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var2, that.var2 ) ) return false;
        if ( !ModelComponent.areEqualInModel( f2, that.f2 ) ) return false;
        return temporalPredicate.equals( that.temporalPredicate );
    }

    @Override
    public Function1<?, ?> getF1() {
        return f1;
    }

    @Override
    public Function1<?, ?> getF2() {
        return f2;
    }

    @Override
    public VariableTemporalConstraint<A> negate() {
        return new VariableTemporalConstraint<A>( "!" + getExprId(), var1, f1, var2, f2, temporalPredicate.negate() );
    }

    @Override
    public VariableTemporalConstraint<A> replaceVariable( Variable oldVar, Variable newVar ) {
        if (var1 == oldVar) {
            return new VariableTemporalConstraint<>( getExprId(), newVar, f1, var2, f2, temporalPredicate );
        }
        if (var2 == oldVar) {
            return new VariableTemporalConstraint<>( getExprId(), var1, f1, newVar, f2, temporalPredicate );
        }
        return this;
    }

    @Override
    public String toString() {
        return "VariableTemporalConstraint for '" + getExprId() + "' (" +
                "function1: lambda " + System.identityHashCode(f1) + ", " +
                "function2: lambda " + System.identityHashCode(f2) + ")";
    }

}
