package org.drools.vsm;

public interface GenericIoWriter {
    void write(Message msg,
               MessageResponseHandler responseHandler);
}