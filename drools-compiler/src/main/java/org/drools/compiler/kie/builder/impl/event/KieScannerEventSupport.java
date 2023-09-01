package org.drools.compiler.kie.builder.impl.event;

import org.drools.core.event.AbstractEventSupport;
import org.kie.api.builder.KieScanner.Status;
import org.kie.api.builder.Results;
import org.kie.api.event.kiescanner.KieScannerEventListener;
import org.kie.api.event.kiescanner.KieScannerStatusChangeEvent;
import org.kie.api.event.kiescanner.KieScannerUpdateResultsEvent;

public class KieScannerEventSupport extends AbstractEventSupport<KieScannerEventListener> {
    
    public void fireKieScannerStatusChangeEventImpl(final Status status) {
        if ( hasListeners() ) {
            KieScannerStatusChangeEvent event = new KieScannerStatusChangeEventImpl(status);
            notifyAllListeners( event, ( l, e ) -> l.onKieScannerStatusChangeEvent( e ) );
        }
    }

    public void fireKieScannerUpdateResultsEventImpl(final Results results) {
        if ( hasListeners() ) {
            KieScannerUpdateResultsEvent event = new KieScannerUpdateResultsEventImpl(results);
            notifyAllListeners( event, ( l, e ) -> l.onKieScannerUpdateResultsEvent( e ) );
        }
    }
}
