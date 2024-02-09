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

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class UseClassFieldsInRulesTest extends BaseModelTest {

    public UseClassFieldsInRulesTest( BaseModelTest.RUN_TYPE testRunType ) {
        super( testRunType );
    }

    public static class ClassWithFields {
        public final int field = 3;
        public static final int STATIC_FIELD = 3;

        public int getValue() {
            return field;
        }
    }

    @Test
    public void testUseAccessor() {
        doCheck(true, "value > 2");
    }

    @Test
    public void testUseField() {
        doCheck(true, "field > 2");
    }

    @Test
    public void testUseStaticField() {
        doCheck(true, "STATIC_FIELD > 2");
    }

    @Test
    public void testUseAccessorInFunction() {
        doCheck(true, "greaterThan( value, 2 )");
    }

    @Test
    public void testUseFieldInFunction() {
        doCheck(true, "greaterThan( field, 2 )");
    }

    @Test
    public void testUseStaticFieldInFunction() {
        doCheck(true, "greaterThan( STATIC_FIELD, 2 )");
    }

    public static boolean greaterThanMethod(int i1, int i2) { return i1 > i2; }

    @Test
    public void testUseAccessorInMethod() {
        doCheck(false, "greaterThanMethod( value, 2 )");
    }

    @Test
    public void testUseFieldInMethod() {
        doCheck(false, "greaterThanMethod( field, 2 )");
    }

    @Test
    public void testUseStaticFieldInMethod() {
        doCheck(false, "greaterThanMethod( STATIC_FIELD, 2 )");
    }

    private void doCheck(boolean useFunction, String pattern) {
        String str =
                "import " + ClassWithFields.class.getCanonicalName() + "\n" +
                (useFunction ?
                        "function boolean greaterThan(int i1, int i2) { return i1 > i2; }\n" :
                        "import static " + UseClassFieldsInRulesTest.class.getCanonicalName() + ".*\n" ) +
                "rule R when\n" +
                "    ClassWithFields( " + pattern + " )" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert(new ClassWithFields());
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testMethodInFrom() {
        String str =
                "import " + ClassWithFields.class.getCanonicalName() + "\n" +
                "import static " + UseClassFieldsInRulesTest.class.getCanonicalName() + ".*\n" +
                "rule R when\n" +
                "    Boolean (booleanValue == true) from greaterThanMethod( ClassWithFields.STATIC_FIELD, 2 )\n" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testMethodInEval() {
        String str =
                "import " + ClassWithFields.class.getCanonicalName() + "\n" +
                "import static " + UseClassFieldsInRulesTest.class.getCanonicalName() + ".*\n" +
                "rule R when\n" +
                "    eval( greaterThanMethod( ClassWithFields.STATIC_FIELD, 2 ) )\n" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testFunctionInFrom() {
        String str =
                "import " + ClassWithFields.class.getCanonicalName() + "\n" +
                "function boolean greaterThan(int i1, int i2) { return i1 > i2; }\n" +
                "rule R when\n" +
                "    Boolean (booleanValue == true) from greaterThan( ClassWithFields.STATIC_FIELD, 2 )\n" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testFunctionWithEval() {
        String str =
                "import " + ClassWithFields.class.getCanonicalName() + "\n" +
                "function boolean greaterThan(int i1, int i2) { return i1 > i2; }\n" +
                "rule R when\n" +
                "    eval( greaterThan( ClassWithFields.STATIC_FIELD, 2 ) )\n" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }
}
