package org.drools.drl.extensions;

import java.io.InputStream;
import java.util.List;

import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.io.ResourceFactory;

public class DecisionTableFactory {

    private static DecisionTableProvider provider = KieService.load(DecisionTableProvider.class);
    
    public static String loadFromInputStream(InputStream is, DecisionTableConfiguration configuration ) {
        return loadFromResource(ResourceFactory.newInputStreamResource( is ), configuration);
    }

    public static String loadFromResource(Resource resource, DecisionTableConfiguration configuration) {
        return getDecisionTableProvider().loadFromResource( resource, configuration );
    }

    public static List<String> loadFromInputStreamWithTemplates(Resource resource, DecisionTableConfiguration configuration) {
        return getDecisionTableProvider().loadFromInputStreamWithTemplates( resource, configuration );
    }

    public static synchronized void setDecisionTableProvider(DecisionTableProvider provider) {
        DecisionTableFactory.provider = provider;
    }
    
    public static synchronized DecisionTableProvider getDecisionTableProvider() {
        return provider;
    }
}
