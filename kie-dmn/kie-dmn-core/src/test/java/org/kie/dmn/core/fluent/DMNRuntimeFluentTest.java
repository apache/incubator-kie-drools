package org.kie.dmn.core.fluent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.RequestContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.core.util.KieHelper;
import org.kie.internal.builder.fluent.ExecutableBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DMNRuntimeFluentTest {

    @Test
    public void testFluentApi() {

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0");
        KieHelper.getKieContainer(releaseId,
                                  ks.getResources().newClassPathResource("org/kie/dmn/core/typecheck_in_DT.dmn", this.getClass()));

        ExecutableBuilder builder = ExecutableBuilder.create()
                .getKieContainer(releaseId)
                .newDMNRuntime()
                .setActiveModel("http://www.trisotech.com/definitions/_99ccd4df-41ac-43c3-a563-d58f43149829", "typecheck in DT")
                .setInput("a number", 0)
                .evaluateModel()
                .out("dmnResult")
                .getAllContext()
                .out("result")
                .getMessages()
                .out("messages")
                .end();

        RequestContext requestContext = ExecutableRunner.create().execute(builder.getExecutable());

        Map<String, Object> resultMap = requestContext.getOutput("result");
        DMNResult dmnResult = requestContext.getOutput("dmnResult");
        List<DMNMessage> messages = requestContext.getOutput("messages");

        assertEquals(47, ((BigDecimal) resultMap.get("an odd decision")).intValue());
        assertNotNull(dmnResult);
        assertEquals(0, messages.size());
    }
}