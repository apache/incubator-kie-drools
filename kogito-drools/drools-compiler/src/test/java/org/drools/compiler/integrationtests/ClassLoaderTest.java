/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassLoaderTest extends CommonTestMethodBase {

    @Test
    public void testClassLoaderGetResourcesFromWithin() throws IOException {
        // DROOLS-1108
        KieServices kieServices = KieServices.Factory.get();
        String drl1 = "package org.drools.testdrl;\n" +
                      "global java.util.List list;\n" +
                      "rule R when\n" +
                      "then\n" +
                      "   java.net.URL url = drools.getProjectClassLoader().getResource(\"META-INF/foo.xml\");\n" +
                      "   if (url != null) list.add(url);\n" +
                      "end\n";

        Resource resource1 = kieServices.getResources().newReaderResource( new StringReader( drl1 ), "UTF-8" );
        resource1.setTargetPath("org/drools/testdrl/rules1.drl");

        String foo = "<xyz/>\n";
        Resource resource2 = kieServices.getResources().newReaderResource( new StringReader( foo ), "UTF-8" );
        resource2.setTargetPath( "META-INF/foo.xml" );

        String kmodule = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                         "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                         "  <kbase name=\"testKbase\" packages=\"org.drools.testdrl\">\n" +
                         "    <ksession name=\"testKsession\"/>\n" +
                         "  </kbase>\n" +
                         "</kmodule>";

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId = kieServices.newReleaseId( "org.kie", "test-cl", "1.0.0" );
        createAndDeployJar( kieServices, kmodule, releaseId, resource1, resource2 );

        KieContainer kieContainer = kieServices.newKieContainer( releaseId );
        ClassLoader classLoader = kieContainer.getClassLoader();
        URL url = classLoader.getResource( "META-INF/foo.xml" );
        assertNotNull( url );

        KieSession ksession = kieContainer.newKieSession( "testKsession" );

        List<URL> list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( url.getPath(), list.get(0).getPath() );
        ksession.dispose();
    }

    @Test
    public void testClassLoaderFromPojo() throws IOException {
        // DROOLS-1108
        String source = "package org.drools.testdrl;\n" +
                        "public class MyPojo {\n" +
                        "    public String getUrlPath() {" +
                        "        return getClass().getClassLoader().getResource(\"META-INF/foo.xml\").getPath();\n" +
                        "    }\n" +
                        "}\n";

        String drl1 = "package org.drools.testdrl;\n" +
                      "import org.drools.testdrl.MyPojo;\n" +
                      "global java.util.List list;\n" +
                      "rule R1 when\n" +
                      "then\n" +
                      "   insert(new MyPojo());\n" +
                      "end\n" +
                      "rule R2 when\n" +
                      "    $m : MyPojo()\n" +
                      "then\n" +
                      "   list.add($m.getUrlPath());\n" +
                      "end\n";

        String foo = "<xyz/>\n";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "test-cl", "1.0.0" );

        KieFileSystem kfs = ks.newKieFileSystem();

        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "testKbase" )
                                          .setDefault( true )
                                          .addPackage( "org.drools.testdrl" );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel( "testKsession" )
                                                 .setDefault( true );

        kfs.writeKModuleXML(kproj.toXML());

        kfs.write("src/main/resources/org/drools/testdrl/rules1.drl", drl1);
        kfs.write("src/main/java/org/drools/testdrl/MyPojo.java", source);
        kfs.write("src/main/resources/META-INF/foo.xml", foo);
        kfs.generateAndWritePomXML(releaseId);

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());

        KieContainer kieContainer = ks.newKieContainer( releaseId );
        ClassLoader classLoader = kieContainer.getClassLoader();
        URL url = classLoader.getResource( "META-INF/foo.xml" );
        assertNotNull( url );

        KieSession ksession = kieContainer.newKieSession( "testKsession" );

        List<URL> list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( url.getPath(), list.get(0) );
        ksession.dispose();
    }
}
