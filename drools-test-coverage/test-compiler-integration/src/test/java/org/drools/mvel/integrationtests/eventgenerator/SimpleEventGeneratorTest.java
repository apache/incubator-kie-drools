/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel.integrationtests.eventgenerator;

import java.io.IOException;

import org.drools.compiler.compiler.DroolsParserException;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.integrationtests.eventgenerator.Event.EventType;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleEventGeneratorTest extends CommonTestMethodBase {

    private final static String TEST_RULE_FILE = "test_eventGenerator.drl";

    @Test
    public void testEventGenerationMaxItems() throws DroolsParserException, IOException, Exception{
        KieBase kbase = loadKnowledgeBase(TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate 10 events, starting from the session clock
        myGenerator.addEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), 0, 10);
        myGenerator.generate();
        assertEquals(ksession.getQueryResults("all inserted events").size(), 10);
    }

    @Test
    public void testEventGenerationMaxTime() throws DroolsParserException, IOException, Exception{
        KieBase kbase = loadKnowledgeBase(TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate events for 1 min, starting from the session clock
        myGenerator.addEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(1), 0);
        myGenerator.generate();
        assertEquals(ksession.getQueryResults("all inserted events").size(), ksession.getQueryResults("all inserted events with generation time < 1 min").size());
    }

    @Test
    public void testEventGenerationMaxTimeAndMaxItems() throws DroolsParserException, IOException, Exception{
        KieBase kbase = loadKnowledgeBase(TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate at most 10 events not exceeding 1 min, starting from the session clock
        myGenerator.addEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(1), 10);
        myGenerator.generate();
        assertEquals(ksession.getQueryResults("all inserted events").size(), ksession.getQueryResults("all inserted events with generation time < 1 min").size());
        assertTrue(ksession.getQueryResults("all inserted events with generation time < 1 min").size()<=10);
    }

    @Test
    public void testEventGenerationDelayedMaxItems() throws DroolsParserException, IOException, Exception{
        KieBase kbase = loadKnowledgeBase(TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate 10 events, delayed by 2 minutes from start session clock
        myGenerator.addDelayedEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(2), 0, 10);
        myGenerator.generate();
        assertEquals(ksession.getQueryResults("all inserted events").size(), 10);
    }

    @Test
    public void testEventGenerationDelayedMaxTime() throws DroolsParserException, IOException, Exception{
        KieBase kbase = loadKnowledgeBase(TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate events for 1 min, delayed by 2 minutes from start session clock
        myGenerator.addDelayedEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(2), PseudoSessionClock.timeInMinutes(1), 0);
        myGenerator.generate();
        assertEquals(ksession.getQueryResults("all inserted events").size(), ksession.getQueryResults("all inserted events with 2 min < generation time < 3 min").size());
    }

    @Test
    public void testEventGenerationDelayedMaxTimeAndMaxItems() throws DroolsParserException, IOException, Exception{
        KieBase kbase = loadKnowledgeBase(TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate at most 10 events not exceeding 1 min, delayed by 2 minutes from start session clock
        myGenerator.addDelayedEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(2), PseudoSessionClock.timeInMinutes(1), 10);
        myGenerator.generate();
        assertEquals(ksession.getQueryResults("all inserted events").size(), ksession.getQueryResults("all inserted events with 2 min < generation time < 3 min").size());
        assertTrue(ksession.getQueryResults("all inserted events with 2 min < generation time < 3 min").size()<=10);
    }

    @Test
    public void testEventGenerationGlobalMaxTime() throws DroolsParserException, IOException, Exception{
        KieBase kbase = loadKnowledgeBase(TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession), PseudoSessionClock.timeInMinutes(1));

        // generate events for 1 min, starting from the session clock
        myGenerator.addEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(3), 0);
        myGenerator.generate();
        assertEquals(ksession.getQueryResults("all inserted events").size(), ksession.getQueryResults("all inserted events with generation time < 1 min").size());
    }

    @Test
    public void testEventGenerationMultipleSources() throws DroolsParserException, IOException, Exception{
        KieBase kbase = loadKnowledgeBase(TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate 15 events with parent resource A and 20 events with parent resource B
        myGenerator.addEventSource("Conveyor1", new Event(EventType.CUSTOM, "resA"), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), 0, 15);
        myGenerator.addEventSource("Conveyor2", new Event(EventType.CUSTOM, "resB"), PseudoSessionClock.timeInSeconds(3), PseudoSessionClock.timeInSeconds(5), 0, 20);
        myGenerator.generate();
        assertEquals(ksession.getQueryResults("all inserted events with parent resource A").size(), 15);
        assertEquals(ksession.getQueryResults("all inserted events with parent resource B").size(), 20);
    }

}
