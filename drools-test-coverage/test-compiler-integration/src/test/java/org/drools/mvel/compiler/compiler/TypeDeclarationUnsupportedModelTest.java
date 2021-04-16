/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.compiler.compiler;

import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TypeDeclarationUnsupportedModelTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public TypeDeclarationUnsupportedModelTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test()
    public void testTraitExtendPojo() {
        //DROOLS-697
        final String s1 = "package test;\n" +

                "declare Poojo " +
                "end " +

                "declare trait Mask extends Poojo " +
                "end " +
                "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, s1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertEquals(1, errors.size());
    }

    @Test
    public void testRedeclareWithInterfaceExtensionAndOverride() {
        final String s1 = "package test;\n" +

                "declare trait " + TypeDeclarationTest.Ext.class.getCanonicalName() + " extends " + TypeDeclarationTest.Base.class.getCanonicalName() + " " +
                " fld : String " +
                "end " +

                "declare trait " + TypeDeclarationTest.Base.class.getCanonicalName() + " " +
                "end " +
                "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, s1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    @Test
    public void testDeclaresInForeignPackages() {
        String str1 = "" +
                "package org.drools \n" +
                "declare foreign.ClassC fld : foreign.ClassD end " +
                "declare foreign.ClassD end " +
                "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());
    }


    @Test
    public void testTypeReDeclarationPojo() {
        String str1 = "" +
                "package org.drools \n" +
                "import " + TypeDeclarationTest.class.getName() + ".ClassC; \n" +
                "" +
                "declare " + TypeDeclarationTest.class.getName() + ".ClassC \n" +
                "    name : String \n" +
                "    age : Integer \n" +
                "end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    @Test
    public void testTypeReDeclarationPojoMoreFields() {
        String str1 = "" +
                "package org.drools \n" +
                "import " + TypeDeclarationTest.class.getName() + ".ClassC; \n" +
                "" +
                "declare " + TypeDeclarationTest.class.getName() + ".ClassC \n" +
                "    name : String \n" +
                "    age : Integer \n" +
                "    address : Objet \n" +
                "end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertFalse("Should have an error", errors.isEmpty());
    }

    @Test
    public void testTypeReDeclarationPojoLessFields() {
        String str1 = "" +
                "package org.drools \n" +
                "import " + TypeDeclarationTest.class.getName() + ".ClassC; \n" +
                "" +
                "declare " + TypeDeclarationTest.class.getName() + ".ClassC \n" +
                "    name : String \n" +
                "end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertFalse("Should have an error", errors.isEmpty());
    }
}
