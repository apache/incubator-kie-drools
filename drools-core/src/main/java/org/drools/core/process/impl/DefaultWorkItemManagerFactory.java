package org.drools.core.process.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.WorkItemManager;
import org.drools.core.process.WorkItemManagerFactory;

public class DefaultWorkItemManagerFactory implements WorkItemManagerFactory, Externalizable {

    public WorkItemManager createWorkItemManager(InternalKnowledgeRuntime kruntime) {
        return new DefaultWorkItemManager(kruntime);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

}
