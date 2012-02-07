package org.jboss.qa.brms.persistence;

import java.io.Serializable;

public class MessageEvent implements Serializable {
    private static final long serialVersionUID = 2161635271723812172L;

    private final Message msg;
    private final Type type;

    public enum Type {
        received, sent
    }

    public MessageEvent(Type type, Message msg) {
        this.type = type;
        this.msg = msg;
    }

    public Message getMsg() {
        return msg;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("MessageEvent[type=%s, message=%s]", type.toString(), msg.toString());
    }
}
