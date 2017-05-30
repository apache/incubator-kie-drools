/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.oopath;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.drools.compiler.oopath.model.Room;
import org.drools.compiler.oopath.model.SensorEvent;
import org.drools.compiler.oopath.model.Thing;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class OOPathQueriesTest {

    @Test
    public void testQueryFromCode() {
        final String drl =
                "import org.drools.compiler.oopath.model.Thing;\n" +
                        "query isContainedIn( Thing $x, Thing $y )\n" +
                        "    $y := /$x/children\n" +
                        "or\n" +
                        "    ( $z := /$x/children and isContainedIn( $z, $y; ) )\n" +
                        "end\n";

        final Thing smartphone = new Thing("smartphone");
        final List<String> itemList = Arrays.asList(new String[] { "display", "keyboard", "processor" });
        itemList.stream().map(item -> new Thing(item)).forEach((thing) -> smartphone.addChild(thing));

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();
        ksession.insert(smartphone);

        final QueryResults queryResults = ksession.getQueryResults("isContainedIn", new Object[] { smartphone, Variable.v });
        final List<String> resultList = StreamSupport.stream(queryResults.spliterator(), false)
                .map(row -> ((Thing) row.get("$y")).getName()).collect(Collectors.toList());
        assertThat(resultList).as("Query does not contain all items").containsAll(itemList);

        ksession.dispose();
    }

    @Test
    public void testReactiveQuery() {
        final String drl =
                "import org.drools.compiler.oopath.model.Room;\n" +
                    "import org.drools.compiler.oopath.model.Sensor;\n" +
                    "import org.drools.compiler.oopath.model.SensorEvent;\n" +
                    "query temperature ( Room $r, double $t )\n" +
                    "    $t := /$r/temperatureSensor/value\n" +
                    "end\n" +
                    "" +
                    "rule \"Change sensor value\" when\n" +
                    "    $e : SensorEvent( $s : sensor, $v : value)\n" +
                    "then\n" +
                    "    modify($s) { setValue($v) }\n" +
                    "    retract($e)\n" +
                    "end\n" +
                    "" +
                    "rule \"Turn heating on\" when\n" +
                    "    $r : Room()\n" +
                    "    temperature( $r, $t; )\n" +
                    "    eval( $t < 20 )" +
                    "then\n" +
                    "    $r.getHeating().setOn(true);\n" +
                    "end\n" +
                    "rule \"Turn heating off\" when\n" +
                    "    $r : Room()\n" +
                    "    temperature( $r, $t; )\n" +
                    "    eval( $t > 20 )" +
                    "then\n" +
                    "    $r.getHeating().setOn(false);\n" +
                    "end\n";

        final Room room = new Room("Room");
        room.getTemperatureSensor().setValue(15);
        room.getHeating().setOn(false);

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();
        ksession.insert(room);
        ksession.insert(room.getTemperatureSensor());
        ksession.insert(room.getHeating());
        ksession.fireAllRules();
        assertThat(room.getHeating().isOn()).as("Temperature is bellow 20 degrees of Celsius. Heating should be turned on.").isTrue();

        ksession.insert(new SensorEvent(room.getTemperatureSensor(), 25));
        ksession.fireAllRules();
        assertThat(room.getHeating().isOn()).as("Temperature is higher than 20 degrees of Celsius. Heating should be turned off.").isFalse();

        ksession.dispose();
    }

    @Test
    public void testNonReactiveOOPathInQuery() {
        final String drl =
                "import org.drools.compiler.oopath.model.Room;\n" +
                        "import org.drools.compiler.oopath.model.Sensor;\n" +
                        "import org.drools.compiler.oopath.model.SensorEvent;\n" +
                        "query temperature ( Room $r, double $t )\n" +
                        "    $t := /$r?/temperatureSensor/value\n" +
                        "end\n" +
                        "" +
                        "rule \"Change sensor value\" when\n" +
                        "    $e : SensorEvent( $s : sensor, $v : value)\n" +
                        "then\n" +
                        "    modify($s) { setValue($v) }\n" +
                        "    retract($e)\n" +
                        "end\n" +
                        "" +
                        "rule \"Turn heating on\" when\n" +
                        "    $r : Room()\n" +
                        "    temperature( $r, $t; )\n" +
                        "    eval( $t < 20 )" +
                        "then\n" +
                        "    $r.getHeating().setOn(true);\n" +
                        "end\n" +
                        "rule \"Turn heating off\" when\n" +
                        "    $r : Room()\n" +
                        "    temperature( $r, $t; )\n" +
                        "    eval( $t > 20 )" +
                        "then\n" +
                        "    $r.getHeating().setOn(false);\n" +
                        "end\n";

        final Room room = new Room("Room");
        room.getTemperatureSensor().setValue(15);
        room.getHeating().setOn(false);

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();
        ksession.insert(room);
        ksession.insert(room.getTemperatureSensor());
        ksession.insert(room.getHeating());
        ksession.fireAllRules();
        assertThat(room.getHeating().isOn()).as("Temperature is bellow 20 degrees of Celsius. Heating should be turned on.").isTrue();

        ksession.insert(new SensorEvent(room.getTemperatureSensor(), 25));
        ksession.fireAllRules();
        assertThat(room.getHeating().isOn()).as("Query is not reactive. Heating should still be turned on.").isTrue();

        ksession.dispose();
    }
}
