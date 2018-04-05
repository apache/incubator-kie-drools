package org.drools.compiler.reteoo.compiled;

import org.drools.modelcompiler.BaseModelTest;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.*;

public class ObjectTypeNodeCompilerTest extends BaseModelTest {

    public ObjectTypeNodeCompilerTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testAlphaConstraint() {
        String str =
                "rule \"Bind\"\n" +
                        "when\n" +
                        "  $s : String( length > 4, length < 10)\n" +
                        "then\n" +
                        "end";

        KieModuleModel kproj = getKieModuleModelWithAlphaNetworkCompiler();
        KieSession ksession = getKieSession(kproj, str );

        ksession.insert("Luca");
        ksession.insert("Asdrubale");

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAlphaConstraintsPerson() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"Bind1\"\n" +
                        "when\n" +
                        "  $s : Person( name == \"Luca\") \n" +
                        "then\n" +
                        "end\n"+
                        "rule \"Bind2\"\n" +
                        "when\n" +
                        "  $s : Person( name == \"Mario\") \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind3\"\n" +
                        "when\n" +
                        "  $s : Person( name == \"Matteo\") \n" +
                        "then\n" +
                        "end\n";

        KieModuleModel kproj = getKieModuleModelWithAlphaNetworkCompiler();
        KieSession ksession = getKieSession(kproj, str );

        ksession.insert(new Person("Luca"));
        ksession.insert(new Person("Asdrubale"));

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAlphaConstraint2() {
        String str =
                "rule \"Bind1\"\n" +
                        "when\n" +
                        "  $s : String( length == 4) \n" +
                        "then\n" +
                        "end\n"+
                        "rule \"Bind2\"\n" +
                        "when\n" +
                        "  $s : String( length == 5) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind3\"\n" +
                        "when\n" +
                        "  $s : String( length == 6) \n" +
                        "then\n" +
                        "end\n";

        KieModuleModel kproj = getKieModuleModelWithAlphaNetworkCompiler();
        KieSession ksession = getKieSession(kproj, str );

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

        KieModuleModel kproj = getKieModuleModelWithAlphaNetworkCompiler();
        KieSession ksession = getKieSession(kproj, str );

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
                        "   modify($p) { setName($p.getName() + \"30\"); }" +
                        "end";

        KieModuleModel kproj = getKieModuleModelWithAlphaNetworkCompiler();
        KieSession ksession = getKieSession(kproj, str );

        final Person luca = new Person("Luca", 30);
        ksession.insert(luca);

        assertEquals(1, ksession.fireAllRules());

        ksession.fireAllRules();
        assertEquals("Luca30", luca.getName());
    }


    @Test
    public void testModify2() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"Modify\"\n" +
                        "when\n" +
                        "  $p : Person( age < 40 )\n" +
                        "then\n" +
                        "   modify($p) { setAge($p.getAge() + 1); }" +
                        "end";

        KieModuleModel kproj = getKieModuleModelWithAlphaNetworkCompiler();
        KieSession ksession = getKieSession(kproj, str );

        final Person luca = new Person("Luca", 30);
        ksession.insert(luca);

        Result result = new Result();
        ksession.insert(result);

        assertEquals(10, ksession.fireAllRules());

        ksession.fireAllRules();
        assertTrue(luca.getAge() == 40);
    }


    private KieModuleModel getKieModuleModelWithAlphaNetworkCompiler() {
        KieModuleModel kproj = KieServices.get().newKieModuleModel();
        kproj.setConfigurationProperty( "drools.alphaNetworkCompiler", "true" );
        return kproj;
    }
}