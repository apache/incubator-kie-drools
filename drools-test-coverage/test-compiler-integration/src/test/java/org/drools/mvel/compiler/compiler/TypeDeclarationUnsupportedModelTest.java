/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.compiler;

import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeDeclarationUnsupportedModelTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(false).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testTraitExtendPojo(KieBaseTestConfiguration kieBaseTestConfiguration) {
        //DROOLS-697
        final String s1 = "package test;\n" +

                "declare Poojo " +
                "end " +

                "declare trait Mask extends Poojo " +
                "end " +
                "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, s1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.size()).isEqualTo(1);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testRedeclareWithInterfaceExtensionAndOverride(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String s1 = "package test;\n" +

                "declare trait " + TypeDeclarationTest.Ext.class.getCanonicalName() + " extends " + TypeDeclarationTest.Base.class.getCanonicalName() + " " +
                " fld : String " +
                "end " +

                "declare trait " + TypeDeclarationTest.Base.class.getCanonicalName() + " " +
                "end " +
                "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, s1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testDeclaresInForeignPackages(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str1 = "" +
                "package org.drools \n" +
                "declare foreign.ClassC fld : foreign.ClassD end " +
                "declare foreign.ClassD end " +
                "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }


    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testTypeReDeclarationPojo(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testTypeReDeclarationPojoMoreFields(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testTypeReDeclarationPojoLessFields(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str1 = "" +
                "package org.drools \n" +
                "import " + TypeDeclarationTest.class.getName() + ".ClassC; \n" +
                "" +
                "declare " + TypeDeclarationTest.class.getName() + ".ClassC \n" +
                "    name : String \n" +
                "end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }
}
