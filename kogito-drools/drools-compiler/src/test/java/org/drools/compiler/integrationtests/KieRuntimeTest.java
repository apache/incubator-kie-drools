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

package org.drools.compiler.integrationtests;

import java.io.IOException;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class KieRuntimeTest extends CommonTestMethodBase {

    @Test
    public void testKieRuntimeAccess() throws IOException, ClassNotFoundException {
        String str = "";
        str += "package org.drools.compiler.test\n";
        str += "import " + Message.class.getName() + "\n";
        str += "rule \"Hello World\"\n";
        str += "when\n";
        str += "    Message( )\n";
        str += "then\n";
        str += "    System.out.println( drools.getKieRuntime() );\n";
        str += "end\n";

        KieBase kbase = loadKnowledgeBaseFromString( str );
        kbase = SerializationHelper.serializeObject( kbase );
        final KieSession ksession = createKnowledgeSession( kbase );

        ksession.insert( new Message( "help" ) );
        ksession.fireAllRules();
        ksession.dispose();
    }

}
