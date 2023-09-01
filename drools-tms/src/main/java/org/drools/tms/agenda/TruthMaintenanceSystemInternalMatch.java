package org.drools.tms.agenda;

import org.drools.core.common.ActivationsManager;
import org.drools.tms.beliefsystem.ModedAssertion;
import org.drools.tms.SimpleMode;
import org.drools.tms.LogicalDependency;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.LinkedList;

public interface TruthMaintenanceSystemInternalMatch<T extends ModedAssertion<T>> extends InternalMatch {

    void addBlocked(final LogicalDependency<SimpleMode> node);

    LinkedList<LogicalDependency<SimpleMode>> getBlocked();

    void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified);

    void addLogicalDependency(LogicalDependency<T> node);

    LinkedList<LogicalDependency<T>> getLogicalDependencies();

    void setLogicalDependencies(LinkedList<LogicalDependency<T>> justified);

    LinkedList<SimpleMode> getBlockers();

    void removeAllBlockersAndBlocked(ActivationsManager activationsManager);

    void removeBlocked(LogicalDependency<SimpleMode> dep);
}
