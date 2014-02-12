package org.drools.compiler.integrationtests.eventgenerator.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.integrationtests.eventgenerator.PseudoSessionClock;
import org.drools.compiler.integrationtests.eventgenerator.SimpleEventGenerator;
import org.drools.compiler.integrationtests.eventgenerator.SimpleEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class ExampleScenario {

    // constants
    public final static String FILE_NAME_RULES = "../example_scenario.drl";
    //public final static String FILE_NAME_LOGGER = "log/event";
    private static int NUMBER_RESOURCES = 3;

    // event occurrence probabilities
    public final static int AVG_OCCUR_PRODUCTION_EVENT = 7000; // average time in milliseconds after which another item is manufactured by one resource; default: 700 ms
    public final static int MIN_OCCUR_PRODUCTION_EVENT = 4000; // minimum time in milliseconds after which another item is manufactured by one resource; default: 700 ms
    public final static int AVG_OCCUR_HEARTBEAT_EVENT = 90000; // average time in milliseconds after which a resource sends another heartbeat; default: 60000 ms
    public final static int MIN_OCCUR_HEARTBEAT_EVENT = 45000; // average time in milliseconds after which a resource sends another heartbeat; default: 60000 ms
    public final static int AVG_OCCUR_ALERT_EVENT = 1800000; // average time in milliseconds after which an alarm is sent; default: 1800000 = 30 mis
    public final static int MIN_OCCUR_ALERT_EVENT = 0; // average time in milliseconds after which an alarm is sent; default: 1800000 = 30 mis

    private static KieSession wm;
    //private static WorkingMemoryFileLogger logger;

    public static void setup(){
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(ExampleScenario.class.getResourceAsStream(FILE_NAME_RULES)), ResourceType.DRL);

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        wm = kbase.newStatefulKnowledgeSession();
    }

    public static final void main (String[] args) {

        setup();

        System.out.println("Waiting for messages...");
        System.out.println("Press [return] to quit\n");
        
        ArrayList<Resource> resources = new ArrayList<Resource>();
        
        // 
        SimpleEventGenerator myGenerator =  new SimpleEventGenerator(wm, new SimpleEventListener(wm), PseudoSessionClock.timeInMinutes(15));
        
        //create fab resources and add them to working memory
        for (int i = 0; i < NUMBER_RESOURCES; i++){

            Resource res = new Resource("mach"+i);
            resources.add(res);
            wm.insert(res.getOpStatus());

            SlidingWindow sw = new SlidingWindow(0, res.getId(), PseudoSessionClock.timeInMinutes(10), PseudoSessionClock.timeInMinutes(2));
            //GlobalWorkingMemory.getInstance().insert(new Event(Event.SLIDING_WINDOW, res.getId(), systemTime, systemTime));
            wm.insert(sw);

            // add eventSenders to EventGenerator
            myGenerator.addEventSource("Conveyor"+i, new ProductionEvent(res.getId()), MIN_OCCUR_PRODUCTION_EVENT, AVG_OCCUR_PRODUCTION_EVENT, 0, 0);
        }

        // start generating events
        myGenerator.generate();
        
        BufferedReader waiter = new BufferedReader(new InputStreamReader(System.in));
        try {
            waiter.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // stop logging
        //logger.writeToDisk();
        //System.out.println("Application terminated - Audit log written to disk");

    }

}
