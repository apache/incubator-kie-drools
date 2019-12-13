package org.drools.model.constraints;

import java.util.Collections;
import java.util.List;

import org.drools.model.BitMask;
import org.drools.model.Constraint;
import org.drools.model.Index;
import org.drools.model.SingleConstraint;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.ExprViewItem;

public abstract class AbstractSingleConstraint extends AbstractConstraint implements SingleConstraint, ModelComponent {

    private final String exprId;

    private Index index;

    private ReactivitySpecs reactivitySpecs = ReactivitySpecs.EMPTY;

    protected AbstractSingleConstraint(String exprId) {
        this.exprId = exprId;
    }

    @Override
    public List<Constraint> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    @Override
    public String[] getReactiveProps() {
        return reactivitySpecs.getProps();
    }

    @Override
    public BitMask getReactivityBitMask() {
        return reactivitySpecs.getBitMask();
    }

    public void setReactivitySpecs( ReactivitySpecs reactivitySpecs ) {
        this.reactivitySpecs = reactivitySpecs;
    }

    @Override
    public String getExprId() {
        return exprId;
    }

    @Override
    public String toString() {
        return "Constraint for '" + exprId + "' (index: " + index + ")";
    }

    public static AbstractSingleConstraint fromExpr( ExprViewItem expr ) {
        if (expr instanceof Expr1ViewItemImpl) {
            return new SingleConstraint1( (Expr1ViewItemImpl) expr );
        }
        if (expr instanceof Expr2ViewItemImpl) {
            return new SingleConstraint2( (Expr2ViewItemImpl) expr );
        }
        throw new UnsupportedOperationException( "Unknown expr: " + expr );
    }

    protected <T extends AbstractSingleConstraint> T negate(T negated) {
        if ( index != null ) {
            negated.setIndex( index.negate() );
        }
        negated.setReactivitySpecs( reactivitySpecs );
        return negated;
    }
}
