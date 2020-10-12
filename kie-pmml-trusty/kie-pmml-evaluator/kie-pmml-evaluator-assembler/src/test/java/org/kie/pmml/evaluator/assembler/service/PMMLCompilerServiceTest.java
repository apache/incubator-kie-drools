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

import org.drools.core.io.impl.FileSystemResource;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.pmml.commons.HasRule;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;

import static org.drools.core.util.StringUtils.generateUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.evaluator.assembler.factories.PMMLRuleMapperFactory.KIE_PMML_RULE_MAPPER_CLASS_NAME;
import static org.kie.pmml.evaluator.assembler.factories.PMMLRuleMappersFactory.KIE_PMML_RULE_MAPPERS_CLASS_NAME;

public class PMMLCompilerServiceTest {

    @Test
    public void populateWithPMMLRuleMappers() {
        final List<KiePMMLModel> toPopulate = new ArrayList<>();
        toPopulate.add( new KiePMMLModelHasSourceMap("TEST", Collections.emptyList()));
        toPopulate.add( new KiePMMLModelHasRule("TEST", Collections.emptyList()));
        toPopulate.add( new KiePMMLModelHasNestedModelsHasRule("TEST", Collections.emptyList()));
        toPopulate.add( new KiePMMLModelHasNestedModelsHasSourceMap("TEST", Collections.emptyList()));
        toPopulate.forEach(kiePMMLModel -> assertTrue(((HasSourcesMap) kiePMMLModel).getSourcesMap().isEmpty()));
        final File file = new File("foo.pmml");
        final Resource resource = new FileSystemResource(file);
        PMMLCompilerService.populateWithPMMLRuleMappers(toPopulate, resource);
        toPopulate.forEach(kiePmmlModel -> {
            if (kiePmmlModel instanceof HasRule || kiePmmlModel instanceof KiePMMLModelHasNestedModelsHasRule) {
                assertFalse(((HasSourcesMap) kiePmmlModel).getSourcesMap().isEmpty());
                String expected =  kiePmmlModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPERS_CLASS_NAME;
                assertTrue(((HasSourcesMap) kiePmmlModel).getSourcesMap().containsKey(expected));
                if (kiePmmlModel instanceof HasRule) {
                    expected =  kiePmmlModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME;
                    assertTrue(((HasSourcesMap) kiePmmlModel).getSourcesMap().containsKey(expected));
                }
            } else {
                assertTrue(((HasSourcesMap) kiePmmlModel).getSourcesMap().isEmpty());
            }
        });
    }

    @Test
    public void addPMMLRuleMapperHasSourcesMap() {
        KiePMMLModelHasSourceMap kiePmmlModel = new KiePMMLModelHasSourceMap("TEST", Collections.emptyList());
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        final List<String> generatedRuleMappers = IntStream.range(0, 3).mapToObj(i -> "apackage.Rule_" + i).collect(Collectors.toList());
        PMMLCompilerService.addPMMLRuleMapper(kiePmmlModel, generatedRuleMappers, "source_path");
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
    }

    @Test
    public void addPMMLRuleMapperHasRule() {
        KiePMMLModelHasRule kiePmmlModel = new KiePMMLModelHasRule("TEST", Collections.emptyList());
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        final List<String> generatedRuleMappers = new ArrayList<>();
        PMMLCompilerService.addPMMLRuleMapper(kiePmmlModel, generatedRuleMappers, "source_path");
        String expected = kiePmmlModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME;
        assertTrue(kiePmmlModel.sourcesMap.containsKey(expected));
        expected =  kiePmmlModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME;
        assertTrue(generatedRuleMappers.contains(expected));
    }

    @Test
    public void addPMMLRuleMapperKiePMMLModelHasNestedModelsHasRule() {
        KiePMMLModelHasNestedModelsHasRule kiePmmlModel = new KiePMMLModelHasNestedModelsHasRule("TEST",
                                                                                                 Collections.emptyList());
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        final List<String> generatedRuleMappers = new ArrayList<>();
        PMMLCompilerService.addPMMLRuleMapper(kiePmmlModel, generatedRuleMappers, "source_path");
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        assertEquals(kiePmmlModel.nestedModels.size(), generatedRuleMappers.size());
        generatedRuleMappers.forEach(ret -> assertEquals(kiePmmlModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME, ret));
        kiePmmlModel.nestedModels.forEach(nestedModel -> {
            assertTrue(((HasSourcesMap) nestedModel).getSourcesMap().containsKey(nestedModel.getKModulePackageName() + "." +KIE_PMML_RULE_MAPPER_CLASS_NAME));
        });
    }

