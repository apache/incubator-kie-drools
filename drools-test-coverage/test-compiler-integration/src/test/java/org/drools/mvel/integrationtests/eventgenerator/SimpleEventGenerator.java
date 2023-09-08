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
package org.drools.mvel.integrationtests.eventgenerator;


import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;


public class SimpleEventGenerator {

    private final KieSession ksession;
    private final long generationEndTime;
    private static AbstractEventListener sendListener;
    private final boolean endInfinite;
    private int eventSourceIdCounter;
    private final Map<String, Integer> eventSourceIds;
    private final LinkedList<EventOccurrence> nextEventSource;
    private final PseudoSessionClock myClock;
    FactHandle clockHandle;

    public SimpleEventGenerator(final KieSession ksession, final AbstractEventListener l) {
        this (ksession, l, 0);
    }

    public SimpleEventGenerator(final KieSession ksession, final AbstractEventListener l, final long generationDuration) {
        this.ksession = ksession;
        sendListener = l;
        // add session clock to working memory
        this.myClock = new PseudoSessionClock();
        this.clockHandle = ksession.insert(myClock);

        this.generationEndTime = this.myClock.calcFuturePointInTime(generationDuration);
        this.endInfinite = (generationDuration == 0);
        this.eventSourceIdCounter = 0;
        this.eventSourceIds = new HashMap<>();
        this.nextEventSource = new LinkedList<>();
    }

    /**
     * @return the endInfinite
     */
    public boolean isEndInfinite() {
        return endInfinite;
    }

    // add source which will generate events from current clock time on, possibly forever (i.e. no boundaries)
    public EventOccurrence addEventSource(final String id, final Event ev, final long minOccur, final long avgOccur){
        return addEventSource (id, ev, minOccur, avgOccur, 0, 0);
    }

    // add source which will generate events from current clock time on, but only maxItems instances at the most AND not exceeding the time specified by maxDuration
    public EventOccurrence addEventSource(final String id, final Event ev, final long minOccur, final long avgOccur, final long maxDuration, final int maxItems){
        if (!eventSourceIds.containsKey(id)){
            this.eventSourceIds.put(id, eventSourceIdCounter++);
            final EventOccurrence evOcc = new EventOccurrence(id, ev, minOccur, avgOccur, this.myClock.getCurrentTime(), maxDuration, maxItems);
            this.nextEventSource.add (evOcc);
            return evOcc;
        }
        return null;
    }

    // add source which will generate events from the given start time on, possibly forever (i.e. no boundaries)
    public EventOccurrence addDelayedEventSource(final String id, final Event ev, final long minOccur, final long avgOccur, final long startTime){
        return addDelayedEventSource (id, ev, minOccur, avgOccur, startTime, 0, 0);
    }

    // add source which will generate events from the given start time on, but only maxItems instances at the most AND not exceeding the time specified by maxDuration
    public EventOccurrence addDelayedEventSource(final String id, final Event ev, final long minOccur, final long avgOccur, final long startTime, final long maxDuration, final int maxItems){
        if (!eventSourceIds.containsKey(id)){
            this.eventSourceIds.put(id, eventSourceIdCounter++);
            final EventOccurrence evOcc = new EventOccurrence(id, ev, minOccur, avgOccur, startTime, maxDuration, maxItems);
            this.nextEventSource.add (evOcc);
            return evOcc;
        }
        return null;
    }

    public void removeEventSource(final String id){
        final Integer hashValue = eventSourceIds.get(id);
        if (hashValue != null){
            this.nextEventSource.remove(hashValue.intValue());
            this.eventSourceIds.remove(id);
            this.eventSourceIdCounter--;
        }
    }

    public static void sendGeneratedEvent(final Event ev){
        sendListener.generatedEventSent(ev);
    }


