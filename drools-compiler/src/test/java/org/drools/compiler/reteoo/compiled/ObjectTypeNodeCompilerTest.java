package org.drools.compiler.reteoo.compiled;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.Result;
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

    @Test
    public void testAlphaConstraintWithModification() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "rule \"Bind\"\n" +
                        "when\n" +
                        "  $r : Result()\n" +
                        "  $s : String( length > 4, length < 10)\n" +
                        "then\n" +
                        "  $r.setValue($s + \" is greater than 4 and smaller than 10\");\n" +
                        "end";

        KieBaseConfiguration configuration = KieServices.Factory.get().newKieBaseConfiguration();

        configuration.setProperty(AlphaNetworkCompilerOption.PROPERTY_NAME, String.valueOf(Boolean.TRUE));

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build(configuration)
                .newKieSession();

        ksession.insert("Luca");
        ksession.insert("Asdrubale");

        Result result = new Result();
        ksession.insert(result);

        assertEquals(1, ksession.fireAllRules());

        ksession.fireAllRules();
        assertEquals("Asdrubale is greater than 4 and smaller than 10", result.getValue());
    }

    @Test
    public void testModify() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"Modify\"\n" +
                        "when\n" +
                        "  $p : Person( age == 30 )\n" +
                        "then\n" +
                        "   modify($p) { setHappy(true); }" +
                        "end";

        KieBaseConfiguration configuration = KieServices.Factory.get().newKieBaseConfiguration();

        configuration.setProperty(AlphaNetworkCompilerOption.PROPERTY_NAME, String.valueOf(Boolean.TRUE));

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build(configuration)
                .newKieSession();

        final Person luca = new Person("Luca", 30, false);
        ksession.insert(luca);

        Result result = new Result();
        ksession.insert(result);

        assertEquals(1, ksession.fireAllRules());

        ksession.fireAllRules();
        assertTrue(luca.isHappy());
    }
}