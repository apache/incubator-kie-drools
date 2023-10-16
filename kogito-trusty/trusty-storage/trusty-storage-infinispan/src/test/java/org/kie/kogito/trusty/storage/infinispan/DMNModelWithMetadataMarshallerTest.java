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
package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.storage.api.model.decision.DMNModelWithMetadata;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DMNModelWithMetadataMarshallerTest extends MarshallerTestTemplate {

    @Test
    public void testWriteAndRead() throws IOException {

        DMNModelWithMetadata dmnModelWithMetadata = new DMNModelWithMetadata("groupId", "artifactId", "version",
                "dmnVersion", "name", "namespace",
                "XML_MODEL");
        DMNModelWithMetadataMarshaller marshaller = new DMNModelWithMetadataMarshaller(new ObjectMapper());

        marshaller.writeTo(writer, dmnModelWithMetadata);
        DMNModelWithMetadata retrieved = marshaller.readFrom(reader);

        Assertions.assertEquals(dmnModelWithMetadata.getGroupId(), retrieved.getGroupId());
        Assertions.assertEquals(dmnModelWithMetadata.getArtifactId(), retrieved.getArtifactId());
        Assertions.assertEquals(dmnModelWithMetadata.getModelVersion(), retrieved.getModelVersion());
        Assertions.assertEquals(dmnModelWithMetadata.getDmnVersion(), retrieved.getDmnVersion());
        Assertions.assertEquals(dmnModelWithMetadata.getModelMetaData().getName(), retrieved.getName());
        Assertions.assertEquals(dmnModelWithMetadata.getModelMetaData().getNamespace(), retrieved.getNamespace());
        Assertions.assertEquals(dmnModelWithMetadata.getModel(), retrieved.getModel());
    }
}
