package org.kie.dmn.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.impl.DMNKnowledgeBuilderError;
import org.kie.internal.builder.InternalKieBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DMNMessagesAPITest {

    @Test
    public void testAPIUsage() {
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                          ks.getResources().newClassPathResource("incomplete_expression.dmn", this.getClass()),
                                                          ks.getResources().newClassPathResource("duff.drl", this.getClass())
                                                          );

        Results verify = kieContainer.verify();
        List<Message> kie_messages = verify.getMessages();
        kie_messages.forEach(System.out::println);
        assertThat(kie_messages.size(), is(3));

        List<DMNKnowledgeBuilderError> dmnKnowledgeBuilderErrors = new ArrayList<>();
        for (Message m : kie_messages) {
            if (m.getMetadata() instanceof DMNKnowledgeBuilderError) {
                dmnKnowledgeBuilderErrors.add((DMNKnowledgeBuilderError) m.getMetadata());
            }
        }
        assertThat(dmnKnowledgeBuilderErrors.size(), is(1));

        DMNMessage dmnMessage = dmnKnowledgeBuilderErrors.get(0).getDmnMessage();
        assertThat(dmnMessage.getSourceId(), is("_c990c3b2-e322-4ef9-931d-79bcdac99686"));
        assertThat(dmnMessage.getMessageType(), is(DMNMessageType.ERR_COMPILING_FEEL));
    }

    public static KieContainer getKieContainer(ReleaseId releaseId,
                                               Resource... resources) {
        KieServices ks = KieServices.Factory.get();
        createAndDeployJar(ks, releaseId, resources);
        return ks.newKieContainer(releaseId);
    }

    public static KieModule createAndDeployJar(KieServices ks,
                                               ReleaseId releaseId,
                                               Resource... resources) {
        byte[] jar = createJar(ks, releaseId, resources);

        KieModule km = deployJarIntoRepository(ks, jar);
        return km;
    }

    public static byte[] createJar(KieServices ks, ReleaseId releaseId, Resource... resources) {
        KieFileSystem kfs = ks.newKieFileSystem().generateAndWritePomXML(releaseId);
        for (int i = 0; i < resources.length; i++) {
            if (resources[i] != null) {
                kfs.write(resources[i]);
            }
        }
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        ((InternalKieBuilder) kieBuilder).buildAll(o -> true);

        InternalKieModule kieModule = (InternalKieModule) ((InternalKieBuilder) kieBuilder).getKieModuleIgnoringErrors();

        byte[] jar = kieModule.getBytes();
        return jar;
    }

    private static KieModule deployJarIntoRepository(KieServices ks, byte[] jar) {
        Resource jarRes = ks.getResources().newByteArrayResource(jar);
        KieModule km = ks.getRepository().addKieModule(jarRes);
        return km;
    }
}
