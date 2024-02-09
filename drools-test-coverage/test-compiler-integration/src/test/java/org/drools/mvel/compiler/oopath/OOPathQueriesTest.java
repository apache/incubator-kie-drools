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
package org.drools.mvel.compiler.oopath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.drools.mvel.compiler.oopath.model.Room;
import org.drools.mvel.compiler.oopath.model.SensorEvent;
import org.drools.mvel.compiler.oopath.model.Thing;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class OOPathQueriesTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public OOPathQueriesTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testQueryFromCode() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.Thing;\n" +
                        "query isContainedIn( Thing $x, Thing $y )\n" +
                        "    $y := /$x/children\n" +
                        "or\n" +
                        "    ( $z := /$x/children and isContainedIn( $z, $y; ) )\n" +
                        "end\n";

        final Thing smartphone = new Thing("smartphone");
        final List<String> itemList = Arrays.asList("display", "keyboard", "processor");
        itemList.stream().map(item -> new Thing(item)).forEach((thing) -> smartphone.addChild(thing));

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        ksession.insert(smartphone);

        final QueryResults queryResults = ksession.getQueryResults("isContainedIn", smartphone, Variable.v);
        final List<String> resultList = StreamSupport.stream(queryResults.spliterator(), false)
                .map(row -> ((Thing) row.get("$y")).getName()).collect(Collectors.toList());
        assertThat(resultList).as("Query does not contain all items").containsAll(itemList);

        ksession.dispose();
    }

    @Test
    public void testReactiveQuery() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.Room;\n" +
                    "import org.drools.mvel.compiler.oopath.model.Sensor;\n" +
                    "import org.drools.mvel.compiler.oopath.model.SensorEvent;\n" +
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
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
                "import org.drools.mvel.compiler.oopath.model.Room;\n" +
                        "import org.drools.mvel.compiler.oopath.model.Sensor;\n" +
                        "import org.drools.mvel.compiler.oopath.model.SensorEvent;\n" +
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
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

    @Test
    public void testRecursiveOOPathQuery() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.Thing;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule \"Print all things contained in the Office\" when\n" +
                        "    $office : Thing( name == \"office\" )\n" +
                        "    isContainedIn( $office, thing; )\n" +
                        "then\n" +
                        "    list.add( thing.getName() );\n" +
                        "end\n" +
                        "\n" +
                        "query isContainedIn( Thing $x, Thing $y )\n" +
                        "    $y := /$x/children\n" +
                        "or\n" +
                        "    ( $z := /$x/children and isContainedIn( $z, $y; ) )\n" +
                        "end\n";

        final Thing house = new Thing( "house" );
        final Thing office = new Thing( "office" );
        house.addChild( office );
        final Thing kitchen = new Thing( "kitchen" );
        house.addChild( kitchen );

        final Thing knife = new Thing( "knife" );
        kitchen.addChild( knife );
        final Thing cheese = new Thing( "cheese" );
        kitchen.addChild( cheese );

        final Thing desk = new Thing( "desk" );
        office.addChild( desk );
        final Thing chair = new Thing( "chair" );
        office.addChild( chair );

        final Thing computer = new Thing( "computer" );
        desk.addChild( computer );
        final Thing draw = new Thing( "draw" );
        desk.addChild( draw );
        final Thing key = new Thing( "key" );
        draw.addChild( key );

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert(house);
        ksession.insert(office);
        ksession.insert(kitchen);
        ksession.insert(knife);
        ksession.insert(cheese);
        ksession.insert(desk);
        ksession.insert(chair);
        ksession.insert(computer);
        ksession.insert(draw);
        ksession.insert(key);

        ksession.fireAllRules();
        assertThat(list).containsExactlyInAnyOrder("desk", "chair", "key", "draw", "computer");
    }
}
