/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.ancompiler;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.model.codegen.ExecutableModelProject;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.AlphaNetworkCompilerOption;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

// Copied from org.drools.modelcompiler.BaseModelTest. To be removed after https://issues.redhat.com/browse/DROOLS-5485
public abstract class BaseModelTest {
    public enum RUN_TYPE {
        PATTERN_DSL( false ),
        STANDARD_FROM_DRL( false ),
        STANDARD_WITH_ALPHA_NETWORK( true ),
        PATTERN_WITH_ALPHA_NETWORK( true );

        private boolean alphaNetworkCompiler;

        RUN_TYPE( boolean isAlphaNetworkCompiler ) {
            this.alphaNetworkCompiler = isAlphaNetworkCompiler;
        }

        public boolean isAlphaNetworkCompiler() {
            return alphaNetworkCompiler;
        }
    }


    final static RUN_TYPE[] WITH_ALPHA_NETWORK = {
            RUN_TYPE.STANDARD_FROM_DRL,
            RUN_TYPE.PATTERN_DSL,
            RUN_TYPE.STANDARD_WITH_ALPHA_NETWORK,
            RUN_TYPE.PATTERN_WITH_ALPHA_NETWORK,
    };

    public static Object[] params() {
        return WITH_ALPHA_NETWORK;
    }
    
    public static Stream<RUN_TYPE> parameters() {
    	return Stream.of(WITH_ALPHA_NETWORK);
    }


    protected KieSession getKieSession(RUN_TYPE testRunType, String... rules) {
        KieModuleModel model = testRunType.isAlphaNetworkCompiler() ? getKieModuleModelWithAlphaNetworkCompiler() : null;
        return getKieSession(testRunType, model, rules);
    }

    protected KieSession getKieSession(RUN_TYPE testRunType, KieModuleModel model, String... stringRules) {
        return getKieContainer(testRunType, model, stringRules ).newKieSession();
    }

    protected KieContainer getKieContainer(RUN_TYPE testRunType, String... stringRules ) {
        KieModuleModel model = testRunType.isAlphaNetworkCompiler() ? getKieModuleModelWithAlphaNetworkCompiler() : null;
        return getKieContainer(testRunType, model, toKieFiles( stringRules ) );
    }

    protected KieContainer getKieContainer(RUN_TYPE testRunType, KieModuleModel model, String... stringRules ) {
        return getKieContainer(testRunType, model, toKieFiles( stringRules ) );
    }

    protected KieContainer getKieContainer(RUN_TYPE testRunType, KieModuleModel model, KieFile... stringRules ) {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "kjar-test-" + UUID.randomUUID(), "1.0" );

        KieBuilder kieBuilder = createKieBuilder(testRunType, ks, model, releaseId, stringRules );
        return ks.newKieContainer( releaseId );
    }

    protected KieBuilder createKieBuilder(RUN_TYPE testRunType, String... stringRules ) {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "kjar-test-" + UUID.randomUUID(), "1.0" );
        return createKieBuilder(testRunType, ks, null, releaseId, false, toKieFiles( stringRules ) );
    }

    protected KieBuilder createKieBuilder(RUN_TYPE testRunType, KieServices ks, KieModuleModel model, ReleaseId releaseId, KieFile... stringRules ) {
        return createKieBuilder(testRunType, ks, model, releaseId, true, stringRules );
    }

    protected KieBuilder createKieBuilder(RUN_TYPE testRunType, KieServices ks, KieModuleModel model, ReleaseId releaseId, boolean failIfBuildError, KieFile... stringRules ) {
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
        if (asList(RUN_TYPE.PATTERN_DSL, RUN_TYPE.PATTERN_WITH_ALPHA_NETWORK).contains(testRunType)) {
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
        return ksession.getInstancesOf(clazz).stream().collect(Collectors.toList());
    }

    protected void createAndDeployJar(RUN_TYPE testRunType, KieServices ks, ReleaseId releaseId, String... drls ) {
        createAndDeployJar(testRunType, ks, null, releaseId, drls );
    }

    protected void createAndDeployJar(RUN_TYPE testRunType, KieServices ks, ReleaseId releaseId, KieFile... ruleFiles ) {
        createAndDeployJar(testRunType, ks, null, releaseId, ruleFiles );
    }

    protected void createAndDeployJar(RUN_TYPE testRunType, KieServices ks, KieModuleModel model, ReleaseId releaseId, String... drls ) {
        createAndDeployJar(testRunType, ks, model, releaseId, toKieFiles( drls ) );
    }

    protected void createAndDeployJar(RUN_TYPE testRunType, KieServices ks, KieModuleModel model, ReleaseId releaseId, KieFile... ruleFiles ) {
        KieBuilder kieBuilder = createKieBuilder(testRunType, ks, model, releaseId, ruleFiles );
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
        KieModuleModel model = KieServices.get().newKieModuleModel();
        model.setConfigurationProperty(AlphaNetworkCompilerOption.PROPERTY_NAME, AlphaNetworkCompilerOption.INMEMORY.toString());
        return model;
    }

    protected void assertReteIsAlphaNetworkCompiled(KieSession ksession) {
        Rete rete = ((InternalKnowledgeBase) ksession.getKieBase()).getRete();
        List<ObjectTypeNode> objectTypeNodes = ObjectTypeNodeCompiler.objectTypeNodes(rete);
        for(ObjectTypeNode otn : objectTypeNodes) {
            assertThat(otn.getObjectSinkPropagator() instanceof CompiledNetwork).isTrue();
        }
    }
}
