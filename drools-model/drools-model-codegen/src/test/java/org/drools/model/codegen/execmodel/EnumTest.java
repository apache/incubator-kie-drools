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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumTest extends BaseModelTest {

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMatchEnum(RUN_TYPE runType) {
        String str =
                "import " + Bus.class.getCanonicalName() + ";" +
                "rule bus2 when\n" +
                "       $bus : Bus( $maker : maker == Bus.Maker.HINO )\n" +
                "   then\n" +
                "       System.out.println(\"bus=\" + $bus + \", maker=\" + $maker);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str );

        Bus a = new Bus("blue", 25, Bus.Maker.HINO);
        ksession.insert(a);
        Bus b = new Bus("red", 25, Bus.Maker.ISUZU);
        ksession.insert(b);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBindEnum(RUN_TYPE runType) {
        // DROOLS-5851
        String str =
                "import " + Bus.class.getCanonicalName() + ";" +
                "rule bus2 when\n" +
                "       $bus : Bus( $maker : Bus.Maker.HINO )\n" +
                "   then\n" +
                "       System.out.println(\"bus=\" + $bus + \", maker=\" + $maker);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Bus a = new Bus("blue", 25, Bus.Maker.HINO);
        ksession.insert(a);
        Bus b = new Bus("red", 25, Bus.Maker.ISUZU);
        ksession.insert(b);

        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    public static class Bus {
        private String name;
        private int person;
        private Maker maker;

        public enum Maker {
            FUSO,
            HINO,
            ISUZU
        }

        public Bus(String name, int person) {
            this.name = name;
            this.person = person;
        }

        public Bus(String name, int person, Maker maker) {
            this.name = name;
            this.person = person;
            this.maker = maker;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPerson() {
            return person;
        }

        public void setPerson(int person) {
            this.person = person;
        }

        public Maker getMaker() {
            return maker;
        }

        public void setMaker(Maker maker) {
            this.maker = maker;
        }
    }
}
