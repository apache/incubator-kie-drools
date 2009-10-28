package org.drools.vsm;

import org.drools.vsm.mina.MinaIoWriter;

public interface ClientGenericMessageReceiver {

    public abstract void addResponseHandler(int id,
                                            MessageResponseHandler responseHandler);

    public abstract void messageReceived(GenericIoWriter writer,
                                         Message msg) throws Exception;

}