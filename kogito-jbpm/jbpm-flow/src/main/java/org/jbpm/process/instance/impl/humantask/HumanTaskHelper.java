/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.process.instance.impl.humantask;

import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.kogito.MapOutput;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.WorkItemNotFoundException;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.AttachmentInfo;
import org.kie.kogito.process.workitem.Comment;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.TaskMetaEntity;

public class HumanTaskHelper {

    private HumanTaskHelper() {
    }

    public static InternalHumanTaskWorkItem decorate(NodeInstance nodeInstance, InternalHumanTaskWorkItem delegate) {
        return new HumanTaskWorkItemDecoratorImpl(nodeInstance, delegate);
    }

    public static InternalHumanTaskWorkItem asHumanTask(KogitoWorkItem item) {
        if (item instanceof InternalHumanTaskWorkItem) {
            return (InternalHumanTaskWorkItem) item;
        } else {
            throw new IllegalArgumentException("Work item " + item.getStringId() + " is not a human task");
        }
    }

    public static Comment addComment(KogitoWorkItem item, String commentInfo, String user) {
        InternalHumanTaskWorkItem humanTask = asHumanTask(item);
        String id = getNewId();
        Comment comment = buildComment(id, commentInfo, user);
        humanTask.setComment(id, comment);
        return comment;
    }

    public static Attachment addAttachment(KogitoWorkItem item, AttachmentInfo attachmentInfo, String user) {
        InternalHumanTaskWorkItem humanTask = asHumanTask(item);
        String id = getNewId();
        Attachment attachment = buildAttachment(id, attachmentInfo, user);
        humanTask.setAttachment(id, attachment);
        return attachment;
    }

    public static Comment updateComment(KogitoWorkItem item, String id, String commentInfo, String user) {
        try {
            InternalHumanTaskWorkItem humanTask = asHumanTask(item);
            Comment comment = humanTask.getComments().get(id);
            if (comment == null) {
                throw new IllegalArgumentException("Comment " + id + " does not exist");
            }
            if (!comment.getUpdatedBy().equals(user)) {
                throw new IllegalArgumentException("User " + user + " did not create the comment, cannot modify it");
            }
            comment = comment.clone();
            humanTask.setComment(id, fillTaskMetaEntity(comment, commentInfo));
            return comment;
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("Attachment could not be modified", e);
        }
    }

    public static Attachment updateAttachment(KogitoWorkItem item,
            String id,
            AttachmentInfo attachmentInfo,
            String user) {
        try {
            InternalHumanTaskWorkItem humanTask = asHumanTask(item);
            Attachment attachment = humanTask.getAttachments().get(id);
            if (attachment == null) {
                throw new IllegalArgumentException("Attachment " + id + " does not exist");
            }
            if (!attachment.getUpdatedBy().equals(user)) {
                throw new IllegalArgumentException("User " + user + " did not create the attachment, cannot modify it");
            }

            attachment = attachment.clone();
            humanTask.setAttachment(id, setAttachmentName(fillTaskMetaEntity(attachment, attachmentInfo.getUri()), attachmentInfo));
            return attachment;
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("Attachment could not be modified", e);
        }

    }

    public static Map<String, Object> updateContent(KogitoWorkItem item, MapOutput model) {
        return updateContent(item, model.toMap());
    }

    public static Map<String, Object> updateContent(KogitoWorkItem item, Map<String, Object> map) {
        InternalHumanTaskWorkItem humanTask = asHumanTask(item);
        humanTask.setResults(map);
        return humanTask.getResults();
    }

    public static boolean deleteComment(KogitoWorkItem item, Object id, String user) {
        InternalHumanTaskWorkItem humanTask = asHumanTask(item);
        Map<Object, Comment> comments = humanTask.getComments();
        Comment comment = comments.get(id);
        if (comment == null || !comment.getUpdatedBy().equals(user)) {
            return false;
        }
        return humanTask.removeComment((String) id) != null;
    }

    public static boolean deleteAttachment(KogitoWorkItem item, Object id, String user) {
        InternalHumanTaskWorkItem humanTask = asHumanTask(item);
        Map<Object, Attachment> attachments = humanTask.getAttachments();
        Attachment attachment = attachments.get(id);
        if (attachment == null || !attachment.getUpdatedBy().equals(user)) {
            return false;
        }
        return humanTask.removeAttachment((String) id) != null;
    }

    public static HumanTaskWorkItem findTask(ProcessInstance<?> pi, String taskId, Policy<?>... policies) {
        return pi.findNodes(ni -> isSearchWorkItem(ni, taskId,
                policies)).stream().findFirst().map(wi -> (HumanTaskWorkItem) ((WorkItemNodeInstance) wi).getWorkItem())
                .orElseThrow(() -> new WorkItemNotFoundException(taskId));
    }

    private static boolean isSearchWorkItem(KogitoNodeInstance ni, String taskId, Policy<?>... policies) {
        return ni instanceof WorkItemNodeInstance && ((WorkItemNodeInstance) ni).getWorkItemId().equals(
                taskId) && ((WorkItemNodeInstance) ni).getWorkItem().enforce(policies) &&
                ((WorkItemNodeInstance) ni).getWorkItem() instanceof HumanTaskWorkItem;
    }

    private static Comment buildComment(String id, String content, String user) {
        return fillTaskMetaEntity(new Comment(id, user), content);
    }

    private static Attachment buildAttachment(String id, AttachmentInfo attachmentInfo, String user) {
        return setAttachmentName(fillTaskMetaEntity(new Attachment(id, user), attachmentInfo.getUri()), attachmentInfo);
    }

    private static Attachment setAttachmentName(Attachment attachment, AttachmentInfo attachmentInfo) {
        String name = attachmentInfo.getName();
        if (name == null) {
            name = Paths.get(attachmentInfo.getUri()).getFileName().toString();
        }
        attachment.setName(name);
        return attachment;
    }

    private static <K extends Serializable, T extends Serializable, C extends TaskMetaEntity<K, T>> C fillTaskMetaEntity(C metaInfo,
            T content) {
        metaInfo.setUpdatedAt(new Date());
        metaInfo.setContent(content);
        return metaInfo;
    }

    private static String getNewId() {
        return UUID.randomUUID().toString();
    }
}
