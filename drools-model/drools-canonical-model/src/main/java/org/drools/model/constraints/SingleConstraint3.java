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

package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.LambdaPrinter;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr3ViewItemImpl;

public class SingleConstraint3<A, B, C> extends AbstractSingleConstraint {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Predicate3<A, B, C> predicate;

    public SingleConstraint3(Variable<A> var1, Variable<B> var2, Variable<C> var3, Predicate3<A, B, C> predicate) {
        super( LambdaPrinter.print(predicate) );
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.predicate = predicate;
    }

    public SingleConstraint3(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Predicate3<A, B, C> predicate) {
        super(exprId);
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.predicate = predicate;
    }

    public SingleConstraint3(Expr3ViewItemImpl<A, B, C> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getVar2(), expr.getVar3(), expr.getPredicate());
        setReactivitySpecs( expr.getReactivitySpecs() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[]{var1, var2, var3};
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> {
            return predicate.test((A) objs[0], (B) objs[1], (C) objs[2]);
        };
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint3<?, ?, ?> that = ( SingleConstraint3<?, ?, ?> ) o;

        if ( !ModelComponent.areEqualInModel( var1, that.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var2, that.var2 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var3, that.var3 ) ) return false;
        return predicate.equals( that.predicate );
    }

    @Override
    public SingleConstraint3<A, B, C> negate() {
        return negate( new SingleConstraint3<>("!" + getExprId(), var1, var2, var3, predicate.negate()) );
    }

    @Override
    public SingleConstraint3<A, B, C> replaceVariable( Variable oldVar, Variable newVar ) {
        if (var1 == oldVar) {
            return new SingleConstraint3<>(getExprId(), newVar, var2, var3, predicate);
        }
        if (var2 == oldVar) {
            return new SingleConstraint3<>(getExprId(), var1, newVar, var3, predicate);
        }
        if (var3 == oldVar) {
            return new SingleConstraint3<>(getExprId(), var1, var2, newVar, predicate);
        }
        return this;
    }
}
