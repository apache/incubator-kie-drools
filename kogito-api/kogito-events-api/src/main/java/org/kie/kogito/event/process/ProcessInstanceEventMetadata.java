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

package org.kie.kogito.event.process;

public final class ProcessInstanceEventMetadata {

    public static final String PROCESS_ID_META_DATA = "kogito.process.id";
    public static final String PROCESS_TYPE_META_DATA = "kogito.process.type";
    public static final String PROCESS_VERSION_META_DATA = "kogito.process.version";

    public static final String PROCESS_INSTANCE_ID_META_DATA = "kogito.processinstance.id";
    public static final String PROCESS_INSTANCE_STATE_META_DATA = "kogito.processinstance.state";
    public static final String PARENT_PROCESS_INSTANCE_ID_META_DATA = "kogito.processinstance.parentInstanceId";

    public static final String ROOT_PROCESS_INSTANCE_ID_META_DATA = "kogito.processinstance.rootInstanceId";
    public static final String ROOT_PROCESS_ID_META_DATA = "kogito.processinstance.rootProcessId";

    private ProcessInstanceEventMetadata() {
        // nothing to comment
    }
}
