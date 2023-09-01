package org.drools.drl.extensions;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.internal.builder.DecisionTableConfiguration;

public interface DecisionTableProvider extends KieService {

    String loadFromResource(Resource resource,
                            DecisionTableConfiguration configuration);

    List<String> loadFromInputStreamWithTemplates(Resource resource,
                                                  DecisionTableConfiguration configuration);

    Map<String,List<String[]>> loadPropertiesFromFile(File file, DecisionTableConfiguration configuration);

    Map<String,List<String[]>> loadPropertiesFromInputStream(InputStream inputStream, DecisionTableConfiguration configuration);
}
