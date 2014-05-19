package org.drools.beliefs.bayes;

import org.drools.core.beliefsystem.defeasible.DefeasibilityStatus;
import org.drools.core.beliefsystem.defeasible.Defeater;
import org.drools.core.beliefsystem.defeasible.Defeats;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.spi.Activation;
import org.drools.core.util.LinkedListEntry;

import java.util.Arrays;

public class BayesLogicalDependency extends SimpleLogicalDependency {


    public BayesLogicalDependency(Activation justifier, Object justified) {
        super(justifier, justified);
    }

    public BayesLogicalDependency(Activation justifier, Object justified, Object object, Object value) {
        super(justifier, justified, object, value);
    }

}
