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
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.codegen.GeneratedFile;

public abstract class AbstractProtoGenerator<T> implements ProtoGenerator {

    private static final String GENERATED_PROTO_RES_PATH = "META-INF/resources/persistence/protobuf/";
    private static final String LISTING_FILE = "list.json";

    protected final ObjectMapper mapper;
    protected final Collection<T> modelClasses;
    protected final Collection<T> dataClasses;
    protected final T persistenceClass;

    protected AbstractProtoGenerator(T persistenceClass, Collection<T> rawModelClasses, Collection<T> rawDataClasses) {
        this.modelClasses = rawModelClasses == null ? Collections.emptyList() : rawModelClasses;
        this.dataClasses = rawDataClasses == null ? Collections.emptyList() : rawDataClasses;
        this.persistenceClass = persistenceClass;
        this.mapper = new ObjectMapper();
    }

    @Override
    public Collection<GeneratedFile> generateProtoFiles() {
        List<GeneratedFile> generatedFiles = new ArrayList<>();

        modelClasses.stream()
                .map(this::generateModelClassProto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(generatedFiles::add);

        try {
            this.generateProtoListingFile(generatedFiles).ifPresent(generatedFiles::add);
        } catch (IOException e) {
            throw new UncheckedIOException("Error during proto listing file creation", e);
        }

        return generatedFiles;
    }

    /**
     * Generates the proto files from the given model.
     */
    protected final GeneratedFile generateProtoFiles(final String processId, final Proto modelProto) {
        String protoFileName = processId + ".proto";
        return new GeneratedFile(PROTO_TYPE,
                                 GENERATED_PROTO_RES_PATH + protoFileName,
                                 modelProto.toString());
    }

    /**
     * Iterates over the generated files and extract all the proto files. Then it creates and add to the generated files collection
     * a listing file ({@link #LISTING_FILE}) from its content.
     *
     * @param generatedFiles  The list of generated files.
     * @throws IOException if something wrong occurs during I/O
     */
    protected final Optional<GeneratedFile> generateProtoListingFile(Collection<GeneratedFile> generatedFiles) throws IOException {
        List<String> fileNames = generatedFiles.stream()
                .filter(x -> x.relativePath().contains(GENERATED_PROTO_RES_PATH))
                .map(x -> x.relativePath().substring(x.relativePath().lastIndexOf("/") + 1))
                .collect(Collectors.toList());

        if (!fileNames.isEmpty()) {
            return Optional.of(new GeneratedFile(PROTO_TYPE,
                                                 GENERATED_PROTO_RES_PATH + LISTING_FILE,
                                                 mapper.writeValueAsString(fileNames)));
        }
        return Optional.empty();
    }

    public Collection<T> getModelClasses() {
        return Collections.unmodifiableCollection(modelClasses);
    }

    public Collection<T> getDataClasses() {
        return Collections.unmodifiableCollection(dataClasses);
    }

    public T getPersistenceClass() {
        return persistenceClass;
    }

    protected abstract Proto generate(String messageComment, String fieldComment, String packageName, T dataModel, String... headers);

    protected abstract Optional<GeneratedFile> generateModelClassProto(T modelClazz);

    protected abstract static class AbstractProtoGeneratorBuilder<E, T extends ProtoGenerator> implements Builder<E, T> {
        protected E persistenceClass;
        protected Collection<E> dataClasses;

        protected abstract Collection<E> extractDataClasses(Collection<E> modelClasses);

        @Override
        public Builder<E, T> withPersistenceClass(E persistenceClass) {
            this.persistenceClass = persistenceClass;
            return this;
        }

        @Override
        public Builder<E, T> withDataClasses(Collection<E> dataClasses) {
            this.dataClasses = dataClasses;
            return this;
        }
    }
}
