package org.kie.api.event.kiescanner;

import org.kie.api.builder.KieScanner.Status;

public interface KieScannerStatusChangeEvent extends KieScannerEvent {
    Status getStatus();
}
