/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.drl;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialectConfiguration;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class RHSTest extends CommonTestMethodBase {

    @Test
    public void testGenericsInRHS() throws Exception {

        String rule = "";
        rule += "package org.drools.compiler;\n";
        rule += "import java.util.Map;\n";
        rule += "import java.util.HashMap;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "  when\n";
        rule += "  then\n";
        rule += "    Map<String,String> map = new HashMap<String,String>();\n";
        rule += "end";

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(rule));
        final KieSession session = createKnowledgeSession(kbase);
        assertNotNull(session);
    }

    @Test
    public void testRHSClone() {
        // JBRULES-3539
        final String str = "import java.util.Map;\n" +
                "dialect \"mvel\"\n" +
                "rule \"RHSClone\"\n" +
                "when\n" +
                "   Map($valOne : this['keyOne'] !=null)\n" +
                "then\n" +
                "   System.out.println( $valOne.clone() );\n" +
                "end\n";

        final KnowledgeBuilderConfigurationImpl pkgBuilderCfg = new KnowledgeBuilderConfigurationImpl();
        final MVELDialectConfiguration mvelConf = (MVELDialectConfiguration) pkgBuilderCfg.getDialectConfiguration("mvel");
        mvelConf.setStrict(false);
        mvelConf.setLangLevel(5);
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pkgBuilderCfg);
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        final KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (final KnowledgeBuilderError error : errors) {
                System.err.println(error);
            }
            fail("Could not parse knowledge");
        }
    }

    @Test
    public void testIncrementOperator() throws Exception {
        String str = "";
        str += "package org.drools.compiler \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "    $I : Integer() \n";
        str += "then \n";
        str += "    int i = $I.intValue(); \n";
        str += "    i += 5; \n";
        str += "    list.add( i ); \n";
        str += "end \n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert(5);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(10, list.get(0));
    }

    @Test
    public void testKnowledgeHelperFixerInStrings() {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  no-loop true ";
        str += "when \n";
        str += "  $fact : String() \n";
        str += "then \n";
        str += "  list.add(\"This is an update()\"); \n";
        str += "  list.add(\"This is an update($fact)\"); \n";
        str += "  update($fact); \n";
        str += "end  \n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert("hello");
        ksession.fireAllRules();

        ksession.dispose();

        assertEquals(2, list.size());
        assertEquals("This is an update()", list.get(0));
        assertEquals("This is an update($fact)", list.get(1));
    }
}
