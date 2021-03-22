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
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.ancompiler.CompiledNetwork;
import org.drools.ancompiler.ObjectTypeNodeCompiler;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.ObjectSinkPropagator;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.util.IoUtils;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.drools.modelcompiler.CanonicalKieModule.getANCFile;
import static org.drools.modelcompiler.CanonicalKieModule.getANCFileOld;
import static org.drools.modelcompiler.CanonicalKieModule.getModelFileWithGAV;
import static org.drools.modelcompiler.CanonicalKieModule.getModelFileWithGAVOld;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class CanonicalKieModuleTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public CanonicalKieModuleTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN});
        return parameters;
    }

    @Test
    public void testModelFile() throws IOException {
        // DROOLS-6175
        // No need to test with CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK because it will do ANC in-memory without adding "alpha-network-compiler" file
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "end";

        ReleaseId releaseId = KieServices.get().newReleaseId("org.kie", "kjar-test", "1.0.0");
        KieModule kieModule = KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, str);
        assertTrue(kieModule instanceof CanonicalKieModule);
        CanonicalKieModule canonicalKieModule = (CanonicalKieModule) kieModule;
        InternalKieModule internalKieModule = canonicalKieModule.getInternalKieModule();

        String modelFileName = getModelFileWithGAV(internalKieModule.getReleaseId());
        assertTrue(modelFileName.startsWith("META-INF/kie/org/kie/kjar-test"));
        assertTrue(internalKieModule.isAvailable(modelFileName));

        Resource modelFile = internalKieModule.getResource(modelFileName);
        String content = new String(IoUtils.readBytesFromInputStream(modelFile.getInputStream()));

        assertTrue(content.contains("Drools-Model-Version"));
        assertTrue(content.contains("defaultpkg.Rules"));
    }

    @Test
    public void testModelFileBackwardCompatibility() throws IOException {
        // DROOLS-6175
        // No need to test with CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK because this test detects "alpha-network-compiler" from existing kjar
        final KieServices kieServices = KieServices.get();
        ((KieServicesImpl) kieServices).nullAllContainerIds();
        try {
            URL kjar = getClass().getClassLoader().getResource("org/example/kjar-em-anc-before-drools6175-1.0.0.jar"); // built by 7.51.0.Final
            Resource resource = kieServices.getResources().newUrlResource(kjar);
            kieServices.getRepository().addKieModule(resource);
            ReleaseId releaseId = kieServices.newReleaseId("org.example", "kjar-em-anc-before-drools6175", "1.0.0");
            KieContainer kieContainer = kieServices.newKieContainer(releaseId);
            KieModule kieModule = ((KieContainerImpl) kieContainer).getKieModuleForKBase("defaultKieBase");
            assertTrue(kieModule instanceof CanonicalKieModule);
            CanonicalKieModule canonicalKieModule = (CanonicalKieModule) kieModule;
            InternalKieModule internalKieModule = canonicalKieModule.getInternalKieModule();

            String modelFileName = getModelFileWithGAV(internalKieModule.getReleaseId());
            assertTrue(modelFileName.startsWith("META-INF/kie/org/example/kjar-em-anc-before-drools6175")); // slash
            assertFalse(internalKieModule.isAvailable(modelFileName));

            String oldModelFileName = getModelFileWithGAVOld(internalKieModule.getReleaseId());
            assertTrue(oldModelFileName.startsWith("META-INF/kie/org.example/kjar-em-anc-before-drools6175")); // dot
            assertTrue(internalKieModule.isAvailable(oldModelFileName));

            Resource modelFile = internalKieModule.getResource(oldModelFileName);
            String content = new String(IoUtils.readBytesFromInputStream(modelFile.getInputStream()));

            assertTrue(content.contains("Drools-Model-Version"));
            assertTrue(content.contains("org.example.Rules"));

            String ancFileName = getANCFile(internalKieModule.getReleaseId());
            assertTrue(ancFileName.startsWith("META-INF/kie/org/example/kjar-em-anc-before-drools6175")); // slash
            assertFalse(internalKieModule.isAvailable(ancFileName));

            String oldAncFileName = getANCFileOld(internalKieModule.getReleaseId());
            assertTrue(oldAncFileName.startsWith("META-INF/kie/org.example/kjar-em-anc-before-drools6175")); // dot
            assertTrue(internalKieModule.isAvailable(oldAncFileName));

            KieSession ksession = kieContainer.newKieSession();
            ksession.insert(Integer.valueOf(20));
            assertEquals(1, ksession.fireAllRules());

            assertReteIsAlphaNetworkCompiled(ksession);

        } finally {
            ((KieServicesImpl) kieServices).nullAllContainerIds();
        }
    }

    protected void assertReteIsAlphaNetworkCompiled(KieSession ksession) {
        Rete rete = ((InternalKnowledgeBase) ksession.getKieBase()).getRete();
        List<ObjectTypeNode> objectTypeNodes = ObjectTypeNodeCompiler.objectTypeNodes(rete);
        for (ObjectTypeNode otn : objectTypeNodes) {
            ObjectSinkPropagator objectSinkPropagator = otn.getObjectSinkPropagator();
            System.out.println(objectSinkPropagator.getClass().getCanonicalName());
            assertTrue(objectSinkPropagator instanceof CompiledNetwork);
        }
    }
}
