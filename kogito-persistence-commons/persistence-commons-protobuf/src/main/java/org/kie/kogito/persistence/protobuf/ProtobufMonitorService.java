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
package org.kie.kogito.persistence.protobuf;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import static java.lang.String.format;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@ApplicationScoped
public class ProtobufMonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufMonitorService.class);
    private static final PathMatcher protoFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**.proto");
    private static final String KOGITO_APPLICATION_PROTO = "kogito-application.proto";

    @Inject
    @ConfigProperty(name = "kogito.protobuf.folder")
    Optional<String> protoFiles;

    @Inject
    @ConfigProperty(name = "kogito.protobuf.watch", defaultValue = "false")
    Boolean monitor;

    Consumer<Path> onFolderWatch;

    @Inject
    ProtobufService protobufService;

    ExecutorService executorService;

    public void startMonitoring() {
        if (protoFiles.isPresent()) {
            String folderPath = protoFiles.get();
            Path protoFolder = Paths.get(folderPath);
            if (!Files.exists(protoFolder)) {
                LOGGER.warn("Could not find proto files folder at: {}. Disabling ProtobufMonitorService", folderPath);
                return;
            }

            if (!Files.isReadable(protoFolder)) {
                LOGGER.warn("The folder {} does not have read access. Cannot register protofiles from that folder", folderPath);
                return;
            }

            LOGGER.info("The folder {} is being used to registering files", folderPath);
            registerFilesFromFolder(protoFolder);
            if (Boolean.TRUE.equals(monitor)) {
                executorService = Executors.newSingleThreadExecutor();
                executorService.submit(new FolderWatcher(registerProtoFile(), protoFolder));
            }
        }
    }

    private void registerFilesFromFolder(Path folderPath) {
        try (Stream<Path> stream = Files.find(folderPath, Integer.MAX_VALUE, (path, attrs) -> protoFileMatcher.matches(path))) {
            stream.filter(path -> !KOGITO_APPLICATION_PROTO.equals(path.getFileName().toFile().getName())).forEach(path -> registerProtoFile().accept(path));
        } catch (IOException ex) {
            throw new ProtobufFileMonitorException(format("Could not read content from proto file folder: %s", folderPath), ex);
        }
    }

    private Consumer<Path> registerProtoFile() {
        return path -> {
            try {
                LOGGER.info("Found proto file: {}", path);
                String content = new String(Files.readAllBytes(path));
                protobufService.registerProtoBufferType(content);
            } catch (IOException ex) {
                throw new ProtobufFileMonitorException("Could not read content from proto file folder", ex);
            } catch (ProtobufValidationException e) {
                LOGGER.error("Failed to register proto file: {}", path, e);
                throw new ProtobufFileMonitorException(e);
            }
        };
    }

    void onStop(@Observes ShutdownEvent ev) {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private class FolderWatcher implements Runnable {

        private final Map<WatchKey, Path> keys = new HashMap<>();
        private Consumer<Path> consumer;
        private Path folder;

        public FolderWatcher(Consumer<Path> consumer, Path folder) {
            this.consumer = consumer;
            this.folder = folder;
        }

        @Override
        public void run() {
            try (WatchService ws = FileSystems.getDefault().newWatchService()) {
                keys.put(folder.register(ws, ENTRY_MODIFY, ENTRY_CREATE), folder);
                Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        keys.put(dir.register(ws, ENTRY_MODIFY, ENTRY_CREATE), dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                if (onFolderWatch != null) {
                    onFolderWatch.accept(folder);
                }
                WatchKey key;
                while ((key = ws.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        LOGGER.debug("Event kind: {}. File affected: {}", event.kind(), event.context());
                        Path path = (Path) event.context();
                        Path proto = keys.get(key).resolve(path);
                        if (Files.isDirectory(proto)) {
                            registerFilesFromFolder(proto);
                            keys.put(proto.register(ws, ENTRY_MODIFY, ENTRY_CREATE), proto);
                        } else if (protoFileMatcher.matches(path) && !KOGITO_APPLICATION_PROTO.equals(path.getFileName().toFile().getName())) {
                            consumer.accept(proto);
                        }
                    }
                    key.reset();
                }
            } catch (Exception ex) {
                LOGGER.warn("Exception in proto folder watcher for folder: {}, message: {}", folder, ex.getMessage(), ex);
            }
        }
    }
}
