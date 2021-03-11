/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.mvel.compiler.runtime.pipeline.impl;

import java.util.Collection;
import java.util.List;

import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.Options;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.KieResources;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.JaxbConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * DROOLS-5803 RHDM-851
 */
@RunWith(Parameterized.class)
public class DroolsJaxbHelperXSDInKJARTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DroolsJaxbHelperXSDInKJARTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private static final Logger LOG = LoggerFactory.getLogger(DroolsJaxbHelperXSDInKJARTest.class);

    private static final String simpleXsdRelativePath = "simple.xsd";

    @Test
    public void testInternalsDryRun() {
        // DROOLS-5803 RHDM-851
        System.getProperties().entrySet().forEach(e -> LOG.debug("{}", e));
        LOG.info("{}", javax.xml.parsers.SAXParserFactory.newInstance().getClass());
        LOG.info("{}", javax.xml.validation.SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI).getClass());
        LOG.info("{}", com.sun.xml.bind.v2.util.XmlFactory.createSchemaFactory(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, false).getClass());
    }

    @Test
    public void testXsdModelInKJAR() {
        // DROOLS-5803 RHDM-851
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieResources kieResources = ks.getResources();

        Options xjcOpts = new Options();
        xjcOpts.setSchemaLanguage(Language.XMLSCHEMA);
        JaxbConfiguration jaxbConfiguration = KnowledgeBuilderFactory.newJaxbConfiguration(xjcOpts, "xsd");
        kfs.write(kieResources.newClassPathResource(simpleXsdRelativePath, getClass()).setResourceType(ResourceType.XSD).setConfiguration(jaxbConfiguration));

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        if (!errors.isEmpty()) {
            fail("" + errors);
        }

        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        assertNotNull(ksession);
    }

}
