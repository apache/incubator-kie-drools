package org.drools.reliability.core;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Storage;
import org.drools.core.phreak.PropagationList;
import org.drools.kiesession.agenda.DefaultAgenda;

import static org.drools.reliability.core.ReliablePropagationList.PROPAGATION_LIST;

public class ReliableAgenda extends DefaultAgenda {

    public ReliableAgenda(InternalWorkingMemory workingMemory) {
        super( workingMemory );
    }

    @Override
    protected PropagationList createPropagationList() {
        Storage<String, Object> componentsStorage = StorageManagerFactory.get().getStorageManager().getOrCreateStorageForSession(workingMemory, "components");
        ReliablePropagationList propagationList = (ReliablePropagationList) componentsStorage.get(PROPAGATION_LIST);
        if (propagationList == null) {
            propagationList = new ReliablePropagationList(workingMemory);
            componentsStorage.put(PROPAGATION_LIST, propagationList);
        } else {
            propagationList.setReteEvaluator(workingMemory);
        }
        return propagationList;
    }
}
