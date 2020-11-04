package org.kie.kogito.services.event;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.event.AbstractDataEvent;

import java.util.Objects;

/**
 * Small utility class to format DataEvents attributes from a given {@link ProcessInstance}
 */
public final class DataEventAttrBuilder {

    private DataEventAttrBuilder() {

    }

    public static String toSource(final ProcessInstance process) {
        Objects.requireNonNull(process);
        return String.format(AbstractDataEvent.SOURCE_FORMAT, process.getProcessId().toLowerCase());
    }

    public static String toSource(final String processId) {
        Objects.requireNonNull(processId);
        return String.format(AbstractDataEvent.SOURCE_FORMAT,processId.toLowerCase());
    }

    public static String toType(final String channelName, final ProcessInstance process) {
        Objects.requireNonNull(process);
        Objects.requireNonNull(channelName);
        return String.format(AbstractDataEvent.TYPE_FORMAT, process.getProcessId().toLowerCase(), channelName.toLowerCase());
    }

    public static String toType(final String channelName, final String processId) {
        Objects.requireNonNull(processId);
        Objects.requireNonNull(channelName);
        return String.format(AbstractDataEvent.TYPE_FORMAT, processId.toLowerCase(), channelName.toLowerCase());
    }

}
