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

import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.codegen.execmodel.fireandalarm.model.Alarm;
import org.drools.model.codegen.execmodel.fireandalarm.model.Fire;
import org.drools.model.codegen.execmodel.fireandalarm.model.Room;
import org.drools.model.codegen.execmodel.fireandalarm.model.Sprinkler;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.KieBaseBuilder;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.any;
import static org.drools.model.DSL.execute;
import static org.drools.model.DSL.exists;
import static org.drools.model.DSL.not;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;

public class FireAndAlarmTest {

    @Test
    public void testFireAndAlarm() {

        Variable<Room> room = any( Room.class );
        Variable<Fire> fire = any( Fire.class );
        Variable<Sprinkler> sprinkler = any( Sprinkler.class );
        Variable<Alarm> alarm = any( Alarm.class );

        Rule r1 = rule("When there is a fire turn on the sprinkler")
                .build(
                        pattern(fire),
                        pattern(sprinkler)
                                .expr(s -> !s.isOn())
                                .expr(fire, (s, f) -> s.getRoom().equals(f.getRoom())),
                        on( sprinkler )
                                .execute( (drools, s) -> {
                                    System.out.println( "Turn on the sprinkler for room " + s.getRoom().getName() );
                                    s.setOn( true );
                                    drools.update( s, "on" );
                                } )
                     );

        Rule r2 = rule("When the fire is gone turn off the sprinkler")
                .build(
                        pattern(sprinkler).expr(Sprinkler::isOn),
                        not(pattern(fire).expr( sprinkler, (f, s) -> f.getRoom().equals(s.getRoom()))),
                        on(sprinkler)
                            .execute( (drools, s) -> {
                                System.out.println("Turn off the sprinkler for room " + s.getRoom().getName());
                                s.setOn(false);
                                drools.update( s, "on" );
                            })
                     );

        Rule r3 = rule("Raise the alarm when we have one or more fires")
                .build(
                        exists(pattern(fire)),
                        execute((drools) -> {
                            System.out.println("Raise the alarm");
                            drools.insert(new Alarm());
                        })
                     );

        Rule r4 = rule("Lower the alarm when all the fires have gone")
                .build(
                        not(pattern(fire)),
                        pattern(alarm),
                        on(alarm).
                            execute((drools, a) -> {
                                System.out.println("Lower the alarm");
                                drools.delete(a);
                            })
                     );

        Rule r5 = rule("Status output when things are ok")
                .build(
                        not(pattern(alarm)),
                        not(pattern(sprinkler).expr(Sprinkler::isOn)),
                        execute(() -> System.out.println("Everything is ok"))
                     );

        Model model = new ModelImpl().withRules( asList( r1, r2, r3, r4, r5 ) );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        // phase 1
        Room room1 = new Room("Room 1");
        ksession.insert(room1);
        FactHandle fireFact1 = ksession.insert(new Fire(room1));
        ksession.fireAllRules();

        // phase 2
        Sprinkler sprinkler1 = new Sprinkler(room1);
        ksession.insert(sprinkler1);
        ksession.fireAllRules();

        assertThat(sprinkler1.isOn()).isTrue();

        // phase 3
        ksession.delete(fireFact1);
        ksession.fireAllRules();
    }
}
