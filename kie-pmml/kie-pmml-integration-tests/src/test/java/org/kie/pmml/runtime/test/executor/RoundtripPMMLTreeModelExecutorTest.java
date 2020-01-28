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

package org.kie.pmml.runtime.test.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.tree.KiePMMLTreeModel;
import org.kie.pmml.runtime.api.executor.PMMLContext;
import org.kie.pmml.runtime.api.executor.PMMLRuntime;
import org.kie.pmml.runtime.core.PMMLContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getPMMLRequestData;
import static org.kie.test.util.filesystem.FileUtils.getFile;

public class RoundtripPMMLTreeModelExecutorTest {

    private static final Logger logger = LoggerFactory.getLogger(RoundtripPMMLTreeModelExecutorTest.class);

    private PMMLRuntime pmmlRuntime;

    @Before
    public void setUp() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ResourceFactory.newFileResource( getFile("TreeSample.xml") ).setResourceType( ResourceType.PMML ) );
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        kieBuilder.getKieModule().getReleaseId();
        Results res = kieBuilder.getResults();
        assertNotNull(res);
        assertTrue(res.getMessages(Message.Level.ERROR).isEmpty());
        KieBase kbase = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() ).getKieBase();
        KieSession session = kbase.newKieSession();
        pmmlRuntime = session.getKieRuntime(PMMLRuntime.class);
        assertNotNull(pmmlRuntime);
    }

    @Test
    public void evaluateTree() throws KiePMMLException {
        String modelName = "golfing";
        commonEvaluate(modelName);
    }

    private void commonEvaluate(String modelName) throws KiePMMLException {
        final Optional<KiePMMLModel> model = pmmlRuntime.getModel(modelName);
        assertTrue(model.isPresent());
        assertEquals(PMML_MODEL.TREE_MODEL, model.get().getPmmlMODEL());
        assertTrue(model.get() instanceof KiePMMLTreeModel);
        KiePMMLTreeModel kiePMMLTreeModel = (KiePMMLTreeModel)model.get();
        Map<String, Object> inputData = new HashMap<>();
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, inputData);
        PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        PMML4Result retrieved = pmmlRuntime.evaluate(model.get(), pmmlContext);
        assertNotNull(retrieved);
        logger.info(retrieved.toString());
        assertNotNull(retrieved.getResultVariables());
    }
}