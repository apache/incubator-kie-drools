package org.kie.kogito.index.storage;

import java.util.ArrayList;

import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.storage.merger.UserTaskInstanceAssignmentDataEventMerger;
import org.kie.kogito.index.storage.merger.UserTaskInstanceAttachmentDataEventMerger;
import org.kie.kogito.index.storage.merger.UserTaskInstanceCommentDataEventMerger;
import org.kie.kogito.index.storage.merger.UserTaskInstanceDeadlineDataEventMerger;
import org.kie.kogito.index.storage.merger.UserTaskInstanceEventMerger;
import org.kie.kogito.index.storage.merger.UserTaskInstanceStateEventMerger;
import org.kie.kogito.index.storage.merger.UserTaskInstanceVariableDataEventMerger;
import org.kie.kogito.persistence.api.Storage;

public class ModelUserTaskInstanceStorage extends ModelStorageFetcher<UserTaskInstance> implements UserTaskInstanceStorage {

    private final UserTaskInstanceAssignmentDataEventMerger assignmentMerger = new UserTaskInstanceAssignmentDataEventMerger();
    private final UserTaskInstanceAttachmentDataEventMerger attachmentMerger = new UserTaskInstanceAttachmentDataEventMerger();
    private final UserTaskInstanceCommentDataEventMerger commentMerger = new UserTaskInstanceCommentDataEventMerger();
    private final UserTaskInstanceDeadlineDataEventMerger deadlineMerger = new UserTaskInstanceDeadlineDataEventMerger();
    private final UserTaskInstanceVariableDataEventMerger variableMerger = new UserTaskInstanceVariableDataEventMerger();
    private final UserTaskInstanceStateEventMerger stateMerger = new UserTaskInstanceStateEventMerger();

    public ModelUserTaskInstanceStorage(Storage<String, UserTaskInstance> storage) {
        super(storage);
    }

    @Override
    public void indexAssignment(UserTaskInstanceAssignmentDataEvent event) {
        index(event, assignmentMerger);

    }

    @Override
    public void indexAttachment(UserTaskInstanceAttachmentDataEvent event) {
        index(event, attachmentMerger);

    }

    @Override
    public void indexDeadline(UserTaskInstanceDeadlineDataEvent event) {
        index(event, deadlineMerger);
    }

    @Override
    public void indexState(UserTaskInstanceStateDataEvent event) {
        index(event, stateMerger);

    }

    @Override
    public void indexVariable(UserTaskInstanceVariableDataEvent event) {
        index(event, variableMerger);

    }

    @Override
    public void indexComment(UserTaskInstanceCommentDataEvent event) {
        index(event, commentMerger);

    }

    private <T extends UserTaskInstanceDataEvent<?>> void index(T event, UserTaskInstanceEventMerger merger) {
        UserTaskInstance taskInstance = storage.get(event.getKogitoUserTaskInstanceId());
        if (taskInstance == null) {
            taskInstance = new UserTaskInstance();
            taskInstance.setId(event.getKogitoUserTaskInstanceId());
            taskInstance.setProcessInstanceId(event.getKogitoProcessInstanceId());
            taskInstance.setProcessId(event.getKogitoProcessId());
            taskInstance.setRootProcessId(event.getKogitoRootProcessId());
            taskInstance.setRootProcessInstanceId(event.getKogitoRootProcessInstanceId());
            taskInstance.setAttachments(new ArrayList<>());
            taskInstance.setComments(new ArrayList<>());
        }
        storage.put(event.getKogitoUserTaskInstanceId(), merger.merge(taskInstance, event));
    }
}
