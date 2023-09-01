package org.drools.compiler.kie.builder.impl.event;

import org.kie.api.builder.KieScanner.Status;
import org.kie.api.event.kiescanner.KieScannerStatusChangeEvent;

public class KieScannerStatusChangeEventImpl  implements KieScannerStatusChangeEvent {
    private final Status status;
    public KieScannerStatusChangeEventImpl(Status status) {
        this.status = status;
    }
    public Status getStatus() {
        return status;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof KieScannerStatusChangeEventImpl))
            return false;
        KieScannerStatusChangeEventImpl other = (KieScannerStatusChangeEventImpl) obj;
        if (status != other.status)
            return false;
        return true;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KieScannerStatusChangeEvent [status=").append(status).append("]");
        return builder.toString();
    }
}