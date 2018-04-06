package org.drools.core.process.instance;

import org.drools.core.process.instance.impl.UntypedWorkItemImpl;
import org.kie.api.runtime.process.WorkItem;

public class Types {

    public static <W> W typed(WorkItem workItem) {
        return (W) ((UntypedWorkItemImpl) workItem).getTyped();
    }
}
