/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

public class FileDescriptorHolder {

    public static final String DESCRIPTOR_PATH = "protobuf/descriptor-sets/output.protobin";

    private static final Logger logger = LoggerFactory.getLogger(FileDescriptorHolder.class);

    private static final FileDescriptorHolder instance = new FileDescriptorHolder();

    public static FileDescriptorHolder get() {
        return instance;
    }

    private FileDescriptorHolder() {
        fdSet = loadFileDescriptorSet();
    }

    private Optional<FileDescriptorSet> fdSet;

    public Optional<FileDescriptorSet> descriptor() {
        return fdSet;
    }

    protected static Optional<FileDescriptorSet> loadFileDescriptorSet() {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DESCRIPTOR_PATH)) {
            return inputStream != null ? Optional.of(FileDescriptorSet.newBuilder().mergeFrom(inputStream.readAllBytes()).build()) : Optional.empty();
        } catch (IOException e) {
            logger.warn("Error loading descriptor set", e);
            return Optional.empty();
        }
    }
}