    @Test
    public void addPMMLRuleMapperKiePMMLModelHasNestedModelsHasSourceMap() {
        KiePMMLModelHasNestedModelsHasSourceMap kiePmmlModel = new KiePMMLModelHasNestedModelsHasSourceMap("TEST",
                                                                                                           Collections.emptyList());
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        final List<String> generatedRuleMappers = new ArrayList<>();
        PMMLCompilerService.addPMMLRuleMapper(kiePmmlModel, generatedRuleMappers, "source_path");
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        assertTrue(generatedRuleMappers.isEmpty());
        kiePmmlModel.nestedModels.forEach(nestedModel -> {
            assertTrue(((HasSourcesMap) nestedModel).getSourcesMap().isEmpty());
        });
    }

    @Test(expected = KiePMMLException.class)
    public void addPMMLRuleMapperNoHasSourceMap() {
        final KiePMMLModel kiePmmlModel = new KiePMMLModel("name", Collections.emptyList()) {
            @Override
            public Object evaluate(Object knowledgeBase, Map<String, Object> requestData) {
                return null;
            }
        };
        PMMLCompilerService.addPMMLRuleMapper(kiePmmlModel, new ArrayList<>(), "source_path");
    }

    @Test
    public void addPMMLRuleMappersHasSourceMap() {
        KiePMMLModelHasSourceMap kiePmmlModel = new KiePMMLModelHasSourceMap("TEST", Collections.emptyList());
        assertTrue(kiePmmlModel.sourcesMap.isEmpty());
        final List<String> generatedRuleMappers = IntStream.range(0, 3)
                .mapToObj(i -> "apackage" + i + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME).collect(Collectors.toList());
        PMMLCompilerService.addPMMLRuleMappers(kiePmmlModel, generatedRuleMappers, "source_path");
        assertFalse(kiePmmlModel.sourcesMap.isEmpty());
        String expected =  kiePmmlModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPERS_CLASS_NAME;
        assertTrue(kiePmmlModel.sourcesMap.containsKey(expected));
    }

    @Test(expected = KiePMMLException.class)
    public void addPMMLRuleMappersNotHasSourceMap() {
        final KiePMMLModel kiePmmlModel = getKiePMMLModel();
        final List<String> generatedRuleMappers = IntStream.range(0, 3)
                .mapToObj(i -> "apackage" + i + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME).collect(Collectors.toList());
        PMMLCompilerService.addPMMLRuleMappers(kiePmmlModel, generatedRuleMappers, "source_path");
    }

    @Test
    public void getFileName() {
        String fileName = "TestFile.pmml";
        String fullPath = String.format("%1$sthis%1$sis%1$sfull%1$spath%1$s%2$s",
                                        File.separator,
                                        fileName);
        String retrieved = PMMLCompilerService.getFileName(fullPath);
        assertEquals(fileName, retrieved);
        fullPath = String.format("%1$sthis%1$sis%1$sfull%1$spath%1$s%2$s",
                                        "/",
                                        fileName);
        retrieved = PMMLCompilerService.getFileName(fullPath);
        assertEquals(fileName, retrieved);
    }

    private KiePMMLModel getKiePMMLModel() {
        return new KiePMMLModel("name", Collections.emptyList()) {
            @Override
            public Object evaluate(Object knowledgeBase, Map<String, Object> requestData) {
                return null;
            }
        };
    }

    private static class KiePMMLModelHasSourceMap extends KiePMMLModel implements HasSourcesMap {

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

    private static class KiePMMLModelHasRule extends KiePMMLModelHasSourceMap implements HasRule {

        private final String pkgUUID = generateUUID();

        public KiePMMLModelHasRule(String name, List<KiePMMLExtension> extensions) {
            super(name, extensions);
        }

        @Override
        public String getPkgUUID() {
            return pkgUUID;
        }
    }

    private static class KiePMMLModelHasNestedModelsHasRule extends KiePMMLModelHasSourceMap implements HasNestedModels {

        final List<KiePMMLModel> nestedModels = new ArrayList<>();

        public KiePMMLModelHasNestedModelsHasRule(String name, List<KiePMMLExtension> extensions) {
            super(name, extensions);
            IntStream.range(0, 3).forEach(i -> nestedModels.add(new KiePMMLModelHasRule(name + "_" + i,
                                                                                        Collections.emptyList())));
        }

        @Override
        public List<KiePMMLModel> getNestedModels() {
            return nestedModels;
        }
    }

    private static class KiePMMLModelHasNestedModelsHasSourceMap extends KiePMMLModelHasSourceMap implements HasNestedModels {

        final List<KiePMMLModel> nestedModels = new ArrayList<>();

        public KiePMMLModelHasNestedModelsHasSourceMap(String name, List<KiePMMLExtension> extensions) {
            super(name, extensions);
            IntStream.range(0, 3).forEach(i -> nestedModels.add(new KiePMMLModelHasSourceMap(name + "_" + i,
                                                                                             Collections.emptyList())));
        }

        @Override
        public List<KiePMMLModel> getNestedModels() {
            return nestedModels;
        }
    }
}