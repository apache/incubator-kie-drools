package org.drools.compiler.compiler;

import java.io.InputStream;

import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.utils.ServiceRegistryImpl;

public class ScoreCardFactory {
    private static ScoreCardProvider provider;

    public static String loadFromInputStream(InputStream is, ScoreCardConfiguration configuration) {
        return getScoreCardProvider().loadFromInputStream( is, configuration );
    }
    
    public static synchronized void setScoreCardProvider(ScoreCardProvider provider) {
        ScoreCardFactory.provider = provider;
    }
    
    public static synchronized ScoreCardProvider getScoreCardProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }
    
    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( ScoreCardProvider.class,  "org.drools.scorecards.ScoreCardProviderImpl" );
        setScoreCardProvider(ServiceRegistryImpl.getInstance().get( ScoreCardProvider.class ) );
    }
}
