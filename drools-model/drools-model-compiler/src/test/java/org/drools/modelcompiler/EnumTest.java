/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
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

package org.drools.modelcompiler;

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class EnumTest extends BaseModelTest {

    public EnumTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testMatchEnum() {
        String str =
                "import " + Bus.class.getCanonicalName() + ";" +
                "rule bus2 when\n" +
                "       $bus : Bus( $maker : maker == Bus.Maker.HINO )\n" +
                "   then\n" +
                "       System.out.println(\"bus=\" + $bus + \", maker=\" + $maker);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Bus a = new Bus("blue", 25, Bus.Maker.HINO);
        ksession.insert(a);
        Bus b = new Bus("red", 25, Bus.Maker.ISUZU);
        ksession.insert(b);

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testBindEnum() {
        // DROOLS-5851
        String str =
                "import " + Bus.class.getCanonicalName() + ";" +
                "rule bus2 when\n" +
                "       $bus : Bus( $maker : Bus.Maker.HINO )\n" +
                "   then\n" +
                "       System.out.println(\"bus=\" + $bus + \", maker=\" + $maker);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Bus a = new Bus("blue", 25, Bus.Maker.HINO);
        ksession.insert(a);
        Bus b = new Bus("red", 25, Bus.Maker.ISUZU);
        ksession.insert(b);

        assertEquals(2, ksession.fireAllRules());
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
