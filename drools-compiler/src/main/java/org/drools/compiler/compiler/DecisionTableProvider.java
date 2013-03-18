package org.drools.compiler.compiler;

import java.io.InputStream;

import org.kie.Service;
import org.kie.internal.builder.DecisionTableConfiguration;

public interface DecisionTableProvider extends Service {

    String loadFromInputStream(InputStream is,
                               DecisionTableConfiguration configuration);


}
