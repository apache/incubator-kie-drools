/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.evaluator.assembler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.pmml.commons.HasRule;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.evaluator.assembler.factories.PMMLRuleMapperFactory.KIE_PMML_RULE_MAPPER_CLASS_NAME;
import static org.kie.pmml.evaluator.assembler.factories.PMMLRuleMappersFactory.KIE_PMML_RULE_MAPPERS_CLASS_NAME;

public class PMMLCompilerServiceTest {

    private static final ReleaseId RELEASE_ID = new ReleaseIdImpl("dummy:dummy:0.0");

    @Test
    public void addPMMLRuleMapperHasSourcesMap() {
        KiePMMLModelHasSourceMap kiePmmlModel = new KiePMMLModelHasSourceMap("TEST", Collections.emptyList());
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        final List<String> generatedRuleMappers = IntStream.range(0, 3).mapToObj(i -> "apackage.Rule_" + i).collect(Collectors.toList());
        PMMLCompilerService.addPMMLRuleMapper(kiePmmlModel, generatedRuleMappers, "source_path", RELEASE_ID);
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
    }

    @Test
    public void addPMMLRuleMapperHasRule() {
        KiePMMLModelHasRule kiePmmlModel = new KiePMMLModelHasRule("TEST", Collections.emptyList());
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        final List<String> generatedRuleMappers = new ArrayList<>();
        PMMLCompilerService.addPMMLRuleMapper(kiePmmlModel, generatedRuleMappers, "source_path", RELEASE_ID);
        String expected =  kiePmmlModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME;
        assertTrue(kiePmmlModel.sourcesMap.containsKey(expected));
        expected =  kiePmmlModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME;
        assertTrue(generatedRuleMappers.contains(expected));
    }

    @Test
    public void addPMMLRuleMapperKiePMMLModelHasNestedModelsHasRule() {
        KiePMMLModelHasNestedModelsHasRule kiePmmlModel = new KiePMMLModelHasNestedModelsHasRule("TEST", Collections.emptyList());
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        final List<String> generatedRuleMappers = new ArrayList<>();
        PMMLCompilerService.addPMMLRuleMapper(kiePmmlModel, generatedRuleMappers, "source_path", RELEASE_ID);
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        assertEquals(kiePmmlModel.nestedModels.size(), generatedRuleMappers.size());
        generatedRuleMappers.forEach(ret -> assertEquals(kiePmmlModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME, ret));
        kiePmmlModel.nestedModels.forEach(nestedModel -> {
            assertTrue(((HasSourcesMap) nestedModel).getSourcesMap().containsKey(nestedModel.getKModulePackageName() + "." +KIE_PMML_RULE_MAPPER_CLASS_NAME));
        });
    }

    @Test
    public void addPMMLRuleMapperKiePMMLModelHasNestedModelsHasSourceMap() {
        KiePMMLModelHasNestedModelsHasSourceMap kiePmmlModel = new KiePMMLModelHasNestedModelsHasSourceMap("TEST", Collections.emptyList());
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        final List<String> generatedRuleMappers = new ArrayList<>();
        PMMLCompilerService.addPMMLRuleMapper(kiePmmlModel, generatedRuleMappers, "source_path", RELEASE_ID);
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        assertTrue(generatedRuleMappers.isEmpty());
        kiePmmlModel.nestedModels.forEach(nestedModel -> {
            assertTrue(((HasSourcesMap) nestedModel).getSourcesMap().isEmpty());
        });
    }


    @Test
    public void addPMMLRuleMappers() {
        KiePMMLModelHasSourceMap kiePmmlModel = new KiePMMLModelHasSourceMap("TEST", Collections.emptyList());
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        final List<String> generatedRuleMappers = IntStream.range(0, 3)
                .mapToObj(i -> "apackage" + i + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME).collect(Collectors.toList());
        PMMLCompilerService.addPMMLRuleMappers(kiePmmlModel, generatedRuleMappers, "source_path");
        assertFalse(kiePmmlModel.sourcesMap.isEmpty());
        String expected =  kiePmmlModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPERS_CLASS_NAME;
        assertTrue(kiePmmlModel.sourcesMap.containsKey(expected));
    }


    @Test
    public void getFileName() {
        String fileName = "TestFile.pmml";
        String fullPath = String.format("%1$sthis%1$sis%1$sfull%1$spath%1$s%2$s",
                                        File.separator,
                                        fileName);
        String retrieved = PMMLCompilerService.getFileName(fullPath);
        assertEquals(fileName, retrieved);
    }

    private class KiePMMLModelHasSourceMap extends KiePMMLModel implements HasSourcesMap {

        protected final Map<String, String> sourcesMap = new HashMap<>();

        public KiePMMLModelHasSourceMap(String name, List<KiePMMLExtension> extensions) {
            super(name, extensions);
        }

        @Override
        public Map<String, String> getSourcesMap() {
            return Collections.unmodifiableMap(sourcesMap);
        }

        @Override
        public void addSourceMap(String key, String value) {
            sourcesMap.put(key, value);
        }

        @Override
        public Object evaluate(Object knowledgeBase, Map<String, Object> requestData) {
            return null;
        }
    }

    private class KiePMMLModelHasRule extends KiePMMLModelHasSourceMap implements HasRule {

        public KiePMMLModelHasRule(String name, List<KiePMMLExtension> extensions) {
            super(name, extensions);
        }
    }

    private class KiePMMLModelHasNestedModelsHasRule extends KiePMMLModelHasSourceMap implements HasNestedModels {

        final List<KiePMMLModel> nestedModels = new ArrayList<>();

        public KiePMMLModelHasNestedModelsHasRule(String name, List<KiePMMLExtension> extensions) {
            super(name, extensions);
            IntStream.range(0, 3).forEach(i -> nestedModels.add(new KiePMMLModelHasRule(name + "_" +i, Collections.emptyList())));
        }

        @Override
        public List<KiePMMLModel> getNestedModels() {
            return nestedModels;
        }
    }

    private class KiePMMLModelHasNestedModelsHasSourceMap extends KiePMMLModelHasSourceMap implements HasNestedModels {

        final List<KiePMMLModel> nestedModels = new ArrayList<>();

        public KiePMMLModelHasNestedModelsHasSourceMap(String name, List<KiePMMLExtension> extensions) {
            super(name, extensions);
            IntStream.range(0, 3).forEach(i -> nestedModels.add(new KiePMMLModelHasSourceMap(name + "_" +i, Collections.emptyList())));
        }

        @Override
        public List<KiePMMLModel> getNestedModels() {
            return nestedModels;
        }
    }
}