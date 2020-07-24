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

import java.util.Arrays;

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PackagesIsolationTest extends BaseModelTest {

    public PackagesIsolationTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    public static class HashSet { }

    @Test
    public void testDoNotMixImports() {
        // DROOLS-5390
        String str2 =
                "package mypkg2\n" +
                "global java.util.List list\n" +
                "declare MyPojo xyz: String end\n" +
                "rule Init when then insert(new MyPojo(\"test\")); end\n" +
                "rule R2 when\n" +
                "  MyPojo(xyz == \"test\")\n" +
                "then\n" +
                "  list.add(\"R2\");\n" +
                "end";

        check( str2 );
    }

    @Test
    public void testImportWildcard() {
        String str2 =
                "package mypkg2\n" +
                "import mypkg1.*;\n" +
                "global java.util.List list\n" +
                "rule R2 when\n" +
                "  MyPojo(abc == \"test\")\n" +
                "then\n" +
                "  list.add(\"R2\");\n" +
                "end";

        check( str2 );
    }

    @Test
    public void testImportType() {
        String str2 =
                "package mypkg2\n" +
                "import mypkg1.MyPojo;\n" +
                "global java.util.List list\n" +
                "rule R2 when\n" +
                "  MyPojo(abc == \"test\")\n" +
                "then\n" +
                "  list.add(\"R2\");\n" +
                "end";

        check( str2 );
    }

    private void check( String str2 ) {
        String str1 =
                "package mypkg1\n" +
                        "global java.util.List list\n" +
                        "declare MyPojo abc: String end\n" +
                        "rule Init when then insert(new MyPojo(\"test\")); end\n" +
                        "rule R1 when\n" +
                        "  MyPojo(abc == \"test\")\n" +
                        "then\n" +
                        "  list.add(\"R1\");\n" +
                        "end";

        KieSession ksession = getKieSession( str1, str2 );

        java.util.List<String> list = new java.util.ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( Arrays.asList( "R1", "R2" ) ) );
    }

}
