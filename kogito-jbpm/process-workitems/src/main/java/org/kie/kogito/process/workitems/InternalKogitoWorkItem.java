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
package org.kie.kogito.process.workitems;

import java.util.Date;

import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

public interface InternalKogitoWorkItem extends org.drools.core.process.WorkItem, org.kie.kogito.internal.process.workitem.KogitoWorkItem {

    void setExternalReferenceId(String id);

    void setActualOwner(String owner);

    void setProcessInstanceId(String processInstanceId);

    void setNodeInstanceId(String deploymentId);

    String getNodeInstanceStringId();

    void setPhaseId(String phaseId);

    void setPhaseStatus(String phaseStatus);

    void setStartDate(Date date);

    void setCompleteDate(Date date);

    void setNodeInstance(KogitoNodeInstance nodeInstance);

    void setProcessInstance(KogitoProcessInstance processInstance);

    void setId(String string);
}
