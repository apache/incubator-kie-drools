/**
 * 
 */
package org.drools.integrationtests.eventgenerator.example;

/**
 * @author Matthias Groch
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.integrationtests.eventgenerator.PseudoSessionClock;
import org.drools.integrationtests.eventgenerator.SimpleEventGenerator;
import org.drools.integrationtests.eventgenerator.SimpleEventListener;
import org.drools.rule.Package;


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

    private static WorkingMemory wm;
    //private static WorkingMemoryFileLogger logger;

    public static void setup(){
        // read in the source
        Reader source = new InputStreamReader (ExampleScenario.class.getResourceAsStream(FILE_NAME_RULES));
        // Use package builder to build up a rule package.
        // An alternative lower level class called DrlParser can also be used ...
        PackageBuilder builder = new PackageBuilder();
        // this will parse and compile in one step
        // NOTE: There are 2 methods here, the one argument one is for normal DRL.
        try {
                builder.addPackageFromDrl(source);
        } catch (DroolsParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // get the compiled package (which is serializable)
        Package pkg = builder.getPackage();

        // add defined object types
        //FactTemplate ftEvent = new FactTemplateImpl();
        //pkg.addFactTemplate(ftEvent);

        // add the package to a rulebase (deploy the rule package).
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        try {
            ruleBase.addPackage (pkg);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        wm = ruleBase.newStatefulSession();
        // create a new Working Memory Logger, that logs to file.
        //logger = new WorkingMemoryFileLogger(wm);
        // an event.log file is created in the log dir (which must exist)
        // in the working directory
        //logger.setFileName(FILE_NAME_LOGGER);
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
