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
package org.jbpm.flow.serialization;

import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;

public interface ProcessInstanceMarshallerListener {

    default void afterUnmarshallProcess(KogitoProcessRuntime runtime, KogitoWorkflowProcessInstance processInstance) {

    }

    default void afterUnmarshallNode(KogitoProcessRuntime runtime, KogitoNodeInstance node) {

    }

    default void beforeMarshallNode(KogitoProcessRuntime runtime, KogitoNodeInstance node) {

    }

    default void beforeMarshallProcess(KogitoProcessRuntime runtime, KogitoWorkflowProcessInstance node) {

    }

}
