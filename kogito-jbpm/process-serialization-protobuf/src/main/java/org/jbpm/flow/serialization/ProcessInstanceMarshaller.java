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

import java.io.IOException;

import org.kie.kogito.process.ProcessInstance;

/**
 * A ProcessInstanceMarshaller must contain all the write/read logic for nodes
 * of a specific ProcessInstance. It colaborates with OutputMarshaller and
 * InputMarshaller, that delegates in a ProcessInstanceMarshaller to stream in/out runtime
 * information.
 *
 * @see org.drools.core.marshalling.impl.OutputMarshaller
 * @see ProcessInstanceMarshallerFactory
 */

public interface ProcessInstanceMarshaller {

    void writeProcessInstance(MarshallerWriterContext context, ProcessInstance<?> processInstance) throws IOException;

    ProcessInstance<?> readProcessInstance(MarshallerReaderContext context) throws IOException;

    void reloadProcessInstance(MarshallerReaderContext context, ProcessInstance<?> processInstance) throws IOException;

}
