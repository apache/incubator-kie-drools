package org.drools.compiler;

import java.io.InputStream;
import java.io.Reader;

import org.kie.Service;
import org.kie.builder.DecisionTableConfiguration;

public interface DecisionTableProvider extends Service {

    String loadFromInputStream(InputStream is,
                               DecisionTableConfiguration configuration);


}
