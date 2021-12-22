/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.tms.agenda;

import org.drools.tms.beliefsystem.ModedAssertion;
import org.drools.tms.SimpleMode;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.tms.LogicalDependency;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;

public class TruthMaintenanceSystemAgendaItemImpl<T extends ModedAssertion<T>> extends RuleAgendaItem implements TruthMaintenanceSystemAgendaItem<T> {
    private LinkedList<LogicalDependency<T>> justified;
    private LinkedList<LogicalDependency<SimpleMode>> blocked;
    private LinkedList<SimpleMode> blockers;

    public TruthMaintenanceSystemAgendaItemImpl() {
    }

    public TruthMaintenanceSystemAgendaItemImpl(long activationNumber, Tuple tuple, int salience, PropagationContext context, PathMemory pmem, TerminalNode rtn, boolean declarativeAgendaEnabled, InternalAgendaGroup agendaGroup) {
        super(activationNumber, tuple, salience, context, pmem, rtn, declarativeAgendaEnabled, agendaGroup);
    }

    @Override
    public void addBlocked(final LogicalDependency<SimpleMode> dep) {
        // Adds the blocked to the blockers list
        if (this.blocked == null) {
            this.blocked = new LinkedList<>();
        }

        this.blocked.add(dep);

        // now ad the blocker to the blocked's list - we need to check that references are null first
        TruthMaintenanceSystemAgendaItemImpl blocked = (TruthMaintenanceSystemAgendaItemImpl) dep.getJustified();
        if (blocked.blockers == null) {
            blocked.blockers = new LinkedList<SimpleMode>();
            blocked.blockers.add( dep.getMode());
        } else if (dep.getMode().getNext() == null && dep.getMode().getPrevious() == null && blocked.getBlockers().getFirst() != dep.getMode()) {
            blocked.blockers.add(dep.getMode());
        }
    }

    @Override
    public void removeAllBlockersAndBlocked(ActivationsManager activationsManager) {
        if (this.blockers != null) {
            // Iterate and remove this node's logical dependency list from each of it's blockers
            for (LinkedListEntry<SimpleMode, LogicalDependency<SimpleMode>> node = blockers.getFirst(); node != null; node = node.getNext()) {
                LogicalDependency dep = node.getObject();
                dep.getJustifier().getBlocked().remove(dep);
            }
        }
        this.blockers = null;

        if (this.blocked != null) {
            // Iterate and remove this node's logical dependency list from each of it's blocked
            for (LogicalDependency<SimpleMode> dep = blocked.getFirst(); dep != null; ) {
                LogicalDependency<SimpleMode> tmp = dep.getNext();
                removeBlocked(dep);
                TruthMaintenanceSystemAgendaItemImpl justified = (TruthMaintenanceSystemAgendaItemImpl) dep.getJustified();
                if (justified.getBlockers().isEmpty()) {
                    activationsManager.stageLeftTuple(null,justified);
                }
                dep = tmp;
            }
        }
        this.blocked = null;
    }

    @Override
    public void removeBlocked(final LogicalDependency<SimpleMode> dep) {
        this.blocked.remove(dep);

        TruthMaintenanceSystemAgendaItemImpl blocked = (TruthMaintenanceSystemAgendaItemImpl) dep.getJustified();
        blocked.blockers.remove(dep.getMode());
    }

    @Override
    public LinkedList<LogicalDependency<SimpleMode>> getBlocked() {
        return this.blocked;
    }

    @Override
    public void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified) {
        this.blocked = justified;
    }

    @Override
    public LinkedList<SimpleMode> getBlockers() {
        return this.blockers;
    }

    @Override
    public void addLogicalDependency(final LogicalDependency<T> node) {
        if (this.justified == null) {
            this.justified = new LinkedList<>();
        }

        this.justified.add(node);
    }

    @Override
    public LinkedList<LogicalDependency<T>> getLogicalDependencies() {
        return this.justified;
    }

    @Override
    public void setLogicalDependencies(LinkedList<LogicalDependency<T>> justified) {
        this.justified = justified;
    }
}
