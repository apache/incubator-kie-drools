package org.drools.vsm;

public interface GenericConnector {

    boolean connect();

    void disconnect();

    void addResponseHandler(int id,
                            MessageResponseHandler responseHandler);

    void write(Message msg);

}