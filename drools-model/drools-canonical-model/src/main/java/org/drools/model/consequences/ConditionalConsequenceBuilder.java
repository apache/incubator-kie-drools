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
package org.drools.model.consequences;

import org.drools.model.ConditionalConsequence;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder.ValidBuilder;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.view.ExprViewItem;

import static org.drools.model.DSL.expr;

public class ConditionalConsequenceBuilder implements RuleItemBuilder<ConditionalConsequence> {

    private final ConditionalConsequenceBuilder rootBuilder;
    private final ExprViewItem expr;

    private ValidBuilder thenBuilder;
    private ConditionalConsequenceBuilder elseBuilder;


    public ConditionalConsequenceBuilder( ExprViewItem expr ) {
        this.rootBuilder = this;
        this.expr = expr;
    }

    private ConditionalConsequenceBuilder( ConditionalConsequenceBuilder rootBuilder, ExprViewItem expr ) {
        this.rootBuilder = rootBuilder;
        this.expr = expr;
    }

    public ConditionalConsequenceBuilder then(ValidBuilder thenBuilder) {
        this.thenBuilder = thenBuilder;
        return this;
    }

    public <A> ConditionalConsequenceBuilder elseWhen(Variable<A> var, Predicate1<A> predicate) {
        return elseWhen( expr(var, predicate) );
    }

    public <A> ConditionalConsequenceBuilder elseWhen(String exprId, Variable<A> var, Predicate1<A> predicate) {
        return elseWhen( expr(exprId, var, predicate) );
    }

    public <A, B> ConditionalConsequenceBuilder elseWhen(Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
        return elseWhen( expr(var1, var2, predicate) );
    }

    public <A, B> ConditionalConsequenceBuilder elseWhen(String exprId, Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
        return elseWhen( expr(exprId, var1, var2, predicate) );
    }

    public ConditionalConsequenceBuilder elseWhen(ExprViewItem expr) {
        this.elseBuilder = new ConditionalConsequenceBuilder(rootBuilder, expr);
        return elseBuilder;
    }

    public ConditionalConsequenceBuilder elseWhen() {
        return elseWhen(null);
    }

    @Override
    public ConditionalConsequence get() {
        return this == rootBuilder ? internalGet() : rootBuilder.get();
    }

    private ConditionalConsequenceImpl internalGet() {
        return new ConditionalConsequenceImpl(expr, thenBuilder.get(), elseBuilder != null ? elseBuilder.internalGet() : null);
    }
}
