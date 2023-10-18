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

import java.util.Set;

import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.Comment;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;

public interface InternalHumanTaskWorkItem extends HumanTaskWorkItem, InternalKogitoWorkItem {

    void setTaskName(String parameter);

    void setTaskDescription(String parameter);

    void setTaskPriority(String parameter);

    void setReferenceName(String parameter);

    void setActualOwner(String string);

    void setPotentialUsers(Set<String> potentialUsers);

    void setPotentialGroups(Set<String> potentialGroups);

    void setAdminGroups(Set<String> potentialGroups);

    void setAdminUsers(Set<String> adminUsers);

    void setExcludedUsers(Set<String> excludedUsers);

    void setAttachment(String id, Attachment attachment);

    void setComment(String id, Comment comment);

    Comment removeComment(String id);

    Attachment removeAttachment(String id);

}
