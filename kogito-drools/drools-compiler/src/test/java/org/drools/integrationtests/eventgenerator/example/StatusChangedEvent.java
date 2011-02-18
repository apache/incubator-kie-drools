/**
 * 
 */
package org.drools.integrationtests.eventgenerator.example;

import java.util.HashMap;
import java.util.Map;

import org.drools.integrationtests.eventgenerator.Event;

/**
 * @author Matthias Groch
 *
 */
public class StatusChangedEvent extends Event {

    public static final String[] PROPERTY_FIELD_NAMES = {"OldValue", "NewValue"};

    public static final int OLD_VALUE = 0;
    public static final int NEW_VALUE = 1;

    /**
     * Special constructor for a statusChanged event
     * @param parentId The id of the corresponding site, resource, ...
     */
    public StatusChangedEvent(String parentId, int oldValue, int newValue) {
        super(EventType.STATUSCHANGED, parentId);
        addParameters(createStatusChangedParameters(oldValue, newValue));
    }

    /**
     * Special constructor for a statusChanged event
     * @param parentId The id of the corresponding site, resource, ...
     * @param start The start instance of the event.
     * @param end The end instance of the event.
     * @param parameters The event parameters.
     */
    public StatusChangedEvent(String parentId, long start, long end) {
        super(EventType.STATUSCHANGED, parentId, start, end);
    }

    /**
     * Special constructor for a statusChanged event
     * @param parentId The id of the corresponding site, resource, ...
     * @param start The start instance of the event.
     * @param end The end instance of the event.
     * @param parameters The event parameters.
     */
    public StatusChangedEvent(String parentId, int oldValue, int newValue, long start, long end) {
        super(EventType.STATUSCHANGED, parentId, start, end);
        addParameters(createStatusChangedParameters(oldValue, newValue));
    }

    private static Map<String, String> createStatusChangedParameters(int oldValue, int newValue){
        Map<String, String> params = new HashMap<String, String>();
        params.put(PROPERTY_FIELD_NAMES[OLD_VALUE], String.valueOf(oldValue));
        params.put(PROPERTY_FIELD_NAMES[NEW_VALUE], String.valueOf(newValue));
        return params;
    }

    /**
     * @return value of the oldValue parameter if such a parameter exists, null otherwise
     */
    public String getParamOldValue() {
        if (this.getParameters().containsKey(PROPERTY_FIELD_NAMES[OLD_VALUE]))
            return this.getParamValue(PROPERTY_FIELD_NAMES[OLD_VALUE]);
        return null;
    }

    /**
     * @return value of the newValue parameter if such a parameter exists, null otherwise
     */
    public String getParamNewValue() {
        if (this.getParameters().containsKey(PROPERTY_FIELD_NAMES[NEW_VALUE]))
            return this.getParamValue(PROPERTY_FIELD_NAMES[NEW_VALUE]);
        return null;
    }
}
