/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.drools.compiler.integrationtests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

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

    @After
    public void cleanUp() {
        System.clearProperty("drools.dialect.java.compiler.lnglevel");
    }

    @Test
    public void testCompileSwitchOverStringWithLngLevel17() {
        double javaVersion = Double.valueOf(System.getProperty("java.specification.version"));
        Assume.assumeTrue("Test only makes sense on Java 7+.", javaVersion >= 1.7);
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.7");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(FUNCTION_WITH_SWITCH_OVER_STRING.getBytes()), ResourceType.DRL);
        Assert.assertFalse("Compilation error(s) occurred!", kbuilder.hasErrors());
    }

    @Test
    public void testShouldFailToCompileSwitchOverStringWithLngLevel16() {
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.6");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(FUNCTION_WITH_SWITCH_OVER_STRING.getBytes()), ResourceType.DRL);
        Assert.assertTrue("Compilation error(s) expected!", kbuilder.hasErrors());
    }

}
