/**
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
package org.kie.pmml.commons.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.testingutility.PMMLRuntimeContextTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class KiePMMLModelWithSourcesTest {

    private static final String FILE_NAME = "fileName";

    private static final String MODEL_NAME = "MODEL_NAME";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private final static Map<String, String> SOURCES_MAP = new HashMap<>();

    private KiePMMLModelWithSources kiePMMLModelWithSources;

    @BeforeEach
    public void setup() {
        kiePMMLModelWithSources = new KiePMMLModelWithSources(FILE_NAME,
                                                              MODEL_NAME,
                                                              PACKAGE_NAME,
                                                              Collections.emptyList(),
                                                              Collections.emptyList(),
                                                              Collections.emptyList(),
                                                              SOURCES_MAP,
                                                              false);
    }

    @Test
    void evaluate() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            kiePMMLModelWithSources.evaluate(Collections.EMPTY_MAP, new PMMLRuntimeContextTest());
        });
    }

    @Test
    void getSourcesMap() {
        assertThat(kiePMMLModelWithSources.getSourcesMap()).isEqualTo(SOURCES_MAP);
    }

    @Test
    void addToGetSourcesMap() {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> {
            Map<String, String> retrieved = kiePMMLModelWithSources.getSourcesMap();
            retrieved.put("KEY", "VALUE");
        });
    }

    @Test
    void addSourceMap() {
        Map<String, String> retrieved = kiePMMLModelWithSources.getSourcesMap();
        assertThat(retrieved).isEmpty();
        kiePMMLModelWithSources.addSourceMap("KEY", "VALUE");
        retrieved = kiePMMLModelWithSources.getSourcesMap();
        assertThat(retrieved).containsKey("KEY");
        assertThat(retrieved.get("KEY")).isEqualTo("VALUE");
    }
}