package org.drools.model;

import org.drools.model.view.ExprViewItem;

public interface ConditionalConsequence extends RuleItem {

    ExprViewItem getExpr();
    Consequence getThen();
    ConditionalConsequence getElse();
}
