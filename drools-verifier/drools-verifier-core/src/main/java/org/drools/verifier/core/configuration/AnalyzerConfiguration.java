package org.drools.verifier.core.configuration;

import java.util.Date;

import org.drools.verifier.core.checks.base.CheckRunner;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UUIDKeyProvider;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.util.PortablePreconditions;

public class AnalyzerConfiguration {

    private final UUIDKeyProvider uuidKeyProvider;
    private final String webWorkerUUID;
    private final DateTimeFormatProvider dateTimeFormatter;
    private final CheckConfiguration checkConfiguration;
    private final CheckRunner checkRunner;

    public AnalyzerConfiguration(final String webWorkerUUID,
                                 final DateTimeFormatProvider dateTimeFormatter,
                                 final UUIDKeyProvider uuidKeyProvider,
                                 final CheckConfiguration checkConfiguration,
                                 final CheckRunner checkRunner) {
        this.webWorkerUUID = PortablePreconditions.checkNotNull("webWorkerUUID",
                                                                webWorkerUUID);
        this.dateTimeFormatter = PortablePreconditions.checkNotNull("dateTimeFormatter",
                                                                    dateTimeFormatter);
        this.uuidKeyProvider = PortablePreconditions.checkNotNull("uuidKeyProvider",
                                                                  uuidKeyProvider);
        this.checkConfiguration = PortablePreconditions.checkNotNull("checkConfiguration",
                                                                     checkConfiguration);
        this.checkRunner = PortablePreconditions.checkNotNull("checkRunner",
                                                              checkRunner);
    }

    public String getWebWorkerUUID() {
        return webWorkerUUID;
    }

    public UUIDKey getUUID(final HasKeys hasKeys) {
        return uuidKeyProvider.get(hasKeys);
    }

    public String formatDate(final Date dateValue) {
        return dateTimeFormatter.format(dateValue);
    }

    public Date parse(final String dateValue) {
        return dateTimeFormatter.parse(dateValue);
    }

    public CheckConfiguration getCheckConfiguration() {
        return checkConfiguration;
    }

    public CheckRunner getCheckRunner() {
        return checkRunner;
    }
}

