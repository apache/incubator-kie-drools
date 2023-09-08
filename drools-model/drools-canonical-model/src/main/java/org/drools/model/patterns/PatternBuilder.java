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
package org.drools.model.patterns;

import org.drools.model.Constraint;
import org.drools.model.DataSourceDefinition;
import org.drools.model.Pattern;
import org.drools.model.SingleConstraint;
import org.drools.model.Variable;
import org.drools.model.constraints.AbstractConstraint;
import org.drools.model.constraints.AbstractSingleConstraint;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.constraints.SingleConstraint2;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.impl.DataSourceDefinitionImpl;

import static org.drools.model.DSL.declarationOf;

public class PatternBuilder {

    private DataSourceDefinition dataSourceDefinition = DataSourceDefinitionImpl.DEFAULT;

    public PatternBuilder from(DataSourceDefinition dataSourceDefinition) {
        this.dataSourceDefinition = dataSourceDefinition;
        return this;
    }

    public <T> BoundPatternBuilder<T> filter(Class<T> type) {
        return filter(declarationOf(type));
    }

    public <T> BoundPatternBuilder<T> filter(Variable<T> var) {
        return new BoundPatternBuilder<>(var, dataSourceDefinition);
    }

    public interface ValidBuilder<T> {
        Pattern<T> get();
    }

    public static class BoundPatternBuilder<T> implements ValidBuilder<T> {
        private final Variable<T> variable;
        private DataSourceDefinition dataSourceDefinition;

        private BoundPatternBuilder(Variable<T> variable, DataSourceDefinition dataSourceDefinition) {
            this.variable = variable;
            this.dataSourceDefinition = dataSourceDefinition;
        }

        public BoundPatternBuilder<T> from(DataSourceDefinition dataSourceDefinition) {
            this.dataSourceDefinition = dataSourceDefinition;
            return this;
        }

        public ConstrainedPatternBuilder<T> with( SingleConstraint constraint) {
            return new ConstrainedPatternBuilder(variable, (AbstractSingleConstraint)constraint, dataSourceDefinition);
        }

        public ConstrainedPatternBuilder<T> with( Predicate1<T> predicate) {
            return with(new SingleConstraint1<T>(variable, predicate));
        }

        public <A, B> ConstrainedPatternBuilder<T> with( Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
            return with(new SingleConstraint2<A, B>(var1, var2, predicate));
        }

        public <A> ConstrainedPatternBuilder<T> with( Variable<A> var2, Predicate2<T, A> predicate) {
            return with(new SingleConstraint2<T, A>(variable, var2, predicate));
        }

        public ConstrainedPatternBuilder<T> with( String exprId, Predicate1<T> predicate) {
            return with(new SingleConstraint1<T>(exprId, variable, predicate));
        }

        public <A, B> ConstrainedPatternBuilder<T> with( String exprId, Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
            return with(new SingleConstraint2<A, B>(exprId, var1, var2, predicate));
        }

        public <A> ConstrainedPatternBuilder<T> with( String exprId, Variable<A> var2, Predicate2<T, A> predicate) {
            return with(new SingleConstraint2<T, A>(exprId, variable, var2, predicate));
        }

        @Override
        public Pattern<T> get() {
            return new PatternImpl(variable, SingleConstraint.TRUE );
        }
    }

    public static class ConstrainedPatternBuilder<T> implements ValidBuilder<T> {
        private final Variable<T> variable;
        private Constraint constraint;
        private DataSourceDefinition dataSourceDefinition;
        private AbstractSingleConstraint lastConstraint;

        private ConstrainedPatternBuilder( Variable<T> variable, AbstractSingleConstraint constraint, DataSourceDefinition dataSourceDefinition) {
            this.variable = variable;
            this.constraint = constraint;
            this.lastConstraint = constraint;
            this.dataSourceDefinition = dataSourceDefinition;
        }

        public ConstrainedPatternBuilder<T> from( DataSourceDefinition dataSourceDefinition) {
            this.dataSourceDefinition = dataSourceDefinition;
            return this;
        }

        public ConstrainedPatternBuilder<T> and( Predicate1<T> predicate) {
            return and(new SingleConstraint1<T>(variable, predicate));
        }

        public ConstrainedPatternBuilder<T> and( String exprId, Predicate1<T> predicate) {
            return and(new SingleConstraint1<T>(exprId, variable, predicate));
        }

        public ConstrainedPatternBuilder<T> and( Constraint constraint) {
            this.constraint = ((AbstractConstraint)this.constraint).with(constraint);
            return this;
        }

        public ConstrainedPatternBuilder<T> or( Predicate1<T> predicate) {
            return or(new SingleConstraint1<T>(variable, predicate));
        }

        public ConstrainedPatternBuilder<T> or( String exprId, Predicate1<T> predicate) {
            return or(new SingleConstraint1<T>(exprId, variable, predicate));
        }

        public ConstrainedPatternBuilder<T> or( Constraint constraint) {
            this.constraint = ((AbstractConstraint)this.constraint).or(constraint);
            return this;
        }

        public <A, B> ConstrainedPatternBuilder<T> and( Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
            return and(lastConstraint = new SingleConstraint2<A, B>(var1, var2, predicate));
        }

        public <A> ConstrainedPatternBuilder<T> and( Variable<A> var1, Predicate2<T, A> predicate) {
            return and(lastConstraint = new SingleConstraint2<T, A>(variable, var1, predicate));
        }

        public <A, B> ConstrainedPatternBuilder<T> and( String exprId, Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
            return and(lastConstraint = new SingleConstraint2<A, B>(exprId, var1, var2, predicate));
        }

        public <A> ConstrainedPatternBuilder<T> and( String exprId, Variable<A> var1, Predicate2<T, A> predicate) {
            return and(lastConstraint = new SingleConstraint2<T, A>(exprId, variable, var1, predicate));
        }

        public ConstrainedPatternBuilder<T> and( AbstractConstraint constraint) {
            this.constraint = ((AbstractConstraint)this.constraint).with(constraint);
            return this;
        }

        public <A, B> ConstrainedPatternBuilder<T> or( Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
            return or(lastConstraint = new SingleConstraint2<A, B>(var1, var2, predicate));
        }

        public <A> ConstrainedPatternBuilder<T> or( Variable<A> var1, Predicate2<T, A> predicate) {
            return or(lastConstraint = new SingleConstraint2<T, A>(variable, var1, predicate));
        }

        public <A, B> ConstrainedPatternBuilder<T> or( String exprId, Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
            return or(lastConstraint = new SingleConstraint2<A, B>(exprId, var1, var2, predicate));
        }

        public <A> ConstrainedPatternBuilder<T> or( String exprId, Variable<A> var1, Predicate2<T, A> predicate) {
            return or(lastConstraint = new SingleConstraint2<T, A>(exprId, variable, var1, predicate));
        }

        public ConstrainedPatternBuilder<T> or( AbstractConstraint constraint) {
            this.constraint = ((AbstractConstraint)this.constraint).or(constraint);
            return this;
        }

        @Override
        public Pattern<T> get() {
            return new PatternImpl(variable, constraint);
        }
    }
}
