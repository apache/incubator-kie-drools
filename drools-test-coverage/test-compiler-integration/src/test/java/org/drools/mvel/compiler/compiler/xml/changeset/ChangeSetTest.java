/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.mvel.compiler.compiler.xml.changeset;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.core.io.impl.UrlResource;
import org.drools.core.xml.XmlChangeSetReader;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.ChangeSet;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.io.ResourceFactory;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ChangeSetTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ChangeSetTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testXmlParser() throws SAXException,
                               IOException {

        KnowledgeBuilderConfigurationImpl conf = new KnowledgeBuilderConfigurationImpl();
        XmlChangeSetReader xmlReader = new XmlChangeSetReader( conf.getSemanticModules() );
        xmlReader.setClassLoader( ChangeSetTest.class.getClassLoader(), ChangeSetTest.class );

        String str = "";
        str += "<change-set ";
        str += "xmlns='http://drools.org/drools-5.0/change-set' ";
        str += "xmlns:xs='http://www.w3.org/2001/XMLSchema-instance' ";
        str += "xs:schemaLocation='http://drools.org/drools-5.0/change-set change-set-1.0.0.xsd' >";
        str += "    <add> ";
        str += "        <resource source='http://www.domain.com/test.drl' type='DRL' />";
        str += "        <resource source='http://www.domain.com/test.xls' type='DTABLE' >";
        str += "            <decisiontable-conf worksheet-name='sheet10' input-type='XLS' />";
        str += "        </resource>";
        str += "    </add> ";
        str += "</change-set>";

        StringReader reader = new StringReader( str );
        ChangeSet changeSet = xmlReader.read( reader );

        assertEquals( 2,
                      changeSet.getResourcesAdded().size() );
        UrlResource resource = ( UrlResource ) ((List)changeSet.getResourcesAdded()).get( 0 );
        assertEquals( "http://www.domain.com/test.drl",
                      resource.getURL().toString() );
        assertEquals( ResourceType.DRL,
                      resource.getResourceType() );

        resource =  ( UrlResource ) ((List)changeSet.getResourcesAdded()).get( 1 );
        
        assertEquals( "http://www.domain.com/test.xls",
                      resource.getURL().toString() );
        assertEquals( ResourceType.DTABLE,
                      resource.getResourceType() );
        DecisionTableConfiguration dtConf = (DecisionTableConfiguration) resource.getConfiguration();
        assertEquals( DecisionTableInputType.XLS,
                      dtConf.getInputType() );
    }

    @Test
    public void testIntegregation() {
        Resource changeSet = ResourceFactory.newClassPathResource("changeset1Test.xml", getClass());
        changeSet.setResourceType(ResourceType.CHANGE_SET);
        KieBase kbase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, changeSet);
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 2,
                      list.size() );
        assertTrue( list.containsAll( Arrays.asList( new String[]{"rule1", "rule2"} ) ) );
    }

    @Test
    public void testBasicAuthentication() throws SAXException,
                               IOException {

        KnowledgeBuilderConfigurationImpl conf = new KnowledgeBuilderConfigurationImpl();
        XmlChangeSetReader xmlReader = new XmlChangeSetReader( conf.getSemanticModules() );
        xmlReader.setClassLoader( ChangeSetTest.class.getClassLoader(), ChangeSetTest.class );

        String str = "";
        str += "<change-set ";
        str += "xmlns='http://drools.org/drools-5.0/change-set' ";
        str += "xmlns:xs='http://www.w3.org/2001/XMLSchema-instance' ";
        str += "xs:schemaLocation='http://drools.org/drools-5.0/change-set change-set-1.0.0.xsd' >";
        str += "    <add> ";
        str += "        <resource source='http://localhost:8081/jboss-brms/org.kie.guvnor.Guvnor/package/defaultPackage/LATEST' type='PKG' basicAuthentication='enabled' username='admin' password='pwd'/>";
        str += "    </add> ";
        str += "</change-set>";

        StringReader reader = new StringReader( str );
        ChangeSet changeSet = xmlReader.read( reader );

        assertEquals( 1,
                      changeSet.getResourcesAdded().size() );
        UrlResource resource = ( UrlResource ) ((List)changeSet.getResourcesAdded()).get( 0 );
        assertEquals( "http://localhost:8081/jboss-brms/org.kie.guvnor.Guvnor/package/defaultPackage/LATEST",
                      resource.getURL().toString() );
        assertEquals( "enabled", resource.getBasicAuthentication() );
        assertEquals( "admin", resource.getUsername() );
        assertEquals( "pwd", resource.getPassword() );
        assertEquals( ResourceType.PKG,
                      resource.getResourceType() );
    }

    @Test(timeout = 10000)
    public void testCustomClassLoader() throws Exception {
        // JBRULES-3630
        String absolutePath = new File("file").getAbsolutePath();

        URL url = ChangeSetTest.class.getResource(ChangeSetTest.class.getSimpleName() + ".class");
        File file = new File( url.toURI() );
        File jar = null;
        while ( true ) {
            file = file.getParentFile();
            jar = new File( file, "/src/test/resources/org/drools/mvel/compiler/compiler/xml/changeset/changeset.jar" );
            if ( jar.exists() ) {
                break;
            }
        }

        ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{jar.toURI().toURL()}, getClass().getClassLoader());
        Resource changeSet = ResourceFactory.newClassPathResource("changeset1.xml", classLoader);

        changeSet.setResourceType(ResourceType.CHANGE_SET);
        final KieModuleModel kieModuleModel = KieUtil.createKieModuleModel(kieBaseTestConfiguration.useAlphaNetworkCompiler());
        final KieFileSystem kieFileSystem = KieUtil.getKieFileSystemWithKieModule(kieModuleModel, KieServices.get().getRepository().getDefaultReleaseId(), changeSet);
        final KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kieFileSystem, classLoader);

        if (kieBaseTestConfiguration.getExecutableModelProjectClass().isPresent()) {
            kbuilder.buildAll(kieBaseTestConfiguration.getExecutableModelProjectClass().get());
        } else {
            kbuilder.buildAll(DrlProject.class);
        }
        List<Message> errors = kbuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());
    }
}
