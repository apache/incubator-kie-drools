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
package org.kie.kogito.persistence.filesystem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.util.PathUtils;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.kie.kogito.Model;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;

public class FileSystemProcessInstances<T extends Model> implements MutableProcessInstances<T> {
    private final String EVENT_SEPARATOR = "::";
    public static final String PI_DESCRIPTION = "ProcessInstanceDescription";
    public static final String PI_STATUS = "ProcessInstanceStatus";

    private Process<?> process;
    private Path storage;
    private Path eventTypeStorage;

    private ProcessInstanceMarshallerService marshaller;

    public FileSystemProcessInstances(Process<?> process, Path storage) {
        this(process, storage, ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().withDefaultListeners().build());
    }

    public FileSystemProcessInstances(Process<?> process, Path storage, ProcessInstanceMarshallerService marshaller) {
        this.process = process;
        this.storage = Paths.get(storage.toString(), process.id());
        this.eventTypeStorage = PathUtils.getSecuredPath(this.storage, "events.types");
        this.marshaller = marshaller;

        try {
            Files.createDirectories(this.storage);
            if (!Files.exists(eventTypeStorage)) {
                Files.createFile(eventTypeStorage);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create directories for file based storage of process instances", e);
        }
    }

    @Override
    public Optional<ProcessInstance<T>> findById(String id, ProcessInstanceReadMode mode) {
        Path processInstanceStorage = PathUtils.getSecuredPath(storage, id);
        if (Files.notExists(processInstanceStorage) || !Files.isRegularFile(processInstanceStorage)) {
            return Optional.empty();
        }
        byte[] data = readBytesFromFile(processInstanceStorage);
        AbstractProcessInstance<T> pi = (AbstractProcessInstance<T>) marshaller.unmarshallProcessInstance(data, process, mode);
        connectInstance(processInstanceStorage, pi);
        return Optional.of(pi);
    }

    @Override
    public Stream<ProcessInstance<T>> stream(ProcessInstanceReadMode mode) {
        try {
            return Files.walk(storage)
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> Files.exists(file))
                    .filter(file -> !file.equals(eventTypeStorage))
                    .map(this::readBytesFromFile)
                    .map(data -> {
                        ProcessInstance<T> pi = (ProcessInstance<T>) marshaller.unmarshallProcessInstance(data, process, mode);
                        Path processInstanceStorage = PathUtils.getSecuredPath(storage.toString(), pi.id());
                        connectInstance(processInstanceStorage, pi);
                        return pi;
                    });
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read process instances ", e);
        }
    }

    @Override
    public boolean exists(String id) {
        Path processInstanceStorage = PathUtils.getSecuredPath(storage, id);
        return Files.exists(processInstanceStorage) && Files.isRegularFile(processInstanceStorage);
    }

    @Override
    public void create(String id, ProcessInstance<T> instance) {
        if (isActive(instance) || instance.status() == ProcessInstance.STATE_PENDING) {
            Path processInstanceStorage = PathUtils.getSecuredPath(storage, id);
            if (Files.exists(processInstanceStorage)) {
                throw new ProcessInstanceDuplicatedException(id);
            }

            storeProcessInstance(processInstanceStorage, instance);
            storeEventType(instance);
            connectInstance(processInstanceStorage, instance);
        }
    }

    @Override
    public void update(String id, ProcessInstance<T> instance) {
        if (isActive(instance) || instance.status() == ProcessInstance.STATE_PENDING) {
            Path processInstanceStorage = PathUtils.getSecuredPath(storage, id);
            if (Files.exists(processInstanceStorage)) {
                storeProcessInstance(processInstanceStorage, instance);
                connectInstance(processInstanceStorage, instance);
                storeEventType(instance);
            }
        }
    }

    @Override
    public void remove(String id) {
        Path processInstanceStorage = PathUtils.getSecuredPath(storage, id);
        try {
            Files.deleteIfExists(processInstanceStorage);
            cleanEventType(id);
        } catch (IOException e) {
            throw new RuntimeException("Unable to remove process instance with id " + id, e);
        }
    }

