/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class NodeHashingTest {

    @Test
    public void testNodeHashTypeMismatch() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( status == 1 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( status == 2 )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession1 = new KieHelper().addContent( drl1, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p1 = new Person();
        p1.setStatus( "1" );
        ksession1.insert( p1 );

        assertEquals( 1, ksession1.fireAllRules() );
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( status == 1 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( status == 2 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( status == 3 )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession2 = new KieHelper().addContent( drl2, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p2 = new Person();
        p2.setStatus( "1" );
        ksession2.insert( p2 );

        assertEquals( 1, ksession2.fireAllRules() );
        ksession2.dispose();
    }

    @Test
    public void testNodeHashTypeMismatchWithBigInteger() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigInteger == \"1\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigInteger == \"2\" )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession1 = new KieHelper().addContent( drl1, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p1 = new Person();
        p1.setBigInteger( new BigInteger( "1" ) );
        ksession1.insert( p1 );

        assertEquals( 1, ksession1.fireAllRules() );
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigInteger == \"1\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigInteger == \"2\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( bigInteger == \"3\" )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession2 = new KieHelper().addContent( drl2, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p2 = new Person();
        p2.setBigInteger( new BigInteger( "1" ) );
        ksession2.insert( p2 );

        assertEquals( 1, ksession2.fireAllRules() );
        ksession2.dispose();
    }

    @Test
    public void testNodeHashTypeMismatchWithBigDecimal() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == \"1.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == \"2.00\" )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession1 = new KieHelper().addContent( drl1, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p1 = new Person();
        p1.setBigDecimal( new BigDecimal( "1.00" ) );
        ksession1.insert( p1 );

        assertEquals( 1, ksession1.fireAllRules() );
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == \"1.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == \"2.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == \"3.00\" )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession2 = new KieHelper().addContent( drl2, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p2 = new Person();
        p2.setBigDecimal( new BigDecimal( "1.00" ) );
        ksession2.insert( p2 );

        assertEquals( 1, ksession2.fireAllRules() );
        ksession2.dispose();
    }

    @Test
    public void testNodeHashTypeMismatchFromBigDecimal() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( age == new BigDecimal( 1 ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( age == new BigDecimal( 2 ) )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession1 = new KieHelper().addContent( drl1, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p1 = new Person();
        p1.setAge( 1 );
        ksession1.insert( p1 );

        assertEquals( 1, ksession1.fireAllRules() );
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( age == new BigDecimal( 1 ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( age == new BigDecimal( 2 ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( age == new BigDecimal( 3 ) )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession2 = new KieHelper().addContent( drl2, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p2 = new Person();
        p2.setAge( 1 );
        ksession2.insert( p2 );

        assertEquals( 1, ksession2.fireAllRules() );
        ksession2.dispose();
    }

    @Test
    public void testNodeHashTypeMismatchWithPrimitiveDouble() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( age == 1.0 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( age == 2.0 )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession1 = new KieHelper().addContent( drl1, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p1 = new Person();
        p1.setAge( 1 );
        ksession1.insert( p1 );

        assertEquals( 1, ksession1.fireAllRules() );
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( age == 1.0 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( age == 2.0 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( age == 3.0 )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession2 = new KieHelper().addContent( drl2, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p2 = new Person();
        p2.setAge( 1 );
        ksession2.insert( p2 );

        assertEquals( 1, ksession2.fireAllRules() );
        ksession2.dispose();
    }

    @Test
    public void testNodeHashTypeMismatchWithBigIntegerAndDecimal() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigInteger.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == new BigInteger( \"1\" ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == new BigInteger( \"2\" ) )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession1 = new KieHelper().addContent( drl1, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p1 = new Person();
        p1.setBigDecimal( new BigDecimal( 1 ) );
        ksession1.insert( p1 );

        assertEquals( 1, ksession1.fireAllRules() );
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigInteger.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == new BigInteger( \"1\" ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == new BigInteger( \"2\" ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == new BigInteger( \"3\" ) )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession2 = new KieHelper().addContent( drl2, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        Person p2 = new Person();
        p2.setBigDecimal( new BigDecimal( 1 ) );
        ksession2.insert( p2 );

        assertEquals( 1, ksession2.fireAllRules() );
        ksession2.dispose();
    }

    public static class DoubleValue {
        private final Double value;

        public DoubleValue( Double value ) {
            this.value = value;
        }

        public Double getValue() {
            return value;
        }
    }

    @Test
    public void testNodeHashTypeMismatchWithDouble() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + DoubleValue.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    DoubleValue( value == \"1.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    DoubleValue( value == \"2.00\" )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession1 = new KieHelper().addContent( drl1, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        ksession1.insert( new DoubleValue(1.00) );

        assertEquals( 1, ksession1.fireAllRules() );
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + DoubleValue.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    DoubleValue( value == \"1.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    DoubleValue( value == \"2.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    DoubleValue( value == \"3.00\" )\n" +
                      "then\n" +
                      "end\n";

        KieSession ksession2 = new KieHelper().addContent( drl2, ResourceType.DRL )
                                              .build()
                                              .newKieSession();

        ksession2.insert( new DoubleValue(1.00) );

        assertEquals( 1, ksession2.fireAllRules() );
        ksession2.dispose();
    }
}
