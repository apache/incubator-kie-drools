/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task.identity;

import java.util.List;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import org.drools.core.util.StringUtils;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.task.Attachment;
import org.jbpm.task.Content;
import org.jbpm.task.User;
import org.jbpm.task.api.TaskAttachmentService;

/**
 *
 */
@Decorator
public class UserGroupTaskAttachmentDecorator implements TaskAttachmentService {

    @Inject
    @Delegate
    private TaskAttachmentService attachmentService;

    @Inject
    private UserGroupCallback userGroupCallback;
    
    @Inject
    private JbpmServicesPersistenceManager pm;
    
    public long addAttachment(long taskId, Attachment attachment, Content content) {
        doCallbackOperationForAttachment(attachment);
        long attachmentId = attachmentService.addAttachment(taskId, attachment, content);
        return attachmentId;
    }

    public void deleteAttachment(long taskId, long attachmentId) {
        attachmentService.deleteAttachment(taskId, attachmentId);
    }

    public List<Attachment> getAllAttachmentsByTaskId(long taskId) {
        return attachmentService.getAllAttachmentsByTaskId(taskId);
    }

    public Attachment getAttachmentById(long attachId) {
        return attachmentService.getAttachmentById(attachId);
    }

    private void doCallbackOperationForAttachment(Attachment attachment) {
        if (attachment != null) {
            if (attachment.getAttachedBy() != null) {
                doCallbackUserOperation(attachment.getAttachedBy().getId());
            }
        }
    }

    private boolean doCallbackUserOperation(String userId) {

        if (userId != null && userGroupCallback.existsUser(userId)) {
            addUserFromCallbackOperation(userId);
            return true;
        }
        return false;

    }
    
    private void addUserFromCallbackOperation(String userId) {
        try {
            boolean userExists = pm.find(User.class, userId) != null;
            if (!StringUtils.isEmpty(userId) && !userExists) {
                User user = new User(userId);
                pm.persist(user);
            }
        } catch (Throwable t) {
            //logger.log(Level.SEVERE, "Unable to add user " + userId);
        }
    }
}
