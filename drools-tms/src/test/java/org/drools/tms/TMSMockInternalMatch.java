package org.drools.tms;

import org.drools.core.common.ActivationsManager;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.util.LinkedList;
import org.drools.kiesession.MockInternalMatch;
import org.drools.tms.agenda.TruthMaintenanceSystemInternalMatch;

public class TMSMockInternalMatch extends MockInternalMatch implements TruthMaintenanceSystemInternalMatch {
    @Override
    public void addBlocked(LogicalDependency node) {

    }

    @Override
    public void setBlocked(LinkedList justified) {

    }

    @Override
    public void addLogicalDependency(LogicalDependency node) {

    }

    @Override
    public LinkedList getLogicalDependencies() {
        return null;
    }

    @Override
    public LinkedList<SimpleMode> getBlockers() {
        return null;
    }

    @Override
    public void setLogicalDependencies(LinkedList justified) {

    }

    @Override
    public void removeAllBlockersAndBlocked(ActivationsManager activationsManager) {

    }

    @Override
    public void removeBlocked(LogicalDependency dep) {

    }

    @Override
    public void setActivationFactHandle(InternalFactHandle factHandle) {

    }

    @Override
    public TerminalNode getTerminalNode() {
        return null;
    }

    @Override
    public String toExternalForm() {
        return null;
    }

    @Override
    public Runnable getCallback() {
        return null;
    }

    @Override
    public void setCallback(Runnable callback) {

    }
}
