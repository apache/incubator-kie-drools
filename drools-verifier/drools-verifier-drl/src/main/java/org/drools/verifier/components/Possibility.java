package org.drools.verifier.components;

import java.util.Set;

import org.drools.verifier.report.components.Cause;

public interface Possibility
    extends
    Cause {

    public Set<? extends RuleComponent> getItems();

    public int getAmountOfItems();
}
