/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.drools.reliability.test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.test.domain.fireandalarm.Alarm;
import org.test.domain.fireandalarm.Fire;
import org.test.domain.fireandalarm.Room;
import org.test.domain.fireandalarm.Sprinkler;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ReliabilityFireAndAlarmTest extends ReliabilityTestBasics{
    private static final String FIRE_AND_ALARM =
            "import " + Alarm.class.getCanonicalName() + ";" +
                    "import " + Fire.class.getCanonicalName() + ";" +
                    "import " + Sprinkler.class.getCanonicalName() + ";" +
                    "import " + Room.class.getCanonicalName() + ";" +
                    "global java.util.List results;" +
                    "rule 'When there is a fire turn on the sprinkler' when\n" +
                    "  Fire($room : room) \n" +
                    "  $sprinkler: Sprinkler( room == $room, on == false ) \n" +
                    "then\n" +
                    "  modify($sprinkler) { setOn(true); } \n" +
                    "  System.out.println(\"Turn on the sprinkler for room\" + $room.getName()); \n" +
                    "end\n" +
                    "rule 'Raise the alarm when we have one or more firs' when\n" +
                    "  exists Fire() \n" +
                    "then\n" +
                    "  insert( new Alarm() );\n" +
                    "  System.out.println(\"Raise the alarm\");\n" +
                    "end\n"+
                    "rule 'Cancel the alarm when all the fires have gone' when \n" +
                    "   not Fire() \n" +
                    "   $alarm : Alarm() \n" +
                    "then\n" +
                    "   delete ( $alarm ); \n" +
                    "   System.out.println(\"Cancel the alarm\"); \n" +
                    "end\n" +
                    "rule 'Status output when things are ok' when\n" +
                    "   not Alarm() \n" +
                    "   not Sprinkler ( on == true ) \n" +
                    "then \n" +
                    "   System.out.println(\"Everything is ok\"); \n" +
                    "end";


    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void testNoFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy){
        createSession(FIRE_AND_ALARM, persistenceStrategy, safepointStrategy);

        // phase 1
        Room room1 = new Room("Room 1");
        insert(room1);
        FactHandle fireFact1 = insert(new Fire(room1));
        fireAllRules();

        // phase 2
        Sprinkler sprinkler1 = new Sprinkler(room1);
        insert(sprinkler1);
        fireAllRules();

        assertThat(sprinkler1.isOn()).isTrue();

        // phase 3
        delete(fireFact1);
        fireAllRules();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void testInsertFailover_ShouldFireRules(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy){
        createSession(FIRE_AND_ALARM, persistenceStrategy, safepointStrategy, PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES);

        Room room1 = new Room("Room 1");
        Sprinkler sprinkler1 = new Sprinkler(room1);
        insert(room1);
        insert(new Fire(room1));
        insert(sprinkler1);

        failover();
        restoreSession(FIRE_AND_ALARM, persistenceStrategy,safepointStrategy,PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES);

        assertThat(fireAllRules()).isEqualTo(2);
        Optional<Object> sprinklerR = getObjectByType(Sprinkler.class);
        assertThat(sprinklerR.isEmpty()).isFalse();
        assertThat(((Sprinkler) sprinklerR.get()).isOn()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints") // does not support PersistedSessionOption.PersistenceObjectsStrategy.SIMPLE
    void testPhase1FailoverPhase2Phase3_ShouldFireRules(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy){
        createSession(FIRE_AND_ALARM, persistenceStrategy, safepointStrategy,PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES);

        // phase 1
        Room room1 = new Room("Room 1");
        insert(room1);
        insert(new Fire(room1));

        assertThat(fireAllRules()).isEqualTo(1);
        Optional<Object> alarm = getObjectByType(Alarm.class);
        assertThat(alarm.isEmpty()).isFalse();

        failover();
        restoreSession(FIRE_AND_ALARM, persistenceStrategy,safepointStrategy,PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES);

        // phase 2
        Optional<Object> room = getObjectByType(Room.class);
        assertThat(room.isEmpty()).isFalse();
        Sprinkler sprinkler1 = new Sprinkler((Room) room.get());
        insert(sprinkler1);
        fireAllRules();

        Optional<Object> sprinkler = getObjectByType(Sprinkler.class);
        assertThat(sprinkler.isEmpty()).isFalse();
        assertThat(((Sprinkler) sprinkler.get()).isOn()).isTrue();

        // phase 3
        Optional<FactHandle> fireFh = getFactHandleByType(Fire.class);
        assertThat(fireFh.isEmpty()).isFalse();
        delete(fireFh.get());
        assertThat(fireAllRules()).isEqualTo(1);
    }
}
