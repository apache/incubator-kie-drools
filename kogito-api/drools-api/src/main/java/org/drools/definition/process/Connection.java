package org.drools.definition.process;

import java.util.Map;

public interface Connection {

    Node getFrom();

    Node getTo();

    String getFromType();

    String getToType();

    Map<String, Object> getMetaData();

    @Deprecated Object getMetaData(String name);

}
