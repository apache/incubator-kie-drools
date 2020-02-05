package org.kie.kogito.rules;

import java.util.Optional;
import java.util.OptionalInt;

import org.kie.kogito.conf.ClockType;
import org.kie.kogito.conf.EventProcessingType;

public final class RuleUnitConfig {

    public static final RuleUnitConfig Default =
            new RuleUnitConfig(EventProcessingType.CLOUD, ClockType.REALTIME, null);

    private final Optional<EventProcessingType> eventProcessingType;
    private final Optional<ClockType> clockType;
    private final OptionalInt sessionPool;

    public RuleUnitConfig(EventProcessingType eventProcessingType, ClockType clockType, Integer sessionPool) {
        this.eventProcessingType = Optional.ofNullable(eventProcessingType);
        this.clockType = Optional.ofNullable(clockType);
        this.sessionPool = sessionPool == null? OptionalInt.empty() : OptionalInt.of(sessionPool);
    }

    public RuleUnitConfig(Optional<EventProcessingType> eventProcessingType, Optional<ClockType> clockType, OptionalInt sessionPool) {
        this.eventProcessingType = eventProcessingType;
        this.clockType = clockType;
        this.sessionPool = sessionPool;
    }

    public Optional<EventProcessingType> getEventProcessingType() {
        return eventProcessingType;
    }

    public EventProcessingType getDefaultedEventProcessingType() {
        return getEventProcessingType().orElseGet(Default.getEventProcessingType()::get);
    }

    public Optional<ClockType> getClockType() {
        return clockType;
    }

    public ClockType getDefaultedClockType() {
        return getClockType().orElseGet(Default.getClockType()::get);
    }

    public OptionalInt getSessionPool() {
        return sessionPool;
    }

    public OptionalInt getDefaultedSessionPool() {
        return sessionPool.isPresent() ? sessionPool :  Default.getSessionPool();
    }

    /**
     * return the merged config with the given. Given config keys win over this
     */
    public RuleUnitConfig merged(RuleUnitConfig overrides) {
        if (overrides == null) {
            return this;
        }
        return new RuleUnitConfig(
                overrides.getEventProcessingType().isPresent() ? overrides.getEventProcessingType() : this.getEventProcessingType(),
                overrides.getClockType().isPresent() ? overrides.getClockType() : this.getClockType(),
                overrides.getSessionPool().isPresent() ? overrides.getSessionPool() : this.getSessionPool());
    }
}
