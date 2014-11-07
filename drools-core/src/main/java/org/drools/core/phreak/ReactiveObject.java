package org.drools.core.phreak;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.AllSetBitMask;

import java.util.HashSet;
import java.util.Set;

public abstract class ReactiveObject {
    private InternalFactHandle factHandle;
    private PropagationContextFactory pctxFactory;

    private Set<ObjectSink> sinks = new HashSet<ObjectSink>();

    private Set<ReactiveObject> parents = new HashSet<ReactiveObject>();

    public InternalFactHandle getFactHandle() {
        return factHandle;
    }

    public void setFactHandle(InternalFactHandle factHandle) {
        this.factHandle = factHandle;
        if (pctxFactory == null) {
            InternalWorkingMemoryEntryPoint ep = (InternalWorkingMemoryEntryPoint)factHandle.getEntryPoint();
            pctxFactory = ep.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
        }
    }

    public void addParent(Object parent) {
        if (parent instanceof ReactiveObject) {
            parents.add((ReactiveObject) parent);
        }
    }

    public void addSink(ObjectSink sink) {
        sinks.add(sink);
    }

    protected void notifyModification() {
        if (factHandle != null) {
            propagateModify();
        } else {
            for (ReactiveObject parent : parents) {
                parent.notifyModification();
            }
        }
    }

    private void propagateModify() {
        InternalWorkingMemoryEntryPoint ep = (InternalWorkingMemoryEntryPoint)factHandle.getEntryPoint();
        InternalWorkingMemory wm = ep.getInternalWorkingMemory();

        ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(factHandle.getFirstLeftTuple(),
                                                                             factHandle.getFirstRightTuple(),
                                                                             ep.getEntryPointNode());

        PropagationContext ctx = pctxFactory.createPropagationContext(wm.getNextPropagationIdCounter(),
                                                                      PropagationContext.MODIFICATION,
                                                                      (RuleImpl)null, (LeftTuple)null,
                                                                      factHandle, ep.getEntryPoint(),
                                                                      AllSetBitMask.get(), Object.class, null);

        for (ObjectSink sink : sinks) {
            sink.modifyObject(factHandle, modifyPreviousTuples, ctx, wm);
        }
    }
}
