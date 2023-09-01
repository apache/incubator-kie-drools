/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.fireandalarm;

import org.drools.model.codegen.execmodel.BaseModelTest;
import org.drools.model.codegen.execmodel.fireandalarm.model.Alarm;
import org.drools.model.codegen.execmodel.fireandalarm.model.Fire;
import org.drools.model.codegen.execmodel.fireandalarm.model.Room;
import org.drools.model.codegen.execmodel.fireandalarm.model.Sprinkler;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerTest extends BaseModelTest {

    public CompilerTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testFireAndAlarm() {
        String str =
                "import " + StringBuilder.class.getCanonicalName() + ";\n" +
                "import " + Alarm.class.getCanonicalName() + ";\n" +
                "import " + Fire.class.getCanonicalName() + ";\n" +
                "import " + Room.class.getCanonicalName() + ";\n" +
                "import " + Sprinkler.class.getCanonicalName() + ";\n" +
                "global StringBuilder sb;" +
                "rule \"When there is a fire turn on the sprinkler\"" +
                "when\n" +
                "   Fire( $room : room )\n" +
                "   $sprinkler : Sprinkler( room == $room, !on )\n" +
                "then\n" +
                "   modify( $sprinkler ) { setOn( true ) };\n" +
                "   sb.append( \"Turn on the sprinkler for room \" + $room.getName() + \"\\n\");\n" +
                "end\n" +
                "\n" +
                "rule \"When the fire is gone turn off the sprinkler\"" +
                "when\n" +
                "   $sprinkler : Sprinkler( $room : room, on == true )\n" +
                "   not Fire( room == $room )\n" +
                "then\n" +
                "   modify( $sprinkler ) { setOn( false ) };\n" +
                "   sb.append( \"Turn off the sprinkler for room \" + $room.getName() + \"\\n\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Raise the alarm when we have one or more fires\"" +
                "when\n" +
                "   exists Fire()\n" +
                "then\n" +
                "   insert( new Alarm() );\n" +
                "   sb.append( \"Raise the alarm\\n\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Lower the alarm when all the fires have gone\"" +
                "when\n" +
                "   not Fire()\n" +
                "   $alarm : Alarm()\n" +
                "then\n" +
                "   retract( $alarm );\n" +
                "   sb.append( \"Lower the alarm\\n\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Status output when things are ok\"" +
                "when\n" +
                "   not Alarm()\n" +
                "   not Sprinkler( on )\n" +
                "then\n" +
                "   sb.append( \"Everything is ok\\n\" );\n" +
                "end\n";

        KieSession ksession = getKieSession(str);

        StringBuilder sb = new StringBuilder();
        ksession.setGlobal( "sb", sb );

        // phase 1
        Room room1 = new Room("Room 1");
        ksession.insert(room1);
        FactHandle fireFact1 = ksession.insert(new Fire(room1));
        ksession.fireAllRules();

        // phase 2
        Sprinkler sprinkler1 = new Sprinkler(room1);
        ksession.insert(sprinkler1);
        ksession.fireAllRules();

        // phase 3
        ksession.delete(fireFact1);
        ksession.fireAllRules();

        String result = "Raise the alarm\n" +
                "Turn on the sprinkler for room Room 1\n" +
                "Turn off the sprinkler for room Room 1\n" +
                "Lower the alarm\n" +
                "Everything is ok\n";

        assertThat(sb.toString()).isEqualTo(result);
    }
}
