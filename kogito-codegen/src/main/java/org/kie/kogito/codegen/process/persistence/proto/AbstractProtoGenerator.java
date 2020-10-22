/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process.persistence.proto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.codegen.GeneratedFile;

public abstract class AbstractProtoGenerator<T> implements ProtoGenerator<T> {

    public static final String GENERATED_PROTO_RES_PATH = "META-INF/resources/persistence/protobuf/";
    public static final String GENERATED_PROTO_PERSISTENCE_PATH = "/classes/persistence/";
    public static final String LISTING_FILE = "list.json";

    protected ObjectMapper mapper;

    public AbstractProtoGenerator() {
        mapper = new ObjectMapper();
    }

    /**
     * Generates the proto files from the given model.
     */
    public final GeneratedFile generateProtoFiles(final String processId, final String targetDirectory, final Proto modelProto) throws IOException {
        String protoFileName = processId + ".proto";
        GeneratedFile protoFile = new GeneratedFile(GeneratedFile.Type.GENERATED_CP_RESOURCE,
                                         GENERATED_PROTO_RES_PATH + protoFileName,
                                         modelProto.toString().getBytes(StandardCharsets.UTF_8));

        Path protoFilePath = Paths.get(targetDirectory, GENERATED_PROTO_PERSISTENCE_PATH + protoFileName);
        Files.createDirectories(protoFilePath.getParent());
        Files.write(protoFilePath, modelProto.toString().getBytes(StandardCharsets.UTF_8));

        return protoFile;
    }

    /**
     * Iterates over the generated files and extract all the proto files. Then it creates and add to the generated files collection
     * a listing file ({@link #LISTING_FILE}) from its content.
     *
     * @param generatedFiles  The list of generated files.
     * @throws IOException if something wrong occurs during I/O
     */
    public final Optional<GeneratedFile> generateProtoListingFile(Collection<GeneratedFile> generatedFiles) throws IOException {
        List<String> fileNames = generatedFiles.stream()
                .filter(x -> x.relativePath().contains(GENERATED_PROTO_RES_PATH))
                .map(x -> x.relativePath().substring(x.relativePath().lastIndexOf("/") + 1))
                .collect(Collectors.toList());

        if (!fileNames.isEmpty()) {
            return Optional.of(new GeneratedFile(GeneratedFile.Type.GENERATED_CP_RESOURCE,
                                                 GENERATED_PROTO_RES_PATH + LISTING_FILE,
                                                 mapper.writeValueAsString(fileNames)));
        }
        return Optional.empty();
    }
}
