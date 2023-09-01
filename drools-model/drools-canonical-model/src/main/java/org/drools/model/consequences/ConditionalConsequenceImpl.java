package org.drools.model.consequences;

import org.drools.model.ConditionalConsequence;
import org.drools.model.Consequence;
import org.drools.model.view.ExprViewItem;

public class ConditionalConsequenceImpl implements ConditionalConsequence {

    private final ExprViewItem expr;
    private final Consequence thenConsequence;
    private final ConditionalConsequence elseConsequence;

    public ConditionalConsequenceImpl( ExprViewItem expr, Consequence thenConsequence, ConditionalConsequence elseConsequence ) {
        this.expr = expr;
        this.thenConsequence = thenConsequence;
        this.elseConsequence = elseConsequence;
    }

    @Override
    public ExprViewItem getExpr() {
        return expr;
    }

    @Override
    public Consequence getThen() {
        return thenConsequence;
    }

    @Override
    public ConditionalConsequence getElse() {
        return elseConsequence;
    }
}
