package org.kie.builder;

import java.util.List;

import org.kie.builder.Message.Level;

public interface KieBuilder {

    Messages build();

    boolean hasMessages(Level... levels);
                         
    Messages getMessages(Level... levels);
    
    Messages getMessages();

    KieJar getKieJar();
}
