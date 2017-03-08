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

package org.drools.compiler.oopath;

import java.util.Collection;
import org.assertj.core.api.Assertions;
import org.drools.compiler.oopath.model.Adult;
import org.drools.compiler.oopath.model.Child;
import org.drools.compiler.oopath.model.Man;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

public class OOPathCollectTest {

    @Test
    public void testCollect() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List<Adult> globalVar\n" +
                        "rule R when\n" +
                        "  $collectResults : java.util.ArrayList() \n " +
                        "    from collect( Adult( /children{age > 5} ) ) \n" +
                        "then\n" +
                        "  kcontext.getKieRuntime().setGlobal(\"globalVar\", $collectResults);\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

        final Man bob = new Man( "Bob", 40 );
        bob.addChild( new Child( "Charles", 12 ) );
        bob.addChild( new Child( "Debbie", 8 ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        final Collection<Adult> result = (Collection<Adult>) ksession.getGlobal("globalVar");
        Assertions.assertThat(result).containsExactlyInAnyOrder(bob);
    }
}
