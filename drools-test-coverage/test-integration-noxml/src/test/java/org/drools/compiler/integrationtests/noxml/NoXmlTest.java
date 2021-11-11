package org.drools.compiler.integrationtests.noxml;

import org.drools.modelcompiler.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;

public class NoXmlTest {

    @Test
    public void testKieHelperKieModuleModel() throws Exception {
        final String drl =
                    "rule R when\n" +
                    "    String()\n" +
                    "then\n" +
                    "end\n";

        KieModuleModel kModuleModel = KieServices.get().newKieModuleModel();

        KieHelper kHelper = new KieHelper();

        KieBase kieBase = kHelper
                .setKieModuleModel(kModuleModel)
                .addContent(drl, ResourceType.DRL)
                .build(ExecutableModelProject.class);

        KieSession kieSession = kieBase.newKieSession();
        kieSession.insert("test");
        assertEquals(1, kieSession.fireAllRules());
    }
}
