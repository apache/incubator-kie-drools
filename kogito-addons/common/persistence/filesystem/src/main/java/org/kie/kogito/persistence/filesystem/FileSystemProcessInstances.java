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
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;

@SuppressWarnings({ "rawtypes" })
public class FileSystemProcessInstances implements MutableProcessInstances {

    public static final String PI_DESCRIPTION = "ProcessInstanceDescription";
    public static final String PI_STATUS = "ProcessInstanceStatus";

    private Process<?> process;
    private Path storage;

    private ProcessInstanceMarshallerService marshaller;

    public FileSystemProcessInstances(Process<?> process, Path storage) {
        this(process, storage, ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().withDefaultListeners().build());
    }

    public FileSystemProcessInstances(Process<?> process, Path storage, ProcessInstanceMarshallerService marshaller) {
        this.process = process;
        this.storage = Paths.get(storage.toString(), process.id());
        this.marshaller = marshaller;

        try {
            Files.createDirectories(this.storage);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create directories for file based storage of process instances", e);
        }
    }

    @Override
    public Optional findById(String id, ProcessInstanceReadMode mode) {
        Path processInstanceStorage = Paths.get(storage.toString(), id);
        if (Files.notExists(processInstanceStorage)) {
            return Optional.empty();
        }
        byte[] data = readBytesFromFile(processInstanceStorage);
        AbstractProcessInstance pi = (AbstractProcessInstance) marshaller.unmarshallProcessInstance(data, process, mode);
        if (pi != null && !ProcessInstanceReadMode.READ_ONLY.equals(mode)) {
            disconnect(processInstanceStorage, pi);
        }
        return Optional.of(pi);
    }

    @Override
    public Stream<ProcessInstance> stream(ProcessInstanceReadMode mode) {
        try {
            return Files.walk(storage)
                    .filter(file -> !Files.isDirectory(file))
                    .map(this::readBytesFromFile)
                    .map(marshaller.createUnmarshallFunction(process, mode));
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read process instances ", e);
        }
    }

    @Override
    public boolean exists(String id) {
        return Files.exists(Paths.get(storage.toString(), id));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void create(String id, ProcessInstance instance) {
        if (isActive(instance)) {
            Path processInstanceStorage = Paths.get(storage.toString(), id);
            if (Files.exists(processInstanceStorage)) {
                throw new ProcessInstanceDuplicatedException(id);
            }
            storeProcessInstance(processInstanceStorage, instance);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(String id, ProcessInstance instance) {
        if (isActive(instance)) {
            Path processInstanceStorage = Paths.get(storage.toString(), id);
            if (Files.exists(processInstanceStorage)) {
                storeProcessInstance(processInstanceStorage, instance);
                disconnect(processInstanceStorage, instance);
            }
        }
    }

    @Override
    public void remove(String id) {
        Path processInstanceStorage = Paths.get(storage.toString(), id);
        try {
            Files.deleteIfExists(processInstanceStorage);
        } catch (IOException e) {
            throw new RuntimeException("Unable to remove process instance with id " + id, e);
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

    protected void disconnect(Path processInstanceStorage, ProcessInstance instance) {
        Supplier<byte[]> supplier = () -> readBytesFromFile(processInstanceStorage);
        ((AbstractProcessInstance<?>) instance).internalSetReloadSupplier(marshaller.createdReloadFunction(supplier));
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance();
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
