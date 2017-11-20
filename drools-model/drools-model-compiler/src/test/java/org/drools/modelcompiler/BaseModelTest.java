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

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.drools.modelcompiler.util.TestFileUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public abstract class BaseModelTest {
    public static enum RUN_TYPE {
        USE_CANONICAL_MODEL,
        STANDARD_FROM_DRL;
    }

    @Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{
                BaseModelTest.RUN_TYPE.STANDARD_FROM_DRL,
                BaseModelTest.RUN_TYPE.USE_CANONICAL_MODEL
        };
    }

    protected final CompilerTest.RUN_TYPE testRunType;

    public BaseModelTest( CompilerTest.RUN_TYPE testRunType ) {
        this.testRunType = testRunType;
    }

    protected KieSession getKieSession( String... rules ) {
        return getKieSession(null, rules);
    }

    protected KieSession getKieSession(KieModuleModel model, String... stringRules) {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "kjar-test-" + UUID.randomUUID(), "1.0" );

        KieBuilder kieBuilder = createKieBuilder( ks, model, releaseId, stringRules );

        return getKieContainer( ks, model, releaseId, kieBuilder ).newKieSession();
    }

    protected KieContainer getKieContainer( KieServices ks, KieModuleModel model, ReleaseId releaseId, KieBuilder kieBuilder ) {
        if ( testRunType == RUN_TYPE.USE_CANONICAL_MODEL ) {
            addKieModuleFromCanonicalModel( ks, model, releaseId, (InternalKieModule) kieBuilder.getKieModule() );
        }
        return ks.newKieContainer( releaseId );
    }

    protected void addKieModuleFromCanonicalModel( KieServices ks, KieModuleModel model, ReleaseId releaseId, InternalKieModule kieModule ) {
        File kjarFile = TestFileUtils.bytesToTempKJARFile( releaseId, kieModule.getBytes(), ".jar" );
        KieModule zipKieModule = new CanonicalKieModule( releaseId, model != null ? model : getDefaultKieModuleModel( ks ), kjarFile );
        ks.getRepository().addKieModule( zipKieModule );
    }

    protected KieBuilder createKieBuilder( KieServices ks, KieModuleModel model, ReleaseId releaseId, String... stringRules ) {
        ks.getRepository().removeKieModule( releaseId );

        KieFileSystem kfs = ks.newKieFileSystem();
        if ( model != null ) {
            kfs.writeKModuleXML( model.toXML() );
        }
        kfs.writePomXML( KJARUtils.getPom( releaseId ) );
        for (int i = 0; i < stringRules.length; i++) {
            kfs.write(String.format("src/main/resources/r%d.drl", i), stringRules[i] );
        }

        KieBuilder kieBuilder = ( testRunType == RUN_TYPE.USE_CANONICAL_MODEL ) ?
                ( (KieBuilderImpl ) ks.newKieBuilder( kfs ) ).buildAll( CanonicalModelKieProject::new ) :
                ks.newKieBuilder( kfs ).buildAll();

        List<Message> messages = kieBuilder.getResults().getMessages();
        if ( !messages.isEmpty() ) {
            fail( messages.toString() );
        }
        return kieBuilder;
    }

    protected KieModuleModel getDefaultKieModuleModel( KieServices ks ) {
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "kbase" ).setDefault( true );
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel( "ksession" ).setDefault( true );
        return kproj;
    }

    public static <T> List<T> getObjectsIntoList(KieSession ksession, Class<T> clazz) {
        return (List<T>) ksession.getObjects(new ClassObjectFilter(clazz)).stream().collect(Collectors.toList());
    }
}
