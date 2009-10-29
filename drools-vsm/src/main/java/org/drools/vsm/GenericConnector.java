package org.drools.vsm;

public interface GenericConnector extends GenericIoWriter {

    boolean connect();

    void disconnect();

    Message write(Message msg);
    
    //void write(Message msg);

}