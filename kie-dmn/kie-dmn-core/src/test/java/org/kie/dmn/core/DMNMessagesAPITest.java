package org.kie.dmn.core;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.util.DMNRuntimeUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DMNMessagesAPITest {

    @Test
    public void testAPIUsage() {
        // DROOLS-3335 Broken DMN resource should inhibit KJAR and report KieBuilder message
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = DMNRuntimeUtil.getKieContainerIgnoringErrors(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                                       ks.getResources().newClassPathResource("incomplete_expression.dmn", this.getClass()),
                                                                                       ks.getResources().newClassPathResource("duff.drl", this.getClass()));

        Results verify = kieContainer.verify();
        List<Message> kie_messages = verify.getMessages();
        kie_messages.forEach(System.out::println);
        assertThat(kie_messages.size(), is(3));
        assertThat(kie_messages.stream().filter(m -> m.getPath().equals("duff.drl")).count(), is(2L));

        List<DMNMessage> dmnMessages = kie_messages.stream()
                                                   .filter(DMNMessage.class::isInstance)
                                                   .map(DMNMessage.class::cast)
                                                   .collect(Collectors.toList());
        assertThat(dmnMessages.size(), is(1));

        DMNMessage dmnMessage = dmnMessages.get(0);
        assertThat(dmnMessage.getSourceId(), is("_c990c3b2-e322-4ef9-931d-79bcdac99686"));
        assertThat(dmnMessage.getMessageType(), is(DMNMessageType.ERR_COMPILING_FEEL));
        assertThat(dmnMessage.getPath(), is("incomplete_expression.dmn"));
    }
}
