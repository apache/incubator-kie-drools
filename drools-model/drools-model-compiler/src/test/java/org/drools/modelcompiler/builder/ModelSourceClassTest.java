/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModelSourceClassTest {


    private static final ReleaseId RELEASE_ID = new ReleaseIdImpl("org:dummy:1.0");

    @Test
    public void addGetModelsMethodEmptyModelsByKBaseTest() {
        ModelSourceClass modelSourceClass = new ModelSourceClass(RELEASE_ID, new HashMap<>(), new HashMap<>());
        StringBuilder sb = new StringBuilder();
        modelSourceClass.addGetModelsMethod(sb);
        String retrieved = sb.toString();
        String expected = "return java.util.Arrays.asList();";
        assertTrue(retrieved.contains(expected));
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
        assertTrue(retrieved.contains(expected));
        String unexpected = "return java.util.Arrays.asList(new ());";
        assertFalse(retrieved.contains(unexpected));
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
        assertTrue(retrieved.contains(expected));
        String unexpected = "return java.util.Arrays.asList();";
        assertFalse(retrieved.contains(unexpected));
    }

    @Test
    public void addGetModelForKieBaseMethodEmptyModelMethodTest() {
        ModelSourceClass modelSourceClass = new ModelSourceClass(RELEASE_ID, new HashMap<>(), new HashMap<>());
        StringBuilder sb = new StringBuilder();
        modelSourceClass.addGetModelForKieBaseMethod(sb);
        String retrieved = sb.toString();
        String unexpected = "switch (kieBaseName) {";
        assertFalse(retrieved.contains(unexpected));
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
        assertTrue(retrieved.contains(expected));
        expected = "case \"default-kie\": return getModels();";
        assertTrue(retrieved.contains(expected));
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
        assertTrue(retrieved.contains(expected));
        expected = "case \"default-kie\": return getModels();";
        assertTrue(retrieved.contains(expected));
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
        assertTrue(retrieved.contains(expected));
        expected = "case \"default-kie\": return java.util.Arrays.asList( new NotModelTest() );";
        assertTrue(retrieved.contains(expected));
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
        assertTrue(retrieved.contains(expected));
        expected = "case \"default-kie\": return java.util.Arrays.asList( new ModelTest() );";
        assertTrue(retrieved.contains(expected));
    }

    private KieBaseModel getKieBaseModel(String modelName) {
        KieModuleModel kieModuleModel = new KieModuleModelImpl();
        return new KieBaseModelImpl(kieModuleModel, modelName);
    }

}