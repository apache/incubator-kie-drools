package org.drools.reliability.core;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.base.rule.EntryPointId;
import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.kie.api.runtime.conf.PersistedSessionOption;

public class ReliableNamedEntryPoint extends NamedEntryPoint {

    public ReliableNamedEntryPoint(EntryPointId entryPoint, EntryPointNode entryPointNode, ReteEvaluator reteEvaluator) {
        super(entryPoint, entryPointNode, reteEvaluator);
    }

    @Override
    protected ObjectStore createObjectStore(EntryPointId entryPoint, RuleBaseConfiguration conf, ReteEvaluator reteEvaluator) {
        boolean storesOnlyStrategy = reteEvaluator.getSessionConfiguration().getPersistedSessionOption().getPersistenceStrategy() == PersistedSessionOption.PersistenceStrategy.STORES_ONLY;
        return storesOnlyStrategy ?
                SimpleReliableObjectStoreFactory.get().createSimpleReliableObjectStore(StorageManagerFactory.get().getStorageManager().getOrCreateStorageForSession(reteEvaluator, "ep" + getEntryPointId()),
                        reteEvaluator.getSessionConfiguration().getPersistedSessionOption()) :
                new FullReliableObjectStore(StorageManagerFactory.get().getStorageManager().getOrCreateStorageForSession(reteEvaluator, "ep" + getEntryPointId()));
    }

    public void safepoint() {
        ((SimpleReliableObjectStore) getObjectStore()).safepoint();
    }
}
