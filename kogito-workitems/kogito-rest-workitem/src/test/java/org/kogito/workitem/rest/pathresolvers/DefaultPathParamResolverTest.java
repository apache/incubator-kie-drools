/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kogito.workitem.rest.pathresolvers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class DefaultPathParamResolverTest {

    private static DefaultPathParamResolver pathParamResolver;

    @BeforeAll
    static void setup() {
        pathParamResolver = new DefaultPathParamResolver();
    }

    @Test
    public void testReplaceTemplateTrivial() {
        Map<String, Object> parameters = Collections.emptyMap();
        String endPoint = "http://pepe:password@www.google.com/results/id/?user=pepe#at_point";
        assertThat(pathParamResolver.apply(endPoint, parameters)).isEqualTo("http://pepe:password@www.google.com/results/id/?user=pepe#at_point");
    }

    @Test
    public void testReplaceTemplate() {
        Map<String, Object> parameters = new HashMap<>();
        // no use singletonMap here since the map must be mutable
        parameters.put("id", "pepe");
        String endPoint = "http://pepe:password@www.google.com/results/{id}/?user=pepe#at_point";
        assertThat(pathParamResolver.apply(endPoint, parameters)).isEqualTo("http://pepe:password@www.google.com/results/pepe/?user=pepe#at_point");
    }

    @Test
    public void testReplaceTemplateMultiple() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("veryVeryLongId", 26);
        parameters.put("name", "pepe");
        String endPoint = "http://pepe:password@www.google.com/results/{veryVeryLongId}/names/{name}/?user=pepe#at_point";
        assertThat(pathParamResolver.apply(endPoint, parameters)).isEqualTo("http://pepe:password@www.google.com/results/26/names/pepe/?user=pepe#at_point");
    }

    @Test
    public void testReplaceTemplateMissing() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 26);
        String endPoint = "http://pepe:password@www.google.com/results/{id}/names/{name}/?user=pepe#at_point";
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> pathParamResolver.apply(endPoint, parameters)).withMessageContaining("name");
    }

    @Test
    public void testReplaceTemplateBadEndpoint() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 26);
        parameters.put("name", "pepe");
        String endPoint = "http://pepe:password@www.google.com/results/{id}/names/{name/?user=pepe#at_point";
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> pathParamResolver.apply(endPoint, parameters)).withMessageContaining("}");
    }

}
