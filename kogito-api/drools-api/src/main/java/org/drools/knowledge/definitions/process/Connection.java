package org.drools.knowledge.definitions.process;

public interface Connection {

    Node getFrom();

    Node getTo();

    String getFromType();

    String getToType();

    Object getMetaData(String name);
    
}
