package org.kie.api.event.kiescanner;

import org.kie.api.builder.Results;

public interface KieScannerUpdateResultsEvent extends KieScannerEvent {
    Results getResults();
}
