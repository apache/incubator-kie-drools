package org.drools.reliability;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.RuleBase;
import org.drools.core.phreak.PropagationList;
import org.drools.kiesession.agenda.DefaultAgenda;
import org.infinispan.Cache;

public class ReliableAgenda extends DefaultAgenda {

    public ReliableAgenda() { }

    public ReliableAgenda(RuleBase kBase) {
        super( kBase );
    }

    public ReliableAgenda(RuleBase kBase, boolean initMain) {
        super( kBase, initMain );
    }

    @Override
    public void setWorkingMemory(InternalWorkingMemory workingMemory) {
        super.setWorkingMemory(workingMemory);
    }

    @Override
    protected PropagationList createPropagationList() {
        Cache<String, Object> componentsCache = CacheManager.INSTANCE.getOrCreateCacheForSession(workingMemory, "components");
        ReliablePropagationList propagationList = (ReliablePropagationList) componentsCache.get("PropagationList");
        if (propagationList == null) {
            propagationList = new ReliablePropagationList(workingMemory);
            componentsCache.put("PropagationList", propagationList);
        } else {
            propagationList = new ReliablePropagationList(workingMemory, propagationList);
        }
        return propagationList;
    }
}
