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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.codegen.process.persistence.proto.Proto;

/**
 * Responsible for generating image labels representing generated proto files
 */
public class PersistenceLabeler implements Labeler {

    private static final String PERSISTENCE_LABEL_PREFIX = ImageMetaData.LABEL_PREFIX + "persistence/proto/";
    private static final String KOGITO_APPLICATION_PROTO = "kogito-application.proto";
    public static final String PROTO_FILE_EXT = ".proto";
    private final Map<String, String> encodedProtos = new HashMap<>();

    /**
     * Transforms the given {@link Proto} into a format for the {@link ImageMetaData} 
     * 
     * @param file that will be added to the label
     * @throws IOException 
     */
    public void processProto(final File file) {
        try {
            if (file != null && !KOGITO_APPLICATION_PROTO.equalsIgnoreCase(file.getName())) {
                this.encodedProtos.put(generateKey(file), compactFile(file));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error while processing proto files as image labels", e);
        }
    }

    protected String compactFile(final File file) throws IOException {
        final byte[] contents = Files.readAllBytes(file.toPath());
        if (contents == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(contents);
    }

    protected String generateKey(final File file) {
        return String.format("%s%s", PERSISTENCE_LABEL_PREFIX, file.getName());
    }

    @Override
    public Map<String, String> generateLabels() {
        return encodedProtos;
    }

}
