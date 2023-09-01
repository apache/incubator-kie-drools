package org.drools.reliability.core;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.Storage;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.conf.PersistedSessionOption;

import static org.drools.reliability.core.StorageManager.getSessionIdentifier;

public class ReliableStatefulKnowledgeSessionImpl extends StatefulKnowledgeSessionImpl implements ReliableKieSession {

    private transient Storage<String, Object> activationsStorage;

    public ReliableStatefulKnowledgeSessionImpl() {
    }

    public ReliableStatefulKnowledgeSessionImpl(long id,
                                                InternalKnowledgeBase kBase,
                                                boolean initInitFactHandle,
                                                SessionConfiguration config,
                                                Environment environment) {
        super(id, kBase, initInitFactHandle, config, environment);
    }

    public ReliableStatefulKnowledgeSessionImpl(long id,
                                                InternalKnowledgeBase kBase,
                                                FactHandleFactory handleFactory,
                                                long propagationContext,
                                                SessionConfiguration config,
                                                Environment environment) {
        super(id, kBase, handleFactory, propagationContext, config, environment);
    }

    @Override
    public void dispose() {
        StorageManagerFactory.get().getStorageManager().removeStoragesBySessionId(String.valueOf(getSessionIdentifier(this)));
        super.dispose();
    }

    @Override
    public void startOperation(InternalOperationType operationType) {
        super.startOperation(operationType);
        if (operationType == InternalOperationType.FIRE) {
            ((ReliableGlobalResolver) getGlobalResolver()).updateStorage();
        }

    }

    @Override
    public void endOperation(InternalOperationType operationType) {
        super.endOperation(operationType);
        if (operationType == InternalOperationType.FIRE) {
            ((ReliableGlobalResolver) getGlobalResolver()).updateStorage();
            if (getSessionConfiguration().getPersistedSessionOption().getSafepointStrategy() == PersistedSessionOption.SafepointStrategy.AFTER_FIRE) {
                safepoint();
            }
        }
    }

    @Override
    public Storage<String, Object> getActivationsStorage() {
        return activationsStorage;
    }

    @Override
    public void setActivationsStorage(Storage<String, Object> activationsStorage) {
        this.activationsStorage = activationsStorage;
    }

    @Override
    public void safepoint() {
        getEntryPoints().stream().map(ReliableNamedEntryPoint.class::cast).forEach(ReliableNamedEntryPoint::safepoint);
        if (getSessionConfiguration().getPersistedSessionOption().getActivationStrategy() == PersistedSessionOption.ActivationStrategy.ACTIVATION_KEY
                && activationsStorage.requiresFlush()) {
            activationsStorage.flush();
        }
    }
}
