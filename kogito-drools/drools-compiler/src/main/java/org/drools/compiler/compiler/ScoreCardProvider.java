package org.drools.compiler.compiler;

import java.io.InputStream;

import org.kie.api.Service;
import org.kie.internal.builder.ScoreCardConfiguration;

public interface ScoreCardProvider extends Service {

    String loadFromInputStream( InputStream is,
                                ScoreCardConfiguration configuration );


}
