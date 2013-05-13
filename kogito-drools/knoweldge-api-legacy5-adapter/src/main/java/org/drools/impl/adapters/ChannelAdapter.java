package org.drools.impl.adapters;

import org.kie.api.runtime.Channel;

public class ChannelAdapter implements org.drools.runtime.Channel {

    private final Channel delegate;

    public ChannelAdapter(Channel delegate) {
        this.delegate = delegate;
    }

    public void send(Object object) {
        delegate.send(object);
    }
}
