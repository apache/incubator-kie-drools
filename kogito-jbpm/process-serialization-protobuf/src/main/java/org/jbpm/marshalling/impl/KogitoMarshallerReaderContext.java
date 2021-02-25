/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.marshalling.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.common.BaseNode;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.KogitoSerializablePlaceholderResolverStrategy;
import org.drools.serialization.protobuf.ProtobufMarshallerReaderContext;
import org.drools.serialization.protobuf.TimersInputMarshaller;
import org.kie.api.definition.process.Process;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;

public class KogitoMarshallerReaderContext extends ProtobufMarshallerReaderContext {

    public Map<String, Process> processes = new HashMap<>();

    public KogitoMarshallerReaderContext(InputStream stream,
            InternalKnowledgeBase kBase,
            Map<Integer, BaseNode> sinks,
            ObjectMarshallingStrategyStore resolverStrategyFactory,
            Map<Integer, TimersInputMarshaller> timerReaders,
            Environment env) throws IOException {
        this(stream,
                kBase,
                sinks,
                resolverStrategyFactory,
                timerReaders,
                true,
                true,
                env);
    }

    public KogitoMarshallerReaderContext(InputStream stream,
            Map<String, Process> processes,
            Map<Integer, BaseNode> sinks,
            ObjectMarshallingStrategyStore resolverStrategyFactory,
            Map<Integer, TimersInputMarshaller> timerReaders,
            Environment env) throws IOException {
        this(stream,
                null,
                sinks,
                resolverStrategyFactory,
                timerReaders,
                true,
                true,
                env);
        this.processes = processes;
    }

    public KogitoMarshallerReaderContext(InputStream stream,
            InternalKnowledgeBase kBase,
            Map<Integer, BaseNode> sinks,
            ObjectMarshallingStrategyStore resolverStrategyFactory,
            Map<Integer, TimersInputMarshaller> timerReaders,
            boolean marshalProcessInstances,
            boolean marshalWorkItems,
            Environment env) throws IOException {
        super(stream, kBase, sinks, resolverStrategyFactory, timerReaders,
                marshalProcessInstances, marshalWorkItems, env);

        if (this.getKnowledgeBase() != null) {
            this.getKnowledgeBase().getProcesses().forEach(p -> this.processes.put(p.getId(), p));
        }
    }

    @Override
    protected ObjectMarshallingStrategy[] getMarshallingStrategy() {
        return new ObjectMarshallingStrategy[] { new KogitoSerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT) };
    }

    public Process getProcess(String processId) {
        return processes.get(processId);
    }
}
