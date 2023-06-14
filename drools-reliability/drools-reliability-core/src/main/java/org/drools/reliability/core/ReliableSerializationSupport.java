package org.drools.reliability.core;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.drools.base.time.JobHandle;
import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.SerializationSupport;
import org.drools.core.impl.WorkingMemoryReteExpireAction;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContext;

import static org.drools.reliability.core.StorageManager.getSessionIdentifier;

public class ReliableSerializationSupport implements SerializationSupport {

    private Map<Long, WeakReference<ReteEvaluator>> reteEvaluatorMap = new HashMap<>(); // key is persisted session id

    private Map<Long, Map<Long, WeakReference<WorkingMemoryReteExpireAction>>> expireActionMap = new HashMap<>(); // outer key is persisted session id. inner key is eventFactHandle id

    @Override
    public void registerReteEvaluator(ReteEvaluator reteEvaluator) {
        this.reteEvaluatorMap.put(getSessionIdentifier(reteEvaluator), new WeakReference<>(reteEvaluator));
    }

    @Override
    public void unregisterReteEvaluator(ReteEvaluator reteEvaluator) {
        long persistecSessionId = getSessionIdentifier(reteEvaluator);
        this.reteEvaluatorMap.remove(persistecSessionId);
        this.expireActionMap.remove(persistecSessionId);
    }

    @Override
    public boolean supportsExpireJobContext() {
        return true;
    }

    @Override
    public void writeExpireJobContext(ObjectOutput out, ExpireJobContext expireJobContext) throws IOException {
        out.writeObject(expireJobContext.expireAction);
        out.writeObject(expireJobContext.handle);
        out.writeLong(getSessionIdentifier(expireJobContext.reteEvaluator)); // persisted session id
    }

    @Override
    public void readExpireJobContext(ObjectInput in, ExpireJobContext expireJobContext) throws IOException, ClassNotFoundException {
        expireJobContext.expireAction = (WorkingMemoryReteExpireAction) in.readObject();
        expireJobContext.handle = (JobHandle) in.readObject();
        long sessionId = in.readLong();
        expireJobContext.reteEvaluator = reteEvaluatorMap.get(sessionId).get();
    }

    @Override
    public boolean supportsWorkingMemoryReteExpireAction() {
        return true;
    }

    @Override
    public void writeWorkingMemoryReteExpireAction(ObjectOutput out, WorkingMemoryReteExpireAction workingMemoryReteExpireAction) throws IOException {
        out.writeLong(getSessionIdentifier(workingMemoryReteExpireAction.getFactHandle().getReteEvaluator()));
        out.writeLong(workingMemoryReteExpireAction.getFactHandle().getId());
        System.out.println("writeWorkingMemoryReteExpireAction : " + workingMemoryReteExpireAction.getFactHandle().getId());
        System.out.println("  => " + workingMemoryReteExpireAction.getFactHandle().getObject());
    }

    @Override
    public void readWorkingMemoryReteExpireAction(ObjectInput in, WorkingMemoryReteExpireAction workingMemoryReteExpireAction) throws IOException, ClassNotFoundException {
        long persistedSessionId = in.readLong();
        long handleId = in.readLong();
        Map<Long, WeakReference<WorkingMemoryReteExpireAction>> expireActionMapPerSession = expireActionMap.computeIfAbsent(persistedSessionId, key -> new HashMap<>());
        expireActionMapPerSession.put(handleId, new WeakReference<>(workingMemoryReteExpireAction));
        System.out.println("expireActionMap : " + expireActionMap);
    }

    @Override
    public void associateDefaultEventHandleForExpiration(long oldHandleId, DefaultEventHandle newDefaultEventHandle) {
        System.out.println("associateDefaultEventHandleForExpiration");
        System.out.println("  oldHandleId : " + oldHandleId);
        System.out.println("  newDefaultEventHandle : " + newDefaultEventHandle);
        long persistedSessionId = getSessionIdentifier(newDefaultEventHandle.getReteEvaluator());
        if (expireActionMap.containsKey(persistedSessionId)) {
            Map<Long, WeakReference<WorkingMemoryReteExpireAction>> expireActionMapPerSession = expireActionMap.get(persistedSessionId);
            if (expireActionMapPerSession.containsKey(oldHandleId)) {
                WorkingMemoryReteExpireAction workingMemoryReteExpireAction = expireActionMapPerSession.get(oldHandleId).get();
                workingMemoryReteExpireAction.setFactHandle(newDefaultEventHandle);
            }
        }
    }
}
