package org.drools.tms;

import org.drools.core.util.LinkedListNode;
import org.drools.tms.agenda.TruthMaintenanceSystemInternalMatch;
import org.drools.tms.beliefsystem.ModedAssertion;

public interface LogicalDependency<M extends ModedAssertion<M>> extends LinkedListNode<LogicalDependency<M>> {

    Object getJustified();

    TruthMaintenanceSystemInternalMatch<M> getJustifier();

    Object getObject();

    M getMode();
}
