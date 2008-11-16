package org.drools.compiler;

import java.io.Reader;

import org.drools.builder.DecisionTableConfiguration;

public interface DecisionTableProvider {

    String loadFromReader(Reader reader, DecisionTableConfiguration configuration);


}
