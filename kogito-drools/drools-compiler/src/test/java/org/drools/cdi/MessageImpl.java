package org.drools.cdi;

public class MessageImpl implements Message {
    @Override
    public String getText() {
        return "default.msg";
    }        
}