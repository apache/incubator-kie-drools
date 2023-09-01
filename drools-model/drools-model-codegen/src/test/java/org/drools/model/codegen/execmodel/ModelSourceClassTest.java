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
package org.drools.model.codegen.execmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kproject.models.KieBaseModelImpl;

import static org.assertj.core.api.Assertions.assertThat;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.util.maven.support.ReleaseIdImpl;

public class ModelSourceClassTest {


    private static final ReleaseId RELEASE_ID = new ReleaseIdImpl("org:dummy:1.0");

    @Test
    public void addGetModelsMethodEmptyModelsByKBaseTest() {
        ModelSourceClass modelSourceClass = new ModelSourceClass(RELEASE_ID, new HashMap<>(), new HashMap<>());
        StringBuilder sb = new StringBuilder();
        modelSourceClass.addGetModelsMethod(sb);
        String retrieved = sb.toString();
        String expected = "return java.util.Arrays.asList();";
        assertThat(retrieved.contains(expected)).isTrue();
    }

    @Test
    public void addGetModelsMethodEmptyModelsByKBaseValuesTest() {
        Map<String, List<String>> modelsByKBase = new HashMap<>();
        modelsByKBase.put("default-kie", Collections.emptyList());
        ModelSourceClass modelSourceClass = new ModelSourceClass(RELEASE_ID, new HashMap<>(), modelsByKBase);
        StringBuilder sb = new StringBuilder();
        modelSourceClass.addGetModelsMethod(sb);
        String retrieved = sb.toString();
        String expected = "return java.util.Arrays.asList();";
        assertThat(retrieved.contains(expected)).isTrue();
        String unexpected = "return java.util.Arrays.asList(new ());";
        assertThat(retrieved.contains(unexpected)).isFalse();
    }

    @Test
    public void addGetModelsMethodPopulatedModelsByKBaseValuesTest() {
        List<String> modelByKBaseValues = Collections.singletonList("ModelTest");
        Map<String, List<String>> modelsByKBase = new HashMap<>();
        modelsByKBase.put("default-kie", modelByKBaseValues);
        ModelSourceClass modelSourceClass = new ModelSourceClass(RELEASE_ID, new HashMap<>(), modelsByKBase);
        StringBuilder sb = new StringBuilder();
        modelSourceClass.addGetModelsMethod(sb);
        String retrieved = sb.toString();
        String expected = "return java.util.Arrays.asList(new ModelTest());";
        assertThat(retrieved.contains(expected)).isTrue();
        String unexpected = "return java.util.Arrays.asList();";
        assertThat(retrieved.contains(unexpected)).isFalse();
    }

    @Test
    public void addGetModelForKieBaseMethodEmptyModelMethodTest() {
        ModelSourceClass modelSourceClass = new ModelSourceClass(RELEASE_ID, new HashMap<>(), new HashMap<>());
        StringBuilder sb = new StringBuilder();
        modelSourceClass.addGetModelForKieBaseMethod(sb);
        String retrieved = sb.toString();
        String unexpected = "switch (kieBaseName) {";
        assertThat(retrieved.contains(unexpected)).isFalse();
    }

    @Test
    public void addGetModelForKieBaseMethodEmptyModelsByKBaseTest() {
        KieBaseModel kieBaseModel = getKieBaseModel("ModelTest");
        Map<String, KieBaseModel> kBaseModels = new HashMap<>();
        kBaseModels.put("default-kie", kieBaseModel);
        ModelSourceClass modelSourceClass = new ModelSourceClass(RELEASE_ID, kBaseModels, new HashMap<>());
        StringBuilder sb = new StringBuilder();
        modelSourceClass.addGetModelForKieBaseMethod(sb);
        String retrieved = sb.toString();
        String expected = "switch (kieBaseName) {";
        assertThat(retrieved.contains(expected)).isTrue();
        expected = "case \"default-kie\": return getModels();";
        assertThat(retrieved.contains(expected)).isTrue();
    }

    @Test
    public void addGetModelForKieBaseMethodEmptyModelsByKBaseValuesTest() {
        KieBaseModel kieBaseModel = getKieBaseModel("ModelTest");
        Map<String, KieBaseModel> kBaseModels = new HashMap<>();
        kBaseModels.put("default-kie", kieBaseModel);
        Map<String, List<String>> modelsByKBase = new HashMap<>();
        modelsByKBase.put("default-kie", Collections.emptyList());
        ModelSourceClass modelSourceClass = new ModelSourceClass(RELEASE_ID, kBaseModels, modelsByKBase);
        StringBuilder sb = new StringBuilder();
        modelSourceClass.addGetModelForKieBaseMethod(sb);
        String retrieved = sb.toString();
        String expected = "switch (kieBaseName) {";
        assertThat(retrieved.contains(expected)).isTrue();
        expected = "case \"default-kie\": return getModels();";
        assertThat(retrieved.contains(expected)).isTrue();
    }

    @Test
    public void addGetModelForKieBaseMethodUnmatchingModelsByKBaseValuesTest() {
        KieBaseModel kieBaseModel = getKieBaseModel("ModelTest");
        Map<String, KieBaseModel> kBaseModels = new HashMap<>();
        kBaseModels.put("default-kie", kieBaseModel);
        List<String> modelByKBaseValues = Collections.singletonList("NotModelTest");
        Map<String, List<String>> modelsByKBase = new HashMap<>();
        modelsByKBase.put("default-kie", modelByKBaseValues);
        ModelSourceClass modelSourceClass = new ModelSourceClass(RELEASE_ID, kBaseModels, modelsByKBase);
        StringBuilder sb = new StringBuilder();
        modelSourceClass.addGetModelForKieBaseMethod(sb);
        String retrieved = sb.toString();
        String expected = "switch (kieBaseName) {";
        assertThat(retrieved.contains(expected)).isTrue();
        expected = "case \"default-kie\": return java.util.Arrays.asList( new NotModelTest() );";
        assertThat(retrieved.contains(expected)).isTrue();
    }

    @Test
    public void addGetModelForKieBaseMethodMatchingModelsByKBaseValuesTest() {
        KieBaseModel kieBaseModel = getKieBaseModel("ModelTest");
        Map<String, KieBaseModel> kBaseModels = new HashMap<>();
        kBaseModels.put("default-kie", kieBaseModel);
        List<String> modelByKBaseValues = Collections.singletonList("ModelTest");
        Map<String, List<String>> modelsByKBase = new HashMap<>();
        modelsByKBase.put("default-kie", modelByKBaseValues);
        ModelSourceClass modelSourceClass = new ModelSourceClass(RELEASE_ID, kBaseModels, modelsByKBase);
        StringBuilder sb = new StringBuilder();
        modelSourceClass.addGetModelForKieBaseMethod(sb);
        String retrieved = sb.toString();
        String expected = "switch (kieBaseName) {";
        assertThat(retrieved.contains(expected)).isTrue();
        expected = "case \"default-kie\": return java.util.Arrays.asList( new ModelTest() );";
        assertThat(retrieved.contains(expected)).isTrue();
    }

    private KieBaseModel getKieBaseModel(String modelName) {
        KieModuleModel kieModuleModel = new KieModuleModelImpl();
        return new KieBaseModelImpl(kieModuleModel, modelName);
    }

}