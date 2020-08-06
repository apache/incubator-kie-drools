/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.persistence.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.process.impl.marshalling.ProcessInstanceMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.process.ProcessInstanceReadMode.MUTABLE;

@SuppressWarnings({"rawtypes"})
public class FileSystemProcessInstances implements MutableProcessInstances {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemProcessInstances.class);

    public static final String PI_DESCRIPTION = "ProcessInstanceDescription";
    public static final String PI_STATUS = "ProcessInstanceStatus";

    private Process<?> process;
    private Path storage;

    private ProcessInstanceMarshaller marshaller;

    public FileSystemProcessInstances(Process<?> process, Path storage) {
        this(process, storage, new ProcessInstanceMarshaller());
    }

    public FileSystemProcessInstances(Process<?> process, Path storage, ProcessInstanceMarshaller marshaller) {
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
    public Integer size() {
        try (Stream<Path> stream = Files.walk(storage)) {
            Long count = stream.filter(file -> !Files.isDirectory(file)).count();
            return count.intValue();
        } catch (IOException e) {
            throw new RuntimeException("Unable to count process instances ", e);
        }
    }

    @Override
    public Optional findById(String id, ProcessInstanceReadMode mode) {
        String resolvedId = resolveId(id);
        Path processInstanceStorage = Paths.get(storage.toString(), resolvedId);

        if (Files.notExists(processInstanceStorage)) {
            return Optional.empty();
        }
        byte[] data = readBytesFromFile(processInstanceStorage);
        return Optional.of(mode == MUTABLE ?
                                   marshaller.unmarshallProcessInstance(data, process) :
                                   marshaller.unmarshallReadOnlyProcessInstance(data, process)
        );
    }

    @Override
    public Collection values(ProcessInstanceReadMode mode) {
        try (Stream<Path> stream = Files.walk(storage)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(this::readBytesFromFile)
                    .map(b -> mode == MUTABLE ?
                            marshaller.unmarshallProcessInstance(b, process) :
                            marshaller.unmarshallReadOnlyProcessInstance(b, process))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read process instances ", e);
        }
    }

    @Override
    public boolean exists(String id) {
        return Files.exists(Paths.get(storage.toString(), resolveId(id)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void create(String id, ProcessInstance instance) {
        if (isActive(instance)) {
            String resolvedId = resolveId(id);
            Path processInstanceStorage = Paths.get(storage.toString(), resolvedId);

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
            String resolvedId = resolveId(id);
            Path processInstanceStorage = Paths.get(storage.toString(), resolvedId);

            if (Files.exists(processInstanceStorage)) {
                storeProcessInstance(processInstanceStorage, instance);
            }
        }
    }

    @Override
    public void remove(String id) {
        Path processInstanceStorage = Paths.get(storage.toString(), resolveId(id));

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

            disconnect(processInstanceStorage, instance);
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
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(() -> {

            try {
                byte[] reloaded = readBytesFromFile(processInstanceStorage);
                return marshaller.unmarshallWorkflowProcessInstance(reloaded, process);
            } catch (RuntimeException e) {
                LOGGER.error("Unexpected exception thrown when reloading process instance {}", instance.id(), e);
                return null;
            }
        });
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