    public void generate() {
        EventOccurrence currentEGT;
        Event currentEvent;

        //sort all events according to their first occurrence
        nextEventSource.sort(new EventGenerationTimeComparator());

        // simulate ongoing simulation time and upcoming events
        while (!nextEventSource.isEmpty()) {

            // get next event generation time from queue
            currentEGT = nextEventSource.removeFirst();
            currentEvent = (Event)currentEGT.getEvent().clone();

            // advance clock to time of currentEGT
            myClock.setTime(currentEGT.getNextOccurrenceTime());

            // send event corresponding to currentEGT
            sendGeneratedEvent(currentEvent);
            //System.out.println ("Sender "+currentEGT.getEventSenderId() + ": Sent "+currentEvent.getEventId()+" event for parent id " + currentEvent.getParentId() + " at " + Tools.formattedDate(currentEGT.getNextOccurrenceTime()));

            //update clock in working memory in order being able to process time sensitive rules
            ksession.update(clockHandle, myClock);

            // determine new event generation time for this event type
            final boolean occIsValid = currentEGT.calculateNextEventOccurrence();
            // add the new generation time to the right position in the queue,
            // but only if the generated event met its local restrictions plus
            // it is going to occur within the global time boundaries (or there are no such boundaries, respectively)
            if (occIsValid && (isEndInfinite() || currentEGT.getNextOccurrenceTime() < this.generationEndTime)) {
                final int index = Collections.binarySearch(nextEventSource, currentEGT, new EventGenerationTimeComparator());
                if (index < 0)
                    nextEventSource.add(-index-1, currentEGT);
            }
            //session.out.print ("\nQueue nach Bearbeitung: ");
            //for (int i = 0; i < nextEventSource.size(); i++)
            //    session.out.print (Tools.formattedDate(nextEventSource.get(i).getGenerationTime())+"\""+nextEventSource.get(i).getEventSenderId()+"; ");

        };

        //System.out.println ("\nSending of messages finished");
    }

}

class EventOccurrence{

    private final String eventSenderId;
    private final Event event;
    private final long evDeviation;
    private final long evMinDur;
    private int itemCounter;
    private final int maxItems;
    private final long latestEnd;
    private final boolean infinite;

    public EventOccurrence(final String eventSenderId, final Event ev, final long evMinDur, final long avgOccur, final long earliestStart, final long maxDuration, final int maxItems) {
        this.eventSenderId = eventSenderId;
        this.evMinDur = evMinDur;
        this.evDeviation = 2*(avgOccur-evMinDur);

        this.event = ev;
        this.event.setTimes(earliestStart);
        this.latestEnd = earliestStart+maxDuration;
        this.maxItems = maxItems;
        this.itemCounter = 0;
        this.infinite = maxDuration<=0;
        calculateNextEventOccurrence();
    }

    // returns true if event source has no boundaries or all restrictions (i.e. a generation duration or max. number of event instances) are met, false otherwise
    public boolean calculateNextEventOccurrence() {
        this.event.setTimes(this.event.getEndTime() + (this.evDeviation - 1) + this.evMinDur);
        if (maxItems <= 0) {
            return isInfinite() || event.getEndTime() < this.latestEnd;
        } else {
            this.itemCounter++;
            if (this.itemCounter > this.maxItems) {
                return false;
            }
            return isInfinite() || (event.getEndTime() < this.latestEnd);
        }
    }

    /**
     * @return the infinite
     */
    private boolean isInfinite() {
        return infinite;
    }

    /**
     * @return the eventSenderId
     */
    public String getEventSenderId() {
        return eventSenderId;
    }

    /**
     * @return the time
     */
    public long getNextOccurrenceTime() {
        return this.event.getEndTime();
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

}

class EventGenerationTimeComparator implements Comparator<EventOccurrence>{

    public int compare(final EventOccurrence o1, final EventOccurrence o2) {
        return (int)(o1.getNextOccurrenceTime()-o2.getNextOccurrenceTime());
    }

}
