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

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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

    private final CompilerTest.RUN_TYPE testRunType;

    public BaseModelTest( CompilerTest.RUN_TYPE testRunType ) {
        this.testRunType = testRunType;
    }

    protected KieSession getKieSession( String str ) {
        return getKieSession( str, null );
    }

    protected KieSession getKieSession( String str, KieModuleModel model ) {
        KieServices ks = KieServices.get();

        ReleaseId releaseId = ks.newReleaseId( "org.kie", "kjar-test-" + UUID.randomUUID(), "1.0" );

        KieRepository repo = ks.getRepository();
        repo.removeKieModule( releaseId );

        KieFileSystem kfs = ks.newKieFileSystem();
        if ( model != null ) {
            kfs.writeKModuleXML( model.toXML() );
        }
        kfs.writePomXML( KJARUtils.getPom( releaseId ) );
        kfs.write( "src/main/resources/r1.drl", str );
// This is actually taken from classloader of test (?) - or anyway it must, because the test are instantiating directly Person.
//        String javaSrc = Person.class.getCanonicalName().replace( '.', File.separatorChar ) + ".java";
//        Resource javaResource = ks.getResources().newFileSystemResource( "src/test/java/" + javaSrc );
//        kfs.write( "src/main/java/" + javaSrc, javaResource );

        KieBuilder kieBuilder = ( testRunType == CompilerTest.RUN_TYPE.USE_CANONICAL_MODEL ) ?
                ( (KieBuilderImpl ) ks.newKieBuilder( kfs ) ).buildAll( CanonicalModelKieProject::new ) :
                ks.newKieBuilder( kfs ).buildAll();
        List<Message> messages = kieBuilder.getResults().getMessages();
        if ( !messages.isEmpty() ) {
            fail( messages.toString() );
        }

        if ( testRunType == CompilerTest.RUN_TYPE.STANDARD_FROM_DRL ) {
            return ks.newKieContainer( releaseId ).newKieSession();
        } else {
            InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();
            File kjarFile = TestFileUtils.bytesToTempKJARFile( releaseId, kieModule.getBytes(), ".jar" );
            KieModule zipKieModule = new CanonicalKieModule( releaseId, model != null ? model : getDefaultKieModuleModel( ks ), kjarFile );
            repo.addKieModule( zipKieModule );

            KieContainer kieContainer = ks.newKieContainer( releaseId );
            KieSession kieSession = kieContainer.newKieSession();

            return kieSession;
        }
    }

    private KieModuleModel getDefaultKieModuleModel( KieServices ks ) {
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "kbase" ).setDefault( true );
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel( "ksession" ).setDefault( true );
        return kproj;
    }

    public static <T> Collection<T> getObjects( KieSession ksession, Class<T> clazz ) {
        return (Collection<T>) ksession.getObjects( new ClassObjectFilter( clazz ) );
    }
}
