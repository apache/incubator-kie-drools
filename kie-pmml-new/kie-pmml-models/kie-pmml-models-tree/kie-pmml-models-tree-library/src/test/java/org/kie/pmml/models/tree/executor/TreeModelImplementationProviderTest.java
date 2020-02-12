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

package org.kie.pmml.models.tree.executor;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.descr.PackageDescr;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.library.testutils.TestUtils;
import org.kie.pmml.models.tree.api.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TreeModelImplementationProviderTest {

    private static final Logger logger = LoggerFactory.getLogger(TreeModelImplementationProviderTest.class.getName());
    private final static TreeModelImplementationProvider PROVIDER = new TreeModelImplementationProvider();

    private KnowledgeBuilder knowledgeBuilder;

    @Before
    public void setup() {
//        KieServices ks = KieServices.Factory.get();
//        KieFileSystem kfs = ks.newKieFileSystem();
//        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
//        kieBuilder.buildAll()
        knowledgeBuilder = new KnowledgeBuilderImpl();
    }

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.TREE_MODEL, PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() throws JAXBException, SAXException, IOException, KiePMMLException {
        final PMML pmml = TestUtils.loadFromFile("TreeSample.xml");
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof TreeModel);
        final TreeModel originalModel = (TreeModel) pmml.getModels().get(0);
        commonVerifyKiePMMLTreeModel(originalModel, PROVIDER.getKiePMMLModel(pmml.getDataDictionary(), originalModel, knowledgeBuilder));
    }

    @Test
    public void puppa() {
        // TODO {gcardosi} remove - used only to verify/experiment with DescrFactory
        PackageDescrBuilder packBuilder =
                DescrFactory.newPackage()
                        .name("org.drools.compiler")
                        .newRule().name("r1")
                        .lhs()
                        .and()
                        .or()
                        .pattern("StockTick").constraint("price > 100").constraint("price < 200").end()
                        .pattern("StockTick").constraint("price < 10").end()
                        .end()
                        .pattern("StockTick").constraint("company == \"RHT\"").end()
                        .end()
                        .end()
                        .rhs("    System.out.println(\"foo\");\n")
                        .end()
                        .newRule().name("r2")
                        .lhs()
                        .pattern(String.class.getName()).id("ciiccio", false)
                        .end()
                        .end().rhs("puppadone").end();
        PackageDescr pkg = packBuilder.getDescr();
        String drl = new DrlDumper().dump(packBuilder.getDescr());
        logger.info(drl);
    }

    private void commonVerifyKiePMMLTreeModel(TreeModel originalModel, KiePMMLTreeModel kiePMMLTreeModel) {
        assertEquals(originalModel.getModelName(), kiePMMLTreeModel.getName());
        // TODO {gcardosi} complete test
        final PackageDescr retrieved = kiePMMLTreeModel.getPackageDescr();
        assertNotNull(retrieved);
        final DrlDumper dumper = new DrlDumper();
        logger.info(dumper.dump(retrieved));
    }
}