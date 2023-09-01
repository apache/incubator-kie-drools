package org.drools.template.parser;

import org.kie.api.io.Resource;

public class DecisionTableParseException extends RuntimeException {

    private static final long serialVersionUID = 510l;

    public DecisionTableParseException(final Resource resource, final Throwable cause) {
        super(String.format("[%s] %s", resource.getSourcePath(), cause.getMessage()), cause);
    }

    public DecisionTableParseException(final String message) {
        super(message);
    }

    public DecisionTableParseException(final String message,
                                       final Throwable cause) {
        super(message,
              cause);
    }
}
