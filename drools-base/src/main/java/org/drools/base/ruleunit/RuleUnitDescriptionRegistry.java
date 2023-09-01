package org.drools.base.ruleunit;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.kie.internal.ruleunit.RuleUnitDescription;

public class RuleUnitDescriptionRegistry {

    private State state = State.UNKNOWN;

    private final Map<String, RuleUnitDescription> ruleUnits = new ConcurrentHashMap<>();

    public RuleUnitDescriptionRegistry() { }

    public RuleUnitDescription getDescription(Object ruleUnit) {
        final RuleUnitDescription ruleUnitDescr = ruleUnits.get(ruleUnit.getClass().getName());
        if (ruleUnitDescr == null) {
            throw new IllegalStateException("Unknown RuleUnitDescription: " + ruleUnit.getClass().getName());
        }
        return ruleUnitDescr;
    }

    public Optional<RuleUnitDescription> getDescription(final String unitClassName) {
        return Optional.ofNullable(ruleUnits.get(unitClassName));
    }

    public Optional<RuleUnitDescription> getDescription(final RuleImpl rule) {
        return getDescription(rule.getRuleUnitClassName());
    }

    public void add(final RuleUnitDescriptionLoader loader) {
        if (loader != null) {
            ruleUnits.putAll(loader.getDescriptions());
            state = state.merge(loader.getState());
        }
    }

    public boolean hasUnits() {
        return !ruleUnits.isEmpty();
    }

    public enum State {
        UNIT,
        NO_UNIT,
        UNKNOWN;

        State hasUnit( boolean hasUnit) {
            if (hasUnit) {
                if (this == NO_UNIT) {
                    throw new IllegalStateException("Cannot mix rules with and without unit");
                }
                return UNIT;
            } else {
                if (this == UNIT) {
                    throw new IllegalStateException("Cannot mix rules with and without unit");
                }
                return NO_UNIT;
            }
        }

        State merge( State other) {
            if (this == UNKNOWN) {
                return other;
            }
            if (other == UNKNOWN) {
                return this;
            }
            if (this != other) {
                throw new IllegalStateException("Cannot mix rules with and without unit");
            }
            return this;
        }
    }
}
