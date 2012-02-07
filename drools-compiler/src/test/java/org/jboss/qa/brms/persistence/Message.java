package org.jboss.qa.brms.persistence;

import java.io.Serializable;

/**
 * Simple class used to give messages to rules or retrieve them from rules
 *
 * @author tschloss
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -7176392345381065685L;

    private String message;

    public Message() {
        message = "";
    }

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "org.jboss.qa.drools.domain.Message[message='" + message + "']";
    }
}
