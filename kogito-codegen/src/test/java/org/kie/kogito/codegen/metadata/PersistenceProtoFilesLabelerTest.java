/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.drools.core.util.IoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PersistenceProtoFilesLabelerTest {

    @Test
    void testGenerateLabels() throws URISyntaxException, IOException {
        final PersistenceProtoFilesLabeler labeler = new PersistenceProtoFilesLabeler();
        final File protoFile = new File(this.getClass().getResource("/kogito-types.proto").toURI());
        final File kogitoApplication = new File(this.getClass().getResource("/kogito-application.proto").toURI());

        String originalContent = new String(Files.readAllBytes(protoFile.toPath()));
        
        assertThat(protoFile).isNotNull();
        assertThat(kogitoApplication).isNotNull();

        labeler.processProto(protoFile);
        labeler.processProto(kogitoApplication);

        final Map<String, String> labels = labeler.generateLabels();

        assertThat(labels).size().isEqualTo(1);
        assertThat(labels).containsKey(labeler.generateKey(protoFile));
        final byte[] bytes = Base64.getDecoder().decode(labels.get("org.kie/persistence/proto/kogito-types.proto"));        
        
        byte[] decompresed = decompres(bytes);
        String roundTrip = new String(decompresed);
        assertThat(roundTrip).isEqualTo(originalContent);
    }

    @Test
    void testGenerateLabelsIOException() throws URISyntaxException {
        final PersistenceProtoFilesLabeler labeler = new PersistenceProtoFilesLabeler();
        Assertions.assertThrows(UncheckedIOException.class, () -> labeler.processProto(new File("target")));
    }
    
    private byte[] decompres(byte[] bytes) throws IOException {
        try(final ByteArrayInputStream fileContents = new ByteArrayInputStream(bytes)) {
            try(final GZIPInputStream gzip = new GZIPInputStream(fileContents)){
                return IoUtils.readBytesFromInputStream(gzip);                
            }
            
        }
    }

}
