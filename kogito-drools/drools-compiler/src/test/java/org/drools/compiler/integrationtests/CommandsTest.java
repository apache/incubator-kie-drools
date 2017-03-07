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

import java.util.concurrent.TimeUnit;

import org.drools.compiler.Cheese;
import org.drools.core.ClockType;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.KieCommands;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;

public class CommandsTest {

    @Test
    public void testSessionTimeCommands() throws Exception {
        String str = "";
        str += "package org.drools.compiler.integrationtests \n";
        str += "import " + Cheese.class.getCanonicalName() + " \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += " $c : Cheese() \n";
        str += " then \n";
        str += " System.out.println($c); \n";
        str += "end \n";

        KieServices ks = KieServices.get();
        KieCommands kieCommands = ks.getCommands();

        KieSessionConfiguration sessionConfig = ks.newKieSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieSession kSession = new KieHelper().addContent( str, ResourceType.DRL )
                                             .build().newKieSession( sessionConfig, null );

        assertEquals(0L, (long) kSession.execute( kieCommands.newGetSessionTime() ) );
        assertEquals(2000L, (long) kSession.execute( kieCommands.newAdvanceSessionTime( 2, TimeUnit.SECONDS ) ) );
        assertEquals(2000L, (long) kSession.execute( kieCommands.newGetSessionTime() ) );

        kSession.dispose();
    }
}
