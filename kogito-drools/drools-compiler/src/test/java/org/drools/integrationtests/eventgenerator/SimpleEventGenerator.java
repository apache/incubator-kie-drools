package org.drools.integrationtests.eventgenerator;


import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import org.drools.FactHandle;
import org.drools.WorkingMemory;



public class SimpleEventGenerator {

    private WorkingMemory wm;
    private long generationEndTime;
    private static AbstractEventListener sendListener;
    private boolean endInfinite;
    private int eventSourceIdCounter;
    private Map<String, Integer> eventSourceIds;
    private LinkedList<EventOccurrence> nextEventSource;
    private PseudoSessionClock myClock;
    FactHandle clockHandle;

    /**
     * @param wm
     */
    public SimpleEventGenerator(WorkingMemory wm, AbstractEventListener l) {
        this (wm, l, 0);
    }

    /**
     * @param wm
     * @param generationDuration
     *
     */
    public SimpleEventGenerator(WorkingMemory wm, AbstractEventListener l, long generationDuration) {
        this.wm = wm;
        this.sendListener = l;
        // add session clock to working memory
        this.myClock = new PseudoSessionClock();
        this.clockHandle = wm.insert(myClock);

        this.generationEndTime = this.myClock.calcFuturePointInTime(generationDuration);
        this.endInfinite = (generationDuration == 0);
        this.eventSourceIdCounter = 0;
        this.eventSourceIds = new HashMap<String, Integer>();
        this.nextEventSource = new LinkedList<EventOccurrence>();
    }

    /**
     * @return the endInfinite
     */
    public boolean isEndInfinite() {
        return endInfinite;
    }

    // add source which will generate events from current clock time on, possibly forever (i.e. no boundaries)
    public EventOccurrence addEventSource(String id, Event ev, long minOccur, long avgOccur){
        return addEventSource (id, ev, minOccur, avgOccur, 0, 0);
    }

    // add source which will generate events from current clock time on, but only maxItems instances at the most AND not exceeding the time specified by maxDuration
    public EventOccurrence addEventSource(String id, Event ev, long minOccur, long avgOccur, long maxDuration, int maxItems){
        if (!eventSourceIds.containsKey(id)){
            this.eventSourceIds.put(id, new Integer(eventSourceIdCounter++));
            EventOccurrence evOcc = new EventOccurrence(id, ev, minOccur, avgOccur, this.myClock.getCurrentTime(), maxDuration, maxItems);
            this.nextEventSource.add (evOcc);
            return evOcc;
        }
        return null;
    }

    // add source which will generate events from the given start time on, possibly forever (i.e. no boundaries)
    public EventOccurrence addDelayedEventSource(String id, Event ev, long minOccur, long avgOccur, long startTime){
        return addDelayedEventSource (id, ev, minOccur, avgOccur, startTime, 0, 0);
    }

    // add source which will generate events from the given start time on, but only maxItems instances at the most AND not exceeding the time specified by maxDuration
    public EventOccurrence addDelayedEventSource(String id, Event ev, long minOccur, long avgOccur, long startTime, long maxDuration, int maxItems){
        if (!eventSourceIds.containsKey(id)){
            this.eventSourceIds.put(id, new Integer(eventSourceIdCounter++));
            EventOccurrence evOcc = new EventOccurrence(id, ev, minOccur, avgOccur, startTime, maxDuration, maxItems);
            this.nextEventSource.add (evOcc);
            return evOcc;
        }
        return null;
    }

    public void removeEventSource(String id){
        Integer hashValue = eventSourceIds.get(id);
        if (hashValue != null){
            this.nextEventSource.remove(hashValue.intValue());
            this.eventSourceIds.remove(id);
            this.eventSourceIdCounter--;
        }
    }

    public static void sendGeneratedEvent(Event ev){
        sendListener.generatedEventSent(ev);
    }


    public void generate() {
        EventOccurrence currentEGT;
        Event currentEvent;

        //sort all events according to their first occurrence
        Collections.sort(nextEventSource, new EventGenerationTimeComparator());

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
            wm.update(clockHandle, myClock);

            // determine new event generation time for this event type
            boolean occIsValid = currentEGT.calculateNextEventOccurrence();
            // add the new generation time to the right position in the queue,
            // but only if the generated event met its local restrictions plus
            // it is going to occur within the global time boundaries (or there are no such boundaries, respectively)
            if (occIsValid && (isEndInfinite() || currentEGT.getNextOccurrenceTime() < this.generationEndTime)) {
                int index = Collections.binarySearch(nextEventSource, currentEGT, new EventGenerationTimeComparator());
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

    private static Random myRandom = new Random();;

    private String eventSenderId;
    private Event event;
    private long evDeviation, evMinDur;
    private int itemCounter, maxItems;
    private long latestEnd;
    private boolean infinite;

    public EventOccurrence(String eventSenderId, Event ev, long evMinDur, long avgOccur, long earliestStart, long maxDuration, int maxItems) {
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
    public boolean calculateNextEventOccurrence (){
        this.event.setTimes(this.event.getEndTime()+myRandom.nextInt((int)this.evDeviation)+this.evMinDur);
        if (maxItems <=0){
            if (isInfinite())
                return true;
            else return event.getEndTime()<this.latestEnd;
            }
            else {
                this.itemCounter++;
                if (this.itemCounter>this.maxItems)
                    return false;
                if (isInfinite())
                    return true;
                else return (event.getEndTime()<this.latestEnd);
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

    public int compare(EventOccurrence o1, EventOccurrence o2) {
        return (int)(o1.getNextOccurrenceTime()-o2.getNextOccurrenceTime());
    }

}
