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

package org.kie.kogito.codegen.process.persistence.proto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.GeneratedFile;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractProtoGeneratorTest {

    @Test
    void checkGeneratedProtoBufAndListing() throws IOException {
        final ReflectionProtoGenerator generator = ReflectionProtoGenerator.builder().build(null);
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Proto proto = new Proto("org.acme.test");
            generatedFiles.add(generator.generateProtoFiles("protofile." + i, proto));
        }
        generator.generateProtoListingFile(generatedFiles).ifPresent(generatedFiles::add);

        GeneratedFile listFile = generatedFiles.stream().filter(x -> x.relativePath().endsWith("list.json")).findFirst().get();
        byte[] list = listFile.contents();
        final ObjectMapper mapper = new ObjectMapper();
        List<String> files = mapper.readValue(list, List.class);
        assertThat(files).isNotEmpty();
        assertThat(files)
                .hasAtLeastOneElementOfType(String.class)
                .contains("protofile.0.proto")
                .hasSize(5);
    }
}