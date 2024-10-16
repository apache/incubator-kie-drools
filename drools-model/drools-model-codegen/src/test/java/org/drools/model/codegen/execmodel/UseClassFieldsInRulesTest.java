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
package org.drools.model.codegen.execmodel;

import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class UseClassFieldsInRulesTest extends BaseModelTest {

    public static class ClassWithFields {
        public final int field = 3;
        public static final int STATIC_FIELD = 3;

        public int getValue() {
            return field;
        }
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testUseAccessor(RUN_TYPE runType) {
        doCheck(runType, true, "value > 2");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testUseField(RUN_TYPE runType) {
        doCheck(runType, true, "field > 2");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testUseStaticField(RUN_TYPE runType) {
        doCheck(runType, true, "STATIC_FIELD > 2");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testUseAccessorInFunction(RUN_TYPE runType) {
        doCheck(runType, true, "greaterThan( value, 2 )");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testUseFieldInFunction(RUN_TYPE runType) {
        doCheck(runType, true, "greaterThan( field, 2 )");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testUseStaticFieldInFunction(RUN_TYPE runType) {
        doCheck(runType, true, "greaterThan( STATIC_FIELD, 2 )");
    }

    public static boolean greaterThanMethod(int i1, int i2) { return i1 > i2; }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testUseAccessorInMethod(RUN_TYPE runType) {
        doCheck(runType, false, "greaterThanMethod( value, 2 )");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testUseFieldInMethod(RUN_TYPE runType) {
        doCheck(runType, false, "greaterThanMethod( field, 2 )");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testUseStaticFieldInMethod(RUN_TYPE runType) {
        doCheck(runType, false, "greaterThanMethod( STATIC_FIELD, 2 )");
    }

    private void doCheck(RUN_TYPE runType, boolean useFunction, String pattern) {
        String str =
                "import " + ClassWithFields.class.getCanonicalName() + "\n" +
                (useFunction ?
                        "function boolean greaterThan(int i1, int i2) { return i1 > i2; }\n" :
                        "import static " + UseClassFieldsInRulesTest.class.getCanonicalName() + ".*\n" ) +
                "rule R when\n" +
                "    ClassWithFields( " + pattern + " )" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new ClassWithFields());
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMethodInFrom(RUN_TYPE runType) {
        String str =
                "import " + ClassWithFields.class.getCanonicalName() + "\n" +
                "import static " + UseClassFieldsInRulesTest.class.getCanonicalName() + ".*\n" +
                "rule R when\n" +
                "    Boolean (booleanValue == true) from greaterThanMethod( ClassWithFields.STATIC_FIELD, 2 )\n" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(runType, str);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMethodInEval(RUN_TYPE runType) {
        String str =
                "import " + ClassWithFields.class.getCanonicalName() + "\n" +
                "import static " + UseClassFieldsInRulesTest.class.getCanonicalName() + ".*\n" +
                "rule R when\n" +
                "    eval( greaterThanMethod( ClassWithFields.STATIC_FIELD, 2 ) )\n" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(runType, str);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testFunctionInFrom(RUN_TYPE runType) {
        String str =
                "import " + ClassWithFields.class.getCanonicalName() + "\n" +
                "function boolean greaterThan(int i1, int i2) { return i1 > i2; }\n" +
                "rule R when\n" +
                "    Boolean (booleanValue == true) from greaterThan( ClassWithFields.STATIC_FIELD, 2 )\n" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(runType, str);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testFunctionWithEval(RUN_TYPE runType) {
        String str =
                "import " + ClassWithFields.class.getCanonicalName() + "\n" +
                "function boolean greaterThan(int i1, int i2) { return i1 > i2; }\n" +
                "rule R when\n" +
                "    eval( greaterThan( ClassWithFields.STATIC_FIELD, 2 ) )\n" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(runType, str);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }
}
