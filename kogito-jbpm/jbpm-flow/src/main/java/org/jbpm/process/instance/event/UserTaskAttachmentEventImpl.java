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

package org.jbpm.process.instance.event;

import java.net.URI;
import java.util.Date;

import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.kie.api.event.usertask.UserTaskAttachmentEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.process.workitem.Attachment;

public class UserTaskAttachmentEventImpl extends UserTaskEventImpl implements UserTaskAttachmentEvent {

    private static final long serialVersionUID = 3956348350804141924L;
    private Attachment oldAttachment;
    private Attachment newAttachment;

    public UserTaskAttachmentEventImpl(ProcessInstance instance, HumanTaskNodeInstance nodeInstance, KieRuntime kruntime, String user) {
        super(instance, nodeInstance, kruntime, user);
    }

    public void setOldAttachment(Attachment oldAttachment) {
        this.oldAttachment = oldAttachment;
    }

    public void setNewAttachment(Attachment newAttachment) {
        this.newAttachment = newAttachment;
    }

    @Override
    public org.kie.api.event.usertask.Attachment getOldAttachment() {
        return wrap(oldAttachment);
    }

    @Override
    public org.kie.api.event.usertask.Attachment getNewAttachment() {
        return wrap(newAttachment);
    }

    private org.kie.api.event.usertask.Attachment wrap(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        return new org.kie.api.event.usertask.Attachment() {

            @Override
            public String getAttachmentId() {
                return attachment.getId();
            }

            @Override
            public String getAttachmentName() {
                return attachment.getName();
            }

            @Override
            public URI getAttachmentURI() {
                return attachment.getContent();
            }

            @Override
            public String getUpdatedBy() {
                return attachment.getUpdatedBy();
            }

            @Override
            public Date getUpdatedAt() {
                return attachment.getUpdatedAt();
            }

        };
    }
}
