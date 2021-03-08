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

import java.util.List;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata.ARTIFACT_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata.DMN_VERSION_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata.GROUP_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata.MODEL_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata.MODEL_VERSION_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata.NAMESPACE_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata.NAME_FIELD;

public class DMNModelWithMetadataMarshallerTest extends MarshallerTestTemplate<DMNModelWithMetadata> {

    private static final List<AbstractTestField<DMNModelWithMetadata, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(GROUP_ID_FIELD, "groupId", DMNModelWithMetadata::getGroupId, DMNModelWithMetadata::setGroupId),
            new StringTestField<>(ARTIFACT_ID_FIELD, "artifactId", DMNModelWithMetadata::getArtifactId, DMNModelWithMetadata::setArtifactId),
            new StringTestField<>(MODEL_VERSION_FIELD, "modelVersion", DMNModelWithMetadata::getModelVersion, DMNModelWithMetadata::setModelVersion),
            new StringTestField<>(DMN_VERSION_FIELD, "dmnVersion", DMNModelWithMetadata::getDmnVersion, DMNModelWithMetadata::setDmnVersion),
            new StringTestField<>(NAME_FIELD, "name", DMNModelWithMetadata::getName, DMNModelWithMetadata::setName),
            new StringTestField<>(NAMESPACE_FIELD, "namespace", DMNModelWithMetadata::getNamespace, DMNModelWithMetadata::setNamespace),
            new StringTestField<>(MODEL_FIELD, "model", DMNModelWithMetadata::getModel, DMNModelWithMetadata::setModel));

    public DMNModelWithMetadataMarshallerTest() {
        super(DMNModelWithMetadata.class);
    }

    @Override
    protected DMNModelWithMetadata buildEmptyObject() {
        return new DMNModelWithMetadata();
    }

    @Override
    protected MessageMarshaller<DMNModelWithMetadata> buildMarshaller() {
        return new DMNModelWithMetadataMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<DMNModelWithMetadata, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
