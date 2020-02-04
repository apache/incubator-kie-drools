package org.drools.modelcompiler.consequence;

import org.drools.model.Channel;

public class ChannelImpl implements Channel {

    private org.kie.api.runtime.Channel channel;

    public ChannelImpl(org.kie.api.runtime.Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(Object object) {
        channel.send(object);
    }
}
