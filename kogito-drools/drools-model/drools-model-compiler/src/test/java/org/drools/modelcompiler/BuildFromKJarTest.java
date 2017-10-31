/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.util.TestFileUtils;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class BuildFromKJarTest {

    @Test
    public void test() {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "kjar-test", "1.0" );

        File jarFile = createJarFile(ks, releaseId);

//        executeSession( ks, releaseId );

        KieRepository repo = ks.getRepository();
        repo.removeKieModule( releaseId );

        KieModule zipKieModule = new CanonicalKieModule( releaseId, getDefaultKieModuleModel( ks ), jarFile );
        repo.addKieModule( zipKieModule );

        executeSession( ks, releaseId );
    }

    private void executeSession( KieServices ks, ReleaseId releaseId ) {
        KieContainer kieContainer = ks.newKieContainer( releaseId );
        KieSession kieSession = kieContainer.newKieSession();

        kieSession.insert(new Person( "Mark", 37) );
        kieSession.insert(new Person("Edson", 35));
        kieSession.insert(new Person("Mario", 40));
        kieSession.fireAllRules();
    }

    private File createJarFile(KieServices ks, ReleaseId releaseId) {

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(getDefaultKieModuleModel(ks).toXML());
        kfs.writePomXML(KJARUtils.getPom(releaseId));

        String javaSrc = Person.class.getCanonicalName().replace( '.', File.separatorChar ) + ".java";
        Resource javaResource = ks.getResources().newFileSystemResource( "src/test/java/" + javaSrc );
        kfs.write( "src/main/java/" + javaSrc, javaResource );

        kfs.write("src/main/resources/rule.drl", getRule());

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        List<Message> messages = ( (KieBuilderImpl) kieBuilder ).buildAll( CanonicalModelKieProject::new )
                                                                .getResults().getMessages();
        if (!messages.isEmpty()) {
            fail(messages.toString());
        }

        InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();
        return TestFileUtils.bytesToTempKJARFile( releaseId, kieModule.getBytes(), ".jar" );
    }


    private KieModuleModel getDefaultKieModuleModel(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "kbase" ).setDefault( true );
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel( "ksession" ).setDefault( true );
        return kproj;

    }

    private String getRule() {
        return "package myrules;\n" +
               "import " + Person.class.getCanonicalName() + ";\n" +
               "rule beta when\n" +
               "  $p1 : Person(name == \"Mark\")\n" +
               "  $p2 : Person(name != \"Mark\", age > $p1.age)\n" +
               "then\n" +
               "  System.out.println($p2.getName() + \" is older than \" + $p1.getName());\n" +
               "end";
    }

}
