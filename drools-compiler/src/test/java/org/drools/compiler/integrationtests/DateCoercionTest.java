/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.drools.compiler.integrationtests;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DateCoercionTest {

    @Test
    public void testDateCoercionWithOr() {
        // DROOLS-296
        String drl = "import java.util.Date\n" +
                     "global java.util.List list\n" +
                     "declare DateContainer\n" +
                     "     date: Date\n" +
                     "end\n" +
                     "\n" +
                     "rule Init when\n" +
                     "then\n" +
                     "    insert(new DateContainer(new Date(0)));" +
                     "end\n" +
                     "\n" +
                     "rule \"Test rule\"\n" +
                     "when\n" +
                     "    $container: DateContainer( date >= \"15-Oct-2013\" || date <= \"01-Oct-2013\" )\n" +
                     "then\n" +
                     "    list.add(\"working\");\n" +
                     "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "working", list.get( 0 ) );
    }

    @Test
    public void testDateCoercionWithVariable() {
        // DROOLS-970
        String drl = "import java.util.Date\n" +
                     "global java.util.List list\n" +
                     "declare DateContainer\n" +
                     "     date: Date\n" +
                     "end\n" +
                     "\n" +
                     "rule Init when\n" +
                     "then\n" +
                     "    insert(new DateContainer(new Date(0)));" +
                     "end\n" +
                     "\n" +
                     "rule \"Test rule\"\n" +
                     "when\n" +
                     "    $container: DateContainer( $date: date, $date <= \"01-Oct-2013\" )\n" +
                     "then\n" +
                     "    list.add(\"working\");\n" +
                     "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "working", list.get( 0 ) );
    }

    @Test
    public void testDateCoercionWithInstanceVariable() {
        // DROOLS-970
        String drl = "import " + DateContainer.class.getCanonicalName() + "\n" +
                     "import java.util.Date\n" +
                     "global java.util.List list\n" +
                     "rule Init when\n" +
                     "then\n" +
                     "    insert(new DateContainer(new Date(0)));" +
                     "end\n" +
                     "\n" +
                     "rule \"Test rule\"\n" +
                     "when\n" +
                     "    $container: DateContainer( date <= \"01-Oct-2013\" )\n" +
                     "then\n" +
                     "    list.add(\"working\");\n" +
                     "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "working", list.get( 0 ) );
    }

    public static class DateContainer {
        public final Date date;

        public DateContainer( Date date ) {
            this.date = date;
        }
    }

    @Test
    public void testDateCoercionWithNestedOr() {
        // BZ-1253575
        String drl = "import java.util.Date\n" +
                     "global java.util.List list\n" +
                     "declare DateContainer\n" +
                     "     date: Date\n" +
                     "end\n" +
                     "\n" +
                     "rule Init when\n" +
                     "then\n" +
                     "    insert(new DateContainer(new Date( 1439882189744L )));" +
                     "end\n" +
                     "\n" +
                     "rule \"Test rule\"\n" +
                     "when\n" +
                     "    $container: DateContainer( (date >= \"19-Jan-2014\" && date <= \"03-Dec-2015\" ) || (date >= \"17-Dec-2016\" && date <= \"02-Jan-2017\" ) )\n" +
                     "then\n" +
                     "    list.add(\"working\");\n" +
                     "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "working", list.get( 0 ) );
    }
}
