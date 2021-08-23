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

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.model.EntryPoint;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.TypeMetaData;
import org.drools.model.impl.GlobalImpl;
import org.junit.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.compiler.commons.factories.KiePMMLModelFactory;
import org.kie.pmml.evaluator.assembler.rulemapping.PMMLRuleMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PMMLLoaderServiceTest {

    private final String PACKAGE_NAME = "apackage";

    @Test
    public void getKiePMMLModelsLoadedFromFactory() {
        final KnowledgeBuilderImpl kbuilderImpl = new KnowledgeBuilderImpl();
        assertTrue(kbuilderImpl.getPackageNames().isEmpty());
        assertNull(kbuilderImpl.getPackage(PACKAGE_NAME));
        final KiePMMLModelFactory kiePMMLModelFactory = getKiePMMLModelFactory();
        final List<KiePMMLModel> retrieved = PMMLLoaderService.getKiePMMLModelsLoadedFromFactory(kbuilderImpl,
                                                                                                 kiePMMLModelFactory);
        assertEquals(kiePMMLModelFactory.getKiePMMLModels(), retrieved);
        assertTrue(kbuilderImpl.getPackageNames().isEmpty());
        assertNull(kbuilderImpl.getPackage(PACKAGE_NAME));
    }

    @Test
    public void loadPMMLRuleMappersNotEmpty() {
        final KnowledgeBuilderImpl kbuilderImpl = new KnowledgeBuilderImpl();
        assertTrue(kbuilderImpl.getPackageNames().isEmpty());
        assertNull(kbuilderImpl.getPackage(PACKAGE_NAME));
        final List<PMMLRuleMapper> pmmlRuleMappers = getPMMLRuleMappers();
        PMMLLoaderService.loadPMMLRuleMappers(kbuilderImpl, pmmlRuleMappers);
        assertEquals(1, kbuilderImpl.getPackageNames().size());
        assertNotNull(kbuilderImpl.getPackage(PACKAGE_NAME));
    }

    @Test
    public void loadPMMLRuleMappersEmpty() {
        final KnowledgeBuilderImpl kbuilderImpl = new KnowledgeBuilderImpl();
        assertTrue(kbuilderImpl.getPackageNames().isEmpty());
        assertNull(kbuilderImpl.getPackage(PACKAGE_NAME));
        final List<PMMLRuleMapper> pmmlRuleMappers = Collections.emptyList();
        PMMLLoaderService.loadPMMLRuleMappers(kbuilderImpl, pmmlRuleMappers);
        assertTrue(kbuilderImpl.getPackageNames().isEmpty());
        assertNull(kbuilderImpl.getPackage(PACKAGE_NAME));
    }

    private List<PMMLRuleMapper> getPMMLRuleMappers() {
        return IntStream.range(0, 3)
                .mapToObj(i -> new PMMLRuleMapperImpl(new ModelMock(PACKAGE_NAME)))
                .collect(Collectors.toList());
    }

    private KiePMMLModelFactory getKiePMMLModelFactory() {
        final List<KiePMMLModel> kiePMMLModels = IntStream.range(0, 3)
                .mapToObj(i -> KiePMMLTestingModel.builder("KiePMMLModel" + i,
                                                           Collections.emptyList(),
                                                           MINING_FUNCTION.REGRESSION).build())
                .collect(Collectors.toList());
        return () -> kiePMMLModels;
    }

    private static class GlobalMock extends GlobalImpl<Double>  {

        public GlobalMock(String packageName, int id) {
            super(Double.class, packageName, "DOUBLE_GLOBAL_" + id );
        }
    }

    private static class ModelMock implements Model {

        private final String name;
        private final List<Global> globals;

        public ModelMock(String packageName) {
            name = UUID.randomUUID().toString();
            globals = IntStream.range(0, 3)
                    .mapToObj(i -> new GlobalMock(packageName, i))
                    .collect(Collectors.toList());
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Global> getGlobals() {
            return globals;
        }

        @Override
        public List<Rule> getRules() {
            return Collections.emptyList();
        }

        @Override
        public List<Query> getQueries() {
            return Collections.emptyList();
        }

        @Override
        public List<TypeMetaData> getTypeMetaDatas() {
            return Collections.emptyList();
        }

        @Override
        public List<EntryPoint> getEntryPoints() {
            return Collections.emptyList();
        }
    }

    private static class PMMLRuleMapperImpl implements PMMLRuleMapper {

        private final Model model;

        public PMMLRuleMapperImpl(Model model) {
            this.model = model;
        }

        @Override
        public Model getModel() {
            return model;
        }
    }
}