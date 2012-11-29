package org.kie.builder;

import java.util.List;

public interface KieBuilder {

    List<Message> build();

    boolean hasMessages();

    Messages getMessages();

    KieJar getKieJar();
}
