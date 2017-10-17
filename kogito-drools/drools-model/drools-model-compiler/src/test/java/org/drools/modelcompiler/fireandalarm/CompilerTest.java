package org.drools.modelcompiler.fireandalarm;

import org.drools.modelcompiler.BaseModelTest;
import org.drools.modelcompiler.fireandalarm.model.Alarm;
import org.drools.modelcompiler.fireandalarm.model.Fire;
import org.drools.modelcompiler.fireandalarm.model.Room;
import org.drools.modelcompiler.fireandalarm.model.Sprinkler;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class CompilerTest extends BaseModelTest {

    public CompilerTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    @Ignore
    public void testFireAndAlarm() {
        String str =
                "import " + Alarm.class.getCanonicalName() + ";" +
                "import " + Fire.class.getCanonicalName() + ";" +
                "import " + Room.class.getCanonicalName() + ";" +
                "import " + Sprinkler.class.getCanonicalName() + ";" +
                "rule \"When there is a fire turn on the sprinkler\"\n" +
                "when\n" +
                "   Fire( $room : room )\n" +
                "   $sprinkler : Sprinkler( room == $room, !on )\n" +
                "then\n" +
                "   modify( $sprinkler ) { setOn( true ) };\n" +
                "   System.out.println( \"Turn on the sprinkler for room \" + $room.getName() );\n" +
                "end\n" +
                "\n" +
                "rule \"When the fire is gone turn off the sprinkler\"\n" +
                "when\n" +
                "   $sprinkler : Sprinkler( $room : room, on == true )\n" +
                "   not Fire( room == $room )\n" +
                "then\n" +
                "   modify( $sprinkler ) { setOn( false ) };\n" +
                "   System.out.println( \"Turn off the sprinkler for room \" + $room.getName() );\n" +
                "end\n" +
                "\n" +
                "rule \"Raise the alarm when we have one or more fires\"\n" +
                "when\n" +
                "   exists Fire()\n" +
                "then\n" +
                "   insert( new Alarm() );\n" +
                "   System.out.println( \"Raise the alarm\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Lower the alarm when all the fires have gone\"\n" +
                "when\n" +
                "   not Fire()\n" +
                "   $alarm : Alarm()\n" +
                "then\n" +
                "   retract( $alarm );\n" +
                "   System.out.println( \"Lower the alarm\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Status output when things are ok\"\n" +
                "when\n" +
                "   not Alarm()\n" +
                "   not Sprinkler( on )\n" +
                "then\n" +
                "   System.out.println( \"Everything is ok\" );\n" +
                "end\n";

        KieSession ksession = getKieSession(str);

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
    }

//    private KieSession getKieSession(String str) {
//        KieServices ks = KieServices.Factory.get();
//        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
//        ks.newKieBuilder( kfs ).buildAll();
//        return ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
//    }
}
