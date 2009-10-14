package org.drools.vsm;

public interface GenericMessageHandler {

    public abstract void messageReceived(GenericIoWriter session,
                                         Message msg) throws Exception;

}