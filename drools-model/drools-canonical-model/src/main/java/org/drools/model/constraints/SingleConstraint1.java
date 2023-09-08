/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.LambdaPrinter;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.PredicateInformation;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr1ViewItemImpl;

public class SingleConstraint1<A> extends AbstractSingleConstraint {

    private final Variable<A> variable;
    private final Predicate1<A> predicate;

    public SingleConstraint1(Variable<A> variable, Predicate1<A> predicate) {
        super( LambdaPrinter.print(predicate), predicate.predicateInformation());
        this.variable = variable;
        this.predicate = predicate;
    }

    public SingleConstraint1(String exprId, Variable<A> variable, Predicate1<A> predicate) {
        super(exprId, predicate.predicateInformation());
        this.variable = variable;
        this.predicate = predicate;
    }

    /**
     * This constructor generates a constraint that cannot be evaluated as it lacks the actual predicate
     * The AlphaNode referring this can be shared, as the exprId is provided
     * Currently it's used only with the Alpha Network Compiler, since the code instantiating
     * the actual constraint will be inlined inside the compiled Alpha Network itself.
     */
    public SingleConstraint1(String exprId, PredicateInformation predicateInformation) {
        super(exprId, predicateInformation);
        this.variable = null;
        this.predicate = null;
    }

    public SingleConstraint1(Expr1ViewItemImpl<A> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getPredicate());
        setIndex( expr.getIndex() );
        setReactivitySpecs( expr.getReactivitySpecs() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[] { variable };
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> predicate.test( (A)objs[0] );
    }

    @Override
    public Predicate1 getPredicate1() {
        return predicate;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint1<?> that = ( SingleConstraint1<?> ) o;

        if ( !ModelComponent.areEqualInModel( variable, that.variable ) ) return false;
        return predicate.equals( that.predicate );
    }

    @Override
    public SingleConstraint1<A> negate() {
        return negate( new SingleConstraint1<>("!" + getExprId(), variable, predicate.negate()) );
    }

    @Override
    public SingleConstraint1<A> replaceVariable( Variable oldVar, Variable newVar ) {
        if (variable == oldVar) {
            return new SingleConstraint1<>(getExprId(), newVar, predicate);
        }
        return this;
    }
}
