package org.drools.ruleunits.impl.datasources;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultEventHandle;
import org.kie.api.time.SessionPseudoClock;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventListDataStream<T> implements DataStream<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListDataStream.class);

    private final ArrayList<T> values = new ArrayList<>();
    private final List<DataProcessor> subscribers = new ArrayList<>();

    @SafeVarargs
    public static <T> EventListDataStream<T> create(T... ts) {
        EventListDataStream<T> stream = new EventListDataStream<>();
        for (T t : ts) {
            stream.append(t);
        }
        return stream;
    }

    @Override
    public void append(T t) {
        values.add(t);
        for (DataProcessor subscriber : subscribers) {
            insertAndAdvanceClock(t, subscriber);
        }
    }

    @Override
    public void subscribe(DataProcessor subscriber) {
        subscribers.add(subscriber);
        values.forEach(v -> insertAndAdvanceClock(v, subscriber));
    }

    private void insertAndAdvanceClock(T t, DataProcessor subscriber) {
        DefaultEventHandle fh = (DefaultEventHandle) subscriber.insert(null, t);
        long timestamp = fh.getStartTimestamp();
        WorkingMemoryEntryPoint ep = fh.getEntryPoint(null);
        SessionPseudoClock clock = (SessionPseudoClock) ep.getReteEvaluator().getSessionClock();
        long advanceTime = timestamp - clock.getCurrentTime();
        if (advanceTime > 0) {
            clock.advanceTime(advanceTime, TimeUnit.MILLISECONDS);
        } else if (advanceTime < 0) {
            LOGGER.warn("Received an event with a timestamp that is " + (-advanceTime) + " milliseconds in the past. " +
                    "Evaluation of out of order events could lead to unpredictable results.");
        }
    }
}
