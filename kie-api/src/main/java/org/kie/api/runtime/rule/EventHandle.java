package org.kie.api.runtime.rule;

public interface EventHandle {

    long getStartTimestamp();

    long getDuration();

    long getEndTimestamp();

}
