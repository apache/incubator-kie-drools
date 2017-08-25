package ${package};

import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

public class ${classPrefix}WorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        try {

            // handler impl here

            manager.completeWorkItem(workItem.getId(),
                                     null);
        } catch(Throwable cause) {
            handleException(cause);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // stub
    }
}


