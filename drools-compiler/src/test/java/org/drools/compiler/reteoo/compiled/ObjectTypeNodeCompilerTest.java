package org.drools.compiler.reteoo.compiled;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.AlphaNetworkCompilerOption;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.*;

public class ObjectTypeNodeCompilerTest extends CommonTestMethodBase {

    @Test
    public void testAlphaConstraint() {
        String str =
                "rule \"Bind\"\n" +
                        "when\n" +
                        "  $s : String( length > 4, length < 10)\n" +
                        "then\n" +
                        "end";

        KieBaseConfiguration configuration = KieServices.Factory.get().newKieBaseConfiguration();

        configuration.setProperty(AlphaNetworkCompilerOption.PROPERTY_NAME, String.valueOf(Boolean.TRUE));

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build(configuration)
                .newKieSession();

        ksession.insert("Luca");
        ksession.insert("Asdrubale");

        assertEquals(1, ksession.fireAllRules());
    }
}