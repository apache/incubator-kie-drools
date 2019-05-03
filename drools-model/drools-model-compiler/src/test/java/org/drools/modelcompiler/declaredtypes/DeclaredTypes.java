package org.drools.modelcompiler.declaredtypes;

import org.drools.modelcompiler.CompilerTest;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

public class DeclaredTypes extends CompilerTest {

    public DeclaredTypes(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testDeclaredType() throws Exception {
        String str =
                        "package org.example\n" +
                        "import java.util.*\n" +
                        "import org.example.*\n" +
                        "\n" +
                        "declare FactB\n" +
                        "    factA: org.example.FactA\n" +
                        "    propB : String\n" +
                        "end" +
                        "\n" +
                        "\n" +
                        "rule \"example\"\n" +
                        "when\n" +
                        "    $a: FactA()\n" +
                        "then\n" +
                        "    System.out.println( \"fact A \");\n" +
                        "end";

        KieSession ksession = getKieSession(str);


        assertEquals(0, ksession.fireAllRules());
    }

}
