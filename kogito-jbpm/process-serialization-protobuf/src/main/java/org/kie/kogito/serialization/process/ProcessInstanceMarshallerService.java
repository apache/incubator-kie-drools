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
package org.kie.kogito.serialization.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.serialization.process.impl.ProtobufProcessInstanceMarshallerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessInstanceMarshallerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInstanceMarshallerService.class);

    private List<ObjectMarshallerStrategy> strats;

    private Map<MarshallerContextName<Object>, Object> contextEntries;

    private ProcessInstanceMarshallerFactory processInstanceMarshallerFactory;

    public class Builder {

        public Builder() {
            ProcessInstanceMarshallerService.this.processInstanceMarshallerFactory = new ProtobufProcessInstanceMarshallerFactory();
        }

        public Builder withProcessInstanceMarshallerFactory(ProcessInstanceMarshallerFactory factory) {
            ProcessInstanceMarshallerService.this.processInstanceMarshallerFactory = factory;
            return this;
        }

        public <T> Builder withContextEntries(Map<MarshallerContextName<T>, T> contextEntries) {
            for (Map.Entry<MarshallerContextName<T>, T> item : contextEntries.entrySet()) {
                ProcessInstanceMarshallerService.this.contextEntries.put((MarshallerContextName<Object>) item.getKey(), (Object) item.getValue());
            }
            return this;
        }

        public Builder withDefaultObjectMarshallerStrategies() {
            ServiceLoader<ObjectMarshallerStrategy> loader = ServiceLoader.load(ObjectMarshallerStrategy.class);

            for (ObjectMarshallerStrategy strategy : loader) {
                ProcessInstanceMarshallerService.this.strats.add(strategy);
            }
            return this;
        }

        public Builder withObjectMarshallerStrategies(ObjectMarshallerStrategy... strategies) {
            for (ObjectMarshallerStrategy strategy : strategies) {
                ProcessInstanceMarshallerService.this.strats.add(strategy);
            }
            return this;
        }

        public ProcessInstanceMarshallerService build() {
            Collections.sort(ProcessInstanceMarshallerService.this.strats);
            return ProcessInstanceMarshallerService.this;
        }

    }

    public static Builder newBuilder() {
        return new ProcessInstanceMarshallerService().new Builder();
    }

    private ProcessInstanceMarshallerService() {
        this.strats = new ArrayList<>();
        this.contextEntries = new HashMap<>();
    }

    protected void setupEnvironment(MarshallerContext env) {
        env.set(MarshallerContextName.OBJECT_MARSHALLING_STRATEGIES, strats.toArray(new ObjectMarshallerStrategy[strats.size()]));

        for (Map.Entry<MarshallerContextName<Object>, Object> entry : contextEntries.entrySet()) {
            env.set(entry.getKey(), entry.getValue());
        }
    }

    public byte[] marshallProcessInstance(ProcessInstance<?> processInstance) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            MarshallerWriterContext context = processInstanceMarshallerFactory.newWriterContext(baos);
            setupEnvironment(context);
            org.kie.kogito.serialization.process.ProcessInstanceMarshaller marshaller = processInstanceMarshallerFactory.newKogitoProcessInstanceMarshaller();
            marshaller.writeProcessInstance(context, processInstance);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ProcessInstanceMarshallerException("Error while marshalling process instance", e);
        }
    }

    public ProcessInstance<?> unmarshallProcessInstance(byte[] data, Process<?> process, boolean readOnly) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            MarshallerReaderContext context = processInstanceMarshallerFactory.newReaderContext(bais);
            context.set(MarshallerContextName.MARSHALLER_PROCESS, process);
            context.set(MarshallerContextName.MARSHALLER_INSTANCE_READ_ONLY, readOnly);
            setupEnvironment(context);
            org.kie.kogito.serialization.process.ProcessInstanceMarshaller marshaller = processInstanceMarshallerFactory.newKogitoProcessInstanceMarshaller();
            return (ProcessInstance<?>) marshaller.readProcessInstance(context);
        } catch (Exception e) {
            throw new ProcessInstanceMarshallerException("Error while unmarshalling process instance", e);
        }
    }

    public ProcessInstance<?> unmarshallProcessInstance(byte[] data, Process<?> process) {
        return unmarshallProcessInstance(data, process, false);
    }

    public ProcessInstance<?> unmarshallReadOnlyProcessInstance(byte[] data, Process<?> process) {
        return unmarshallProcessInstance(data, process, true);
    }

    public Consumer<AbstractProcessInstance<?>> createdReloadFunction(Supplier<byte[]> dataSupplier) {
        return (processInstance) -> {
            byte[] data = dataSupplier.get();
            if (data == null) {
                LOGGER.warn("Process Instance {} cannot be found", processInstance.id());
            }
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                MarshallerReaderContext context = processInstanceMarshallerFactory.newReaderContext(bais);
                context.set(MarshallerContextName.MARSHALLER_PROCESS, processInstance.process());
                setupEnvironment(context);
                org.kie.kogito.serialization.process.ProcessInstanceMarshaller marshaller =
                        processInstanceMarshallerFactory.newKogitoProcessInstanceMarshaller();
                marshaller.reloadProcessInstance(context, processInstance);
            } catch (Exception e) {
                LOGGER.warn("Process Instance {} cannot be reloaded", processInstance.id(), e);
            }
        };
    }
}
