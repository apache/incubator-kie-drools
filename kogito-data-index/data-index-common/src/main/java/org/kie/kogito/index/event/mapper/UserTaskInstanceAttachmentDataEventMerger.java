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
package org.kie.kogito.index.event.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.DateTimeUtils;
import org.kie.kogito.index.model.Attachment;
import org.kie.kogito.index.model.UserTaskInstance;

@ApplicationScoped
public class UserTaskInstanceAttachmentDataEventMerger implements UserTaskInstanceEventMerger {

    @Override
    public boolean accept(UserTaskInstanceDataEvent<?> event) {
        return event instanceof UserTaskInstanceAttachmentDataEvent;
    }

    @Override
    public void merge(UserTaskInstance userTaskInstance, UserTaskInstanceDataEvent<?> data) {
        UserTaskInstanceAttachmentDataEvent event = (UserTaskInstanceAttachmentDataEvent) data;
        UserTaskInstanceAttachmentEventBody body = event.getData();

        List<Attachment> attachments = Optional.ofNullable(userTaskInstance.getAttachments()).orElse(new ArrayList<>());
        userTaskInstance.setAttachments(attachments);

        switch (body.getEventType()) {
            case UserTaskInstanceAttachmentEventBody.EVENT_TYPE_ADDED:
            case UserTaskInstanceAttachmentEventBody.EVENT_TYPE_CHANGE:
                Optional<Attachment> found = attachments.stream().filter(e -> e.getId().equals(body.getAttachmentId())).findAny();
                Attachment attachment;
                if (found.isEmpty()) {
                    attachment = new Attachment();
                    attachments.add(attachment);
                } else {
                    attachment = found.get();
                }
                attachment.setId(body.getAttachmentId());
                attachment.setName(body.getAttachmentName());
                attachment.setContent(body.getAttachmentURI().toString());
                attachment.setUpdatedBy(body.getEventUser() != null ? body.getEventUser() : "unknown");
                attachment.setUpdatedAt(DateTimeUtils.toZonedDateTime(body.getEventDate()));

                break;
            case UserTaskInstanceAttachmentEventBody.EVENT_TYPE_DELETED:
                attachments.removeIf(e -> e.getId().equals(body.getAttachmentId()));
                break;
        }

    }

}
