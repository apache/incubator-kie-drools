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
package org.kie.kogito;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class KogitoGAVTest {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void serializationTest() throws JsonProcessingException {
        KogitoGAV originalGav = new KogitoGAV("group", "artifact", "version");

        KogitoGAV processedGav = MAPPER.readValue(MAPPER.writeValueAsString(originalGav), KogitoGAV.class);

        assertThat(processedGav).isNotNull();
        assertThat(processedGav.getGroupId()).isEqualTo(originalGav.getGroupId());
        assertThat(processedGav.getArtifactId()).isEqualTo(originalGav.getArtifactId());
        assertThat(processedGav.getVersion()).isEqualTo(originalGav.getVersion());
        assertThat(processedGav).isEqualTo(originalGav);
    }
}
