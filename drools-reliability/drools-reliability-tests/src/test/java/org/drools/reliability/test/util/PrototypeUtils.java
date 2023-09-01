package org.drools.reliability.test.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.base.facttemplates.Event;
import org.drools.model.Prototype;
import org.drools.model.PrototypeDSL;
import org.drools.model.PrototypeFact;

import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedEvent;

public class PrototypeUtils {

    public static final String DEFAULT_PROTOTYPE_NAME = "DROOLS_PROTOTYPE";
    public static final String SYNTHETIC_PROTOTYPE_NAME = "DROOLS_SYNTHETIC_PROTOTYPE";

    private static final Map<String, Prototype> prototypes = new HashMap<>();

    private PrototypeUtils() {
        // It is not allowed to create instances of util classes.
    }

    public static Event createControlEvent() {
        return createMapBasedEvent(getPrototype(SYNTHETIC_PROTOTYPE_NAME));
    }

    public static Prototype getPrototype(String name) {
        return prototypes.computeIfAbsent(name, PrototypeDSL::prototype);
    }

    public static void processResults(List<Object> globalResults, List<Event> controlResults) {
        List<Object> events = controlResults.stream().map(r -> ((PrototypeFact) r).get("event")).collect(Collectors.toList());
        globalResults.addAll(events);
    }

    public static Event createEvent() {
        return createMapBasedEvent(getPrototype(DEFAULT_PROTOTYPE_NAME));
    }
}
