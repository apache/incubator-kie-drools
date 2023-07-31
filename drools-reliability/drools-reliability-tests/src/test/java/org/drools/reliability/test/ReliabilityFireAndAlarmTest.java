package org.drools.reliability.test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.test.domain.fireandalarm.Alarm;
import org.test.domain.fireandalarm.Fire;
import org.test.domain.fireandalarm.Room;
import org.test.domain.fireandalarm.Sprinkler;

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
        insert(room1);
        insert(new Fire(room1));
        insert(new Sprinkler(room1));

        failover();
        restoreSession(FIRE_AND_ALARM, persistenceStrategy,safepointStrategy,PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES);

        assertThat(fireAllRules()).isEqualTo(2);
    }
}
