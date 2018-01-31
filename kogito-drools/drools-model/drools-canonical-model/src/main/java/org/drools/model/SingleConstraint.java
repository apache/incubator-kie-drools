package org.drools.model;

import org.drools.model.constraints.AbstractSingleConstraint;
import org.drools.model.constraints.AndConstraints;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.constraints.SingleConstraint2;
import org.drools.model.constraints.SingleConstraint3;
import org.drools.model.constraints.SingleConstraint4;
import org.drools.model.constraints.SingleConstraint5;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.Expr3ViewItemImpl;
import org.drools.model.view.Expr4ViewItemImpl;
import org.drools.model.view.Expr5ViewItemImpl;
import org.drools.model.view.ExprNViewItem;

public interface SingleConstraint extends Constraint {
    Variable[] getVariables();

    PredicateN getPredicate();

    Index getIndex();

    String getExprId();

    String[] getReactiveProps();

    default boolean isTemporal() {
        return false;
    }

    @Override
    default Type getType() {
        return Type.SINGLE;
    }

    SingleConstraint EMPTY = new AbstractSingleConstraint("EMPTY") {
        @Override
        public Variable[] getVariables() {
            return new Variable[0];
        }

        @Override
        public PredicateN getPredicate() {
            return PredicateN.True;
        }

        @Override
        public AndConstraints and(Constraint constraint ) {
            return new AndConstraints(constraint);
        }

        @Override
        public boolean isEqualTo( ModelComponent other ) {
            return this == other;
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
        throw new UnsupportedOperationException( "Unknow expr type: " + expr.getClass() );
    }
}
