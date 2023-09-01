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
package org.drools.model;

import org.drools.model.constraints.AbstractSingleConstraint;
import org.drools.model.constraints.MultipleConstraints;
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
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.Predicate4;
import org.drools.model.functions.Predicate5;
import org.drools.model.functions.PredicateInformation;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr10ViewItemImpl;
import org.drools.model.view.Expr11ViewItemImpl;
import org.drools.model.view.Expr12ViewItemImpl;
import org.drools.model.view.Expr13ViewItemImpl;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.Expr3ViewItemImpl;
import org.drools.model.view.Expr4ViewItemImpl;
import org.drools.model.view.Expr5ViewItemImpl;
import org.drools.model.view.Expr6ViewItemImpl;
import org.drools.model.view.Expr7ViewItemImpl;
import org.drools.model.view.Expr8ViewItemImpl;
import org.drools.model.view.Expr9ViewItemImpl;
import org.drools.model.view.ExprNViewItem;

public interface SingleConstraint extends Constraint {
    Variable[] getVariables();

    PredicateN getPredicate();

    default Predicate1 getPredicate1() {
        throw new UnsupportedOperationException();
    }

    default Predicate2 getPredicate2() {
        throw new UnsupportedOperationException();
    }

    default Predicate3 getPredicate3() {
        throw new UnsupportedOperationException();
    }

    default Predicate4 getPredicate4() {
        throw new UnsupportedOperationException();
    }

    default Predicate5 getPredicate5() {
        throw new UnsupportedOperationException();
    }

    Index getIndex();

    String getExprId();

    String[] getReactiveProps();
    BitMask getReactivityBitMask();

    default boolean isTemporal() {
        return false;
    }

    @Override
    default Type getType() {
        return Type.SINGLE;
    }

    SingleConstraint TRUE = new AbstractSingleConstraint("TRUE", PredicateInformation.EMPTY_PREDICATE_INFORMATION) {
        @Override
        public Constraint negate() {
            return FALSE;
        }

        @Override
        public Variable[] getVariables() {
            return new Variable[0];
        }

        @Override
        public PredicateN getPredicate() {
            return PredicateN.True;
        }

        @Override
        public MultipleConstraints with( Constraint constraint ) {
            return new MultipleConstraints(constraint);
        }

        @Override
        public boolean isEqualTo( ModelComponent other ) {
            return this == other;
        }

        @Override
        public Constraint replaceVariable( Variable oldVar, Variable newVar ) {
            return this;
        }
    };

    SingleConstraint FALSE = new AbstractSingleConstraint("FALSE", PredicateInformation.EMPTY_PREDICATE_INFORMATION) {
        @Override
        public Constraint negate() {
            return TRUE;
        }

        @Override
        public Variable[] getVariables() {
            return new Variable[0];
        }

        @Override
        public PredicateN getPredicate() {
            return PredicateN.False;
        }

        @Override
        public MultipleConstraints with( Constraint constraint ) {
            return new MultipleConstraints(constraint);
        }

        @Override
        public boolean isEqualTo( ModelComponent other ) {
            return this == other;
        }

        @Override
        public Constraint replaceVariable( Variable oldVar, Variable newVar ) {
            return this;
        }
    };

    static SingleConstraint createConstraint(ExprNViewItem expr) {
        if ( expr instanceof Expr1ViewItemImpl ) {
            return new SingleConstraint1( ( Expr1ViewItemImpl ) expr );
        }
        if ( expr instanceof Expr2ViewItemImpl ) {
            return new SingleConstraint2( ( Expr2ViewItemImpl ) expr );
        }
        if ( expr instanceof Expr3ViewItemImpl ) {
            return new SingleConstraint3( ( Expr3ViewItemImpl ) expr );
        }
        if ( expr instanceof Expr4ViewItemImpl ) {
            return new SingleConstraint4( ( Expr4ViewItemImpl ) expr );
        }
        if ( expr instanceof Expr5ViewItemImpl ) {
            return new SingleConstraint5( ( Expr5ViewItemImpl ) expr );
        }
        if ( expr instanceof Expr6ViewItemImpl ) {
            return new SingleConstraint6( ( Expr6ViewItemImpl ) expr );
        }
        if ( expr instanceof Expr7ViewItemImpl ) {
            return new SingleConstraint7( ( Expr7ViewItemImpl ) expr );
        }
        if (expr instanceof Expr8ViewItemImpl ) {
            return new SingleConstraint8( (Expr8ViewItemImpl) expr );
        }
        if (expr instanceof Expr9ViewItemImpl ) {
            return new SingleConstraint9( (Expr9ViewItemImpl) expr );
        }
        if (expr instanceof Expr10ViewItemImpl ) {
            return new SingleConstraint10( (Expr10ViewItemImpl) expr );
        }
        if (expr instanceof Expr11ViewItemImpl ) {
            return new SingleConstraint11( (Expr11ViewItemImpl) expr );
        }
        if (expr instanceof Expr12ViewItemImpl ) {
            return new SingleConstraint12( (Expr12ViewItemImpl) expr );
        }
        if (expr instanceof Expr13ViewItemImpl ) {
            return new SingleConstraint13( (Expr13ViewItemImpl) expr );
        }
        throw new UnsupportedOperationException( "Unknow expr type: " + expr.getClass() );
    }
}
