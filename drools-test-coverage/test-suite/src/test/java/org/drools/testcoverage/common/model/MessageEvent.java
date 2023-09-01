package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class MessageEvent implements Serializable {

    private static final long serialVersionUID = 5700427692523132353L;

    private final Message msg;
    private final Type type;
    private final long duration;

    public enum Type {
        received, sent
    }

    public MessageEvent(final Type type, final Message msg) {
        this(type, msg, 0);
    }

    public MessageEvent(final Type type, final Message msg, final long duration) {
        this.type = type;
        this.msg = msg;
        this.duration = duration;
    }

    public Message getMsg() {
        return msg;
    }

    public Type getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return String.format("MessageEvent[type=%s, message=%s, duration=%d]", type.toString(), msg.toString(), duration);
    }
}
