package org.kie.api.event.kiescanner;

import java.util.EventListener;

public interface KieScannerEventListener
        extends
        EventListener {
    /**
     * Method called when the KieScanner transitioned to a new {@link org.kie.api.builder.KieScanner.Status}
     */
    void onKieScannerStatusChangeEvent( KieScannerStatusChangeEvent statusChange );
    
    /**
     * Method called when the KieScanner performed an update.
     */
    void onKieScannerUpdateResultsEvent( KieScannerUpdateResultsEvent updateResults );
}
