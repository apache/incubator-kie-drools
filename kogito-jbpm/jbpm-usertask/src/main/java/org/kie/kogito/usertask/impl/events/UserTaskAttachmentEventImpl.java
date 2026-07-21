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

package org.kie.kogito.usertask.impl.events;

import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.events.UserTaskAttachmentEvent;
import org.kie.kogito.usertask.model.Attachment;

public class UserTaskAttachmentEventImpl extends UserTaskEventImpl implements UserTaskAttachmentEvent {

    private static final long serialVersionUID = 3956348350804141924L;
    private Attachment oldAttachment;
    private Attachment newAttachment;

    public UserTaskAttachmentEventImpl(UserTaskInstance userTaskInstance, String user) {
        super(userTaskInstance, user);
    }

    public void setOldAttachment(Attachment oldAttachment) {
        this.oldAttachment = oldAttachment;
    }

    public void setNewAttachment(Attachment newAttachment) {
        this.newAttachment = newAttachment;
    }

    @Override
    public Attachment getOldAttachment() {
        return oldAttachment;
    }

    @Override
    public Attachment getNewAttachment() {
        return newAttachment;
    }

    @Override
    public String toString() {
        return "UserTaskAttachmentEventImpl [oldAttachment=" + oldAttachment + ", newAttachment=" + newAttachment + "]";
    }

}