    @Override
    public Stream<ProcessInstance<T>> waitingForEventType(String eventType, ProcessInstanceReadMode mode) {
        try {
            if (!Files.exists(eventTypeStorage)) {
                return Collections.<ProcessInstance<T>> emptyList().stream();
            }
            List<String> processInstanceIds = Files.readAllLines(eventTypeStorage)
                    .stream()
                    .filter(line -> line.startsWith(eventType + ":"))
                    .map(line -> line.substring(line.indexOf(EVENT_SEPARATOR) + EVENT_SEPARATOR.length())).toList();
            List<ProcessInstance<T>> waitingInstances = new ArrayList<>();
            for (String processInstanceId : processInstanceIds) {
                Path processInstanceStorage = PathUtils.getSecuredPath(storage, processInstanceId);
                byte[] data = readBytesFromFile(processInstanceStorage);
                AbstractProcessInstance<T> pi = (AbstractProcessInstance<T>) marshaller.unmarshallProcessInstance(data, process, mode);
                connectInstance(processInstanceStorage, pi);
                waitingInstances.add(pi);
            }
            return waitingInstances.stream();
        } catch (IOException e) {
            throw new RuntimeException("Unable to store process events with id " + eventType, e);
        }
    }

    protected void storeEventType(ProcessInstance<?> instance) {
        try {
            cleanEventType(instance.id());
            Set<String> eventTypes = Stream.of(((AbstractProcessInstance<T>) instance).internalGetProcessInstance().getEventTypes()).collect(Collectors.toSet());
            for (String eventType : eventTypes) {
                Files.write(eventTypeStorage, (eventType + EVENT_SEPARATOR + instance.id() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to store process instance with id " + instance.id(), e);
        }
    }

    protected void cleanEventType(String processInstanceId) {
        try {
            if (!Files.exists(eventTypeStorage)) {
                return;
            }
            List<String> lines = Files.readAllLines(eventTypeStorage).stream()
                    .filter(line -> !line.endsWith(EVENT_SEPARATOR + processInstanceId)).toList();
            String fileContent = String.join(System.lineSeparator(), lines);

            Files.write(eventTypeStorage, (fileContent + System.lineSeparator()).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Unable to store process instance events with id " + processInstanceId, e);
        }
    }

    protected void storeProcessInstance(Path processInstanceStorage, ProcessInstance<?> instance) {
        try {
            byte[] data = marshaller.marshallProcessInstance(instance);
            Files.write(processInstanceStorage, data);
            setMetadata(processInstanceStorage, PI_DESCRIPTION, instance.description());
            setMetadata(processInstanceStorage, PI_STATUS, String.valueOf(instance.status()));

        } catch (IOException e) {
            throw new RuntimeException("Unable to store process instance with id " + instance.id(), e);
        }
    }

    protected byte[] readBytesFromFile(Path processInstanceStorage) {
        try {
            return Files.readAllBytes(processInstanceStorage);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read process instance from " + processInstanceStorage, e);
        }
    }

    protected void connectInstance(Path processInstanceStorage, ProcessInstance instance) {
        Supplier<byte[]> supplier = () -> readBytesFromFile(processInstanceStorage);
        ((AbstractProcessInstance<?>) instance).internalSetReloadSupplier(marshaller.createdReloadFunction(supplier));
    }

    public String getMetadata(Path file, String key) {

        if (supportsUserDefinedAttributes(file)) {
            UserDefinedFileAttributeView view = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
            try {
                ByteBuffer bb = ByteBuffer.allocate(view.size(key));
                view.read(key, bb);
                bb.flip();
                return Charset.defaultCharset().decode(bb).toString();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    public boolean setMetadata(Path file, String key, String value) {

        if (supportsUserDefinedAttributes(file)) {
            UserDefinedFileAttributeView view = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
            try {
                if (value != null) {
                    view.write(key, Charset.defaultCharset().encode(value));
                } else {
                    view.delete(key);
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    protected boolean supportsUserDefinedAttributes(Path file) {
        try {
            return Files.getFileStore(file).supportsFileAttributeView(UserDefinedFileAttributeView.class);
        } catch (IOException e) {
            return false;
        }
    }

}
