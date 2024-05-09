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

package org.kie.kogito.index.storage;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessDefinitionKey;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelDataIndexStorageServiceTest {

    @Test
    void testIdAndVersion() {
        assertKeyConversion(new ProcessDefinitionKey("Javierito", "1_0"));
    }

    @Test
    void testIdEmptyVersion() {
        assertKeyConversion(new ProcessDefinitionKey("Javierito", ""));
    }

    @Test
    void testIdNullVersion() {
        assertKeyConversion(new ProcessDefinitionKey("Javierito", null));
    }

    private void assertKeyConversion(ProcessDefinitionKey key) {
        Set<ProcessDefinitionKey> set = new HashSet<>();
        set.add(key);
        ProcessDefinitionKey deserializedKey = ModelProcessDefinitionStorage.fromString(ModelProcessDefinitionStorage.toString(key));
        set.add(deserializedKey);
        assertThat(deserializedKey).isEqualTo(key);
        assertThat(set).hasSize(1);
    }
}
