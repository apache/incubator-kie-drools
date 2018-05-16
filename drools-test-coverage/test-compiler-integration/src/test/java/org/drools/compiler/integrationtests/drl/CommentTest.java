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

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieBase;

public class CommentTest extends CommonTestMethodBase {

    @Test
    public void testCommentDelimiterInString() throws Exception {
        // JBRULES-3401
        final String str = "rule x\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "System.out.println( \"/*\" );\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString( str );
        kbase.newKieSession();
    }

    @Test
    public void testCommentWithCommaInRHS() {
        // JBRULES-3648
        final String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   $p : Person( age < name.length ) \n" +
                "then\n" +
                "   insertLogical(new Person(\"Mario\",\n" +
                "       // this is the age,\n" +
                "       38));" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString( str );
        kbase.newKieSession();
    }
}
