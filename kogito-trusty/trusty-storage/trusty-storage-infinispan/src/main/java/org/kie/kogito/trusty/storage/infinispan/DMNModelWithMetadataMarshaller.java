/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;

import org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DMNModelWithMetadataMarshaller extends AbstractModelMarshaller<DMNModelWithMetadata> {

    public DMNModelWithMetadataMarshaller(ObjectMapper mapper) {
        super(mapper, DMNModelWithMetadata.class);
    }

    @Override
    public DMNModelWithMetadata readFrom(ProtoStreamReader reader) throws IOException {
        return new DMNModelWithMetadata(
                reader.readString(DMNModelWithMetadata.GROUP_ID_FIELD),
                reader.readString(DMNModelWithMetadata.ARTIFACT_ID_FIELD),
                reader.readString(DMNModelWithMetadata.MODEL_VERSION_FIELD),
                reader.readString(DMNModelWithMetadata.DMN_VERSION_FIELD),
                reader.readString(DMNModelWithMetadata.NAME_FIELD),
                reader.readString(DMNModelWithMetadata.NAMESPACE_FIELD),
                reader.readString(DMNModelWithMetadata.MODEL_FIELD));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, DMNModelWithMetadata dmnModelWithMetadata) throws IOException {
        writer.writeString(DMNModelWithMetadata.GROUP_ID_FIELD, dmnModelWithMetadata.getGroupId());
        writer.writeString(DMNModelWithMetadata.ARTIFACT_ID_FIELD, dmnModelWithMetadata.getArtifactId());
        writer.writeString(DMNModelWithMetadata.MODEL_VERSION_FIELD, dmnModelWithMetadata.getModelVersion());
        writer.writeString(DMNModelWithMetadata.DMN_VERSION_FIELD, dmnModelWithMetadata.getDmnVersion());
        writer.writeString(DMNModelWithMetadata.NAME_FIELD, dmnModelWithMetadata.getName());
        writer.writeString(DMNModelWithMetadata.NAMESPACE_FIELD, dmnModelWithMetadata.getNamespace());
        writer.writeString(DMNModelWithMetadata.MODEL_FIELD, dmnModelWithMetadata.getModel());
    }
}
