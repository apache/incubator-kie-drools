/*
 * Copyright 2015 JBoss Inc
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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLogger;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * This is a sample class to launch a rule.
 */
public class DateComparisonTest extends CommonTestMethodBase {

    @Test
    public void testDateComparisonThan() throws Exception {
        List<String> results = new ArrayList<String>();
        // load up the knowledge base
        String str = "";
        str += "package org.drools.compiler;\n";
        str += "dialect \"mvel\"\n";
        str += "global java.util.List results;\n";
        str += "rule \"test date greater than\"\n";
        str += "     when\n";
        str += "         $c : Cheese(type == \"Yesterday\")\n";
        str += "         Cheese(type == \"Tomorrow\",  usedBy > ($c.usedBy))\n";
        str += "     then\n";
        str += "         results.add( \"test date greater than\" );\n";
        str += "end\n";

        str += "rule \"test date less than\"\n";
        str += "    when\n";
        str += "        $c : Cheese(type == \"Tomorrow\")\n";
        str += "        Cheese(type == \"Yesterday\", usedBy < ($c.usedBy));\n";
        str += "    then\n";
        str += "        results.add( \"test date less than\" );\n";
        str += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.setGlobal( "results",
                            results );
        File testTmpDir = new File("target/test-tmp/");
        testTmpDir.mkdirs();
        KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession,
                "target/test-tmp/testDateComparisonThan");
        // go !
        Cheese yesterday = new Cheese( "Yesterday" );
        yesterday.setUsedBy( yesterday() );
        Cheese tomorrow = new Cheese( "Tomorrow" );
        tomorrow.setUsedBy( tomorrow() );
        ksession.insert( yesterday );
        ksession.insert( tomorrow );
        ksession.fireAllRules();
        logger.close();
        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( "test date greater than" ) );
        assertTrue( results.contains( "test date less than" ) );
    }

    private Date yesterday() {
        Calendar c = new GregorianCalendar();
        c.set( Calendar.DAY_OF_MONTH,
               c.get( Calendar.DAY_OF_MONTH ) - 1 );
        return c.getTime();
    }

    private Date tomorrow() {
        Calendar c = new GregorianCalendar();
        c.set( Calendar.DAY_OF_MONTH,
               c.get( Calendar.DAY_OF_MONTH ) + 1 );
        return c.getTime();
    }

}
