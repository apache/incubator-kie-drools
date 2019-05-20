package org.drools.compiler.integrationtests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SwitchOverStringTest {

    private static final String FUNCTION_WITH_SWITCH_OVER_STRING = "function void theTest(String input) {\n" +
            "  switch(input) {\n" +
            "    case \"Hello World\" :" +
            "      System.out.println(\"yep\");\n" +
            "      break;\n" +
            "    default :\n" +
            "      System.out.println(\"uh\");\n" +
            "      break;\n" +
            "  }\n" +
            "}";

    @AfterEach
    public void cleanUp() {
        System.clearProperty("drools.dialect.java.compiler.lnglevel");
    }

    @Test
    public void testCompileSwitchOverStringWithLngLevel17() {
        double javaVersion = Double.valueOf(System.getProperty("java.specification.version"));
        Assumptions.assumeTrue(javaVersion >= 1.7);
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.7");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(FUNCTION_WITH_SWITCH_OVER_STRING.getBytes()), ResourceType.DRL);
        assertFalse(kbuilder.hasErrors(), "Compilation error(s) occurred!");
    }

    @Test
    public void testShouldFailToCompileSwitchOverStringWithLngLevel16() {
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.6");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(FUNCTION_WITH_SWITCH_OVER_STRING.getBytes()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors(), "Compilation error(s) expected!");
    }

}
