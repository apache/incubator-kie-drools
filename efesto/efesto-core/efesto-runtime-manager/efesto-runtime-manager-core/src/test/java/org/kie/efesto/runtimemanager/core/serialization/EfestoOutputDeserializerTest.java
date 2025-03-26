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
package org.kie.efesto.runtimemanager.core.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoOutput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;

class EfestoOutputDeserializerTest {

    @Test
    @SuppressWarnings("rawtypes")
    void deserializeTest() throws JsonProcessingException {
        String toDeserialize = "{\"modelLocalUriId\":{\"model\":\"mock\",\"basePath\":\"/org/kie/efesto/runtimemanager/core/mocks/MockEfestoOutput\",\"fullPath\":\"/mock/org/kie/efesto/runtimemanager/core/mocks/MockEfestoOutput\"},\"outputData\":\"MockEfestoOutput\",\"kind\":\"org.kie.efesto.runtimemanager.core.mocks.MockEfestoOutput\"}";
        EfestoOutput retrieved = getObjectMapper().readValue(toDeserialize, EfestoOutput.class);
        assertThat(retrieved).isNotNull().isExactlyInstanceOf(MockEfestoOutput.class);
    }
}