package org.kie.dmn.core.internal.utils;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;

public class FailedConversionException extends RuntimeException {

    private static final long serialVersionUID = -8455184925130529001L;

    public FailedConversionException(FEELEvent event) {
        super(event.getMessage());
    }
}