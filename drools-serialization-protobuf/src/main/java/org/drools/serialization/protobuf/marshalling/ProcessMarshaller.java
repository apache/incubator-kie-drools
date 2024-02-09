/**
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
package org.drools.serialization.protobuf.marshalling;

import java.io.IOException;
import java.util.List;

import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.kie.api.runtime.process.ProcessInstance;
import org.drools.core.process.WorkItem;

public interface ProcessMarshaller {

    void writeProcessInstances( MarshallerWriteContext context ) throws IOException;

    void writeProcessTimers( MarshallerWriteContext context ) throws IOException;

    void writeWorkItems( MarshallerWriteContext context ) throws IOException;

    List<ProcessInstance> readProcessInstances( MarshallerReaderContext context ) throws IOException;

    void readProcessTimers( MarshallerReaderContext context ) throws IOException, ClassNotFoundException;

    void readWorkItems( MarshallerReaderContext context ) throws IOException;

    void init(MarshallerReaderContext context);

    void writeWorkItem( MarshallerWriteContext context, WorkItem workItem);

    WorkItem readWorkItem(MarshallerReaderContext context);

}
