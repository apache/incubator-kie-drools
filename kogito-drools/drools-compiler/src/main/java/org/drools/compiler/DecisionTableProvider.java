package org.drools.compiler;

import java.io.InputStream;
import java.io.Reader;

import org.drools.Service;
import org.drools.builder.DecisionTableConfiguration;

public interface DecisionTableProvider extends Service {

    String loadFromInputStream(InputStream is,
                               DecisionTableConfiguration configuration);


}
