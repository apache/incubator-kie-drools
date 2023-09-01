package org.drools.verifier.core.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Severity;

public class CheckConfiguration {

    private final Set<CheckType> configurations = new HashSet<>();

    private final Map<CheckType, Severity> severityOverwrites = new HashMap<>();

    private CheckConfiguration() {
    }

    public static CheckConfiguration newDefault() {
        final CheckConfiguration checkConfiguration = new CheckConfiguration();

        checkConfiguration.getCheckConfiguration()
                .addAll(Arrays.asList(CheckType.values()));

        return checkConfiguration;
    }

    public static CheckConfiguration newEmpty() {
        return new CheckConfiguration();
    }

    public Set<CheckType> getCheckConfiguration() {
        return configurations;
    }

    public void setSeverityOverwrites(CheckType checkType, Severity severity) {
        severityOverwrites.put(checkType, severity);
    }

    public Optional<Severity> getSeverityOverwrite(final CheckType checkType) {
        if (severityOverwrites.containsKey(checkType)) {
            return Optional.of(severityOverwrites.get(checkType));
        } else {
            return Optional.empty();
        }
    }
}
