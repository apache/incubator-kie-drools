/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.parser;

import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.domain.Person;
import org.junit.Test;

public class ParserTest {

    @Test
    public void test() {
        String str =
                "package mypkg;\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setAge( 18 ) };" +
                "  insert(\"Done\");\n" +
                "end";

        AnalysisModel analysisModel = new ModelBuilder().build( str );
        System.out.println(analysisModel);
    }
}
