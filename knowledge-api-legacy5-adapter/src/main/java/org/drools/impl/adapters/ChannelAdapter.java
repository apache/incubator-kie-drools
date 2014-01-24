package org.drools.impl.adapters;

import org.drools.runtime.Channel;

public class ChannelAdapter implements org.kie.api.runtime.Channel {

    private final Channel delegate;

    public ChannelAdapter(Channel delegate) {
        this.delegate = delegate;
    }

    public void send(Object object) {
        delegate.send(object);
    }
}
