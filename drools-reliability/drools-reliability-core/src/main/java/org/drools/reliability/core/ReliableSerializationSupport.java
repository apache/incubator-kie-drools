/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private Map<Long, Map<String, Map<Long, WeakReference<WorkingMemoryReteExpireAction>>>> expireActionMap = new HashMap<>(); // keys are persistedSessionId, entryPointName, eventFactHandleId

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
        out.writeObject(workingMemoryReteExpireAction.getFactHandle().getEntryPointName());
        out.writeLong(workingMemoryReteExpireAction.getFactHandle().getId());
    }

    @Override
    public void readWorkingMemoryReteExpireAction(ObjectInput in, WorkingMemoryReteExpireAction workingMemoryReteExpireAction) throws IOException, ClassNotFoundException {
        long persistedSessionId = in.readLong();
        String entryPointName = (String) in.readObject();
        long handleId = in.readLong();
        Map<String, Map<Long, WeakReference<WorkingMemoryReteExpireAction>>> expireActionMapPerSession = expireActionMap.computeIfAbsent(persistedSessionId, key -> new HashMap<>());
        Map<Long, WeakReference<WorkingMemoryReteExpireAction>> expireActionMapPerEntryPoint = expireActionMapPerSession.computeIfAbsent(entryPointName, key -> new HashMap<>());
        expireActionMapPerEntryPoint.put(handleId, new WeakReference<>(workingMemoryReteExpireAction));
    }

    @Override
    public void associateDefaultEventHandleForExpiration(long oldHandleId, DefaultEventHandle newDefaultEventHandle) {
        long persistedSessionId = getSessionIdentifier(newDefaultEventHandle.getReteEvaluator());
        String entryPointName = newDefaultEventHandle.getEntryPointName();
        if (expireActionMap.containsKey(persistedSessionId)
                && expireActionMap.get(persistedSessionId).containsKey(entryPointName)
                && expireActionMap.get(persistedSessionId).get(entryPointName).containsKey(oldHandleId)) {
            WorkingMemoryReteExpireAction workingMemoryReteExpireAction = expireActionMap.get(persistedSessionId).get(entryPointName).get(oldHandleId).get();
            if (workingMemoryReteExpireAction != null) { // if GC'ed, null
                workingMemoryReteExpireAction.setFactHandle(newDefaultEventHandle);
            }
        }
    }
}
