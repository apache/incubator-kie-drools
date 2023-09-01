package org.drools.compiler.kie.builder.impl.event;

import org.kie.api.builder.Results;
import org.kie.api.event.kiescanner.KieScannerUpdateResultsEvent;

public class KieScannerUpdateResultsEventImpl implements KieScannerUpdateResultsEvent {
    private final Results results;
    public KieScannerUpdateResultsEventImpl(Results results) {
        this.results = results;
    }
    public Results getResults() {
        return results;
    }
}
