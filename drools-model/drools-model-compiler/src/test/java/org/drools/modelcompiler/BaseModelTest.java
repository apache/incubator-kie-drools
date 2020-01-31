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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static java.util.Arrays.asList;
import static org.drools.modelcompiler.BaseModelTest.RUN_TYPE.FLOW_DSL;
import static org.drools.modelcompiler.BaseModelTest.RUN_TYPE.FLOW_WITH_ALPHA_NETWORK;
import static org.drools.modelcompiler.BaseModelTest.RUN_TYPE.PATTERN_DSL;
import static org.drools.modelcompiler.BaseModelTest.RUN_TYPE.PATTERN_WITH_ALPHA_NETWORK;
import static org.drools.modelcompiler.BaseModelTest.RUN_TYPE.STANDARD_WITH_ALPHA_NETWORK;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public abstract class BaseModelTest {
    public enum RUN_TYPE {
        FLOW_DSL( false ),
        PATTERN_DSL( false ),
        STANDARD_FROM_DRL( false ),
        STANDARD_WITH_ALPHA_NETWORK( true ),
        PATTERN_WITH_ALPHA_NETWORK( true ),
        FLOW_WITH_ALPHA_NETWORK( true );

        private boolean alphaNetworkCompiler;

        RUN_TYPE( boolean isAlphaNetworkCompiler ) {
            this.alphaNetworkCompiler = isAlphaNetworkCompiler;
        }

        public boolean isAlphaNetworkCompiler() {
            return alphaNetworkCompiler;
        }
    }

    final static Object[] PLAIN = {
            RUN_TYPE.STANDARD_FROM_DRL,
            FLOW_DSL,
            PATTERN_DSL,
    };

    final static Object[] WITH_ALPHA_NETWORK = {
            RUN_TYPE.STANDARD_FROM_DRL,
            FLOW_DSL,
            PATTERN_DSL,
            STANDARD_WITH_ALPHA_NETWORK,
            PATTERN_WITH_ALPHA_NETWORK,
            FLOW_WITH_ALPHA_NETWORK,
    };


    @Parameters(name = "{0}")
    public static Object[] params() {
        if(Boolean.valueOf(System.getProperty("alphanetworkCompilerEnabled"))) {
            return WITH_ALPHA_NETWORK;
        } else {
            return PLAIN;
        }
    }

    protected final CompilerTest.RUN_TYPE testRunType;

    public BaseModelTest( CompilerTest.RUN_TYPE testRunType ) {
        this.testRunType = testRunType;
    }

    protected KieSession getKieSession(String... rules) {
        KieModuleModel kproj = testRunType.isAlphaNetworkCompiler() ? getKieModuleModelWithAlphaNetworkCompiler() : null;
        return getKieSession(kproj, rules);
    }

    protected KieSession getKieSession(KieModuleModel model, String... stringRules) {
        return getKieContainer( model, stringRules ).newKieSession();
    }

    protected KieContainer getKieContainer( KieModuleModel model, String... stringRules ) {
        return getKieContainer( model, toKieFiles( stringRules ) );
    }

    protected KieContainer getKieContainer( KieModuleModel model, KieFile... stringRules ) {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "kjar-test-" + UUID.randomUUID(), "1.0" );

        KieBuilder kieBuilder = createKieBuilder( ks, model, releaseId, stringRules );
        return ks.newKieContainer( releaseId );
    }

    protected KieBuilder createKieBuilder( String... stringRules ) {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "kjar-test-" + UUID.randomUUID(), "1.0" );
        return createKieBuilder( ks, null, releaseId, false, toKieFiles( stringRules ) );
    }

    protected KieBuilder createKieBuilder( KieServices ks, KieModuleModel model, ReleaseId releaseId, KieFile... stringRules ) {
        return createKieBuilder( ks, model, releaseId, true, stringRules );
    }

    protected KieBuilder createKieBuilder( KieServices ks, KieModuleModel model, ReleaseId releaseId, boolean failIfBuildError, KieFile... stringRules ) {
        ks.getRepository().removeKieModule( releaseId );

        KieFileSystem kfs = ks.newKieFileSystem();
        if ( model != null ) {
            kfs.writeKModuleXML( model.toXML() );
        }
        kfs.writePomXML( KJARUtils.getPom( releaseId ) );
        for (int i = 0; i < stringRules.length; i++) {
            kfs.write( stringRules[i].path, stringRules[i].content );
        }

        KieBuilder kieBuilder;
        if (asList(FLOW_DSL, FLOW_WITH_ALPHA_NETWORK).contains(testRunType)) {
            kieBuilder = ks.newKieBuilder(kfs).buildAll(ExecutableModelFlowProject.class);
        } else if (asList(PATTERN_DSL, PATTERN_WITH_ALPHA_NETWORK).contains(testRunType)) {
            kieBuilder = ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        } else {
            kieBuilder = ks.newKieBuilder(kfs).buildAll(DrlProject.class);
        }

        if ( failIfBuildError ) {
            List<Message> messages = kieBuilder.getResults().getMessages();
            if ( !messages.isEmpty() ) {
                fail( messages.toString() );
            }
        }

        return kieBuilder;
    }

    protected KieModuleModel getDefaultKieModuleModel( KieServices ks ) {
        KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel( "kbase" ).setDefault( true ).newKieSessionModel( "ksession" ).setDefault( true );
        return kproj;
    }

    public static <T> List<T> getObjectsIntoList(KieSession ksession, Class<T> clazz) {
        return (List<T>) ksession.getObjects(new ClassObjectFilter(clazz)).stream().collect(Collectors.toList());
    }

    protected void createAndDeployJar( KieServices ks, ReleaseId releaseId, String... drls ) {
        createAndDeployJar( ks, null, releaseId, drls );
    }

    protected void createAndDeployJar( KieServices ks, ReleaseId releaseId, KieFile... ruleFiles ) {
        createAndDeployJar( ks, null, releaseId, ruleFiles );
    }

    protected void createAndDeployJar( KieServices ks, KieModuleModel model, ReleaseId releaseId, String... drls ) {
        createAndDeployJar( ks, model, releaseId, toKieFiles( drls ) );
    }

    protected void createAndDeployJar( KieServices ks, KieModuleModel model, ReleaseId releaseId, KieFile... ruleFiles ) {
        KieBuilder kieBuilder = createKieBuilder( ks, model, releaseId, ruleFiles );
        InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();
        ks.getRepository().addKieModule( kieModule );
    }

    public static class KieFile {

        public final String path;
        public final String content;

        public KieFile( int index, String content ) {
            this( String.format("src/main/resources/r%d.drl", index), content );
        }

        public KieFile( String path, String content ) {
            this.path = path;
            this.content = content;
        }
    }

    public KieFile[] toKieFiles(String[] stringRules) {
        KieFile[] kieFiles = new KieFile[stringRules.length];
        for (int i = 0; i < stringRules.length; i++) {
            kieFiles[i] = new KieFile( i, stringRules[i] );
        }
        return kieFiles;
    }

    private KieModuleModel getKieModuleModelWithAlphaNetworkCompiler() {
        KieModuleModel kproj = KieServices.get().newKieModuleModel();
        kproj.setConfigurationProperty( org.drools.compiler.kie.builder.impl.KieContainerImpl.ALPHA_NETWORK_COMPILER_OPTION, "true" );
        return kproj;
    }
}
