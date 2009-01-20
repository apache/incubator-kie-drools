package org.drools.logger;

import org.drools.ProviderInitializationException;
import org.drools.event.KnowledgeRuntimeEventManager;

/**
 * <p>
 * The KnowledgeRuntimeLogger uses the comprehensive event system in Drools to create an audit log that can be used
 * log the execution of drools for later inspection, in tools such as the Eclipse audit viewer.
 * </p>
 * 
 * <pre>
 * KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "logdir/mylogfile");
 * ....
 * logger.close();
 * </pre>
 * 
 * <p>
 * Don't forget to close the logger when it is no longer needed, so resources can be released.
 * </p>
 *
 */
public class KnowledgeRuntimeLoggerFactory {

    private static KnowledgeRuntimeLoggerProvider knowledgeRuntimeLoggerProvider;

    /**
     * Creates a file logger in the current thread and events are written as they happen.
     * 
     * @param session
     * @param fileName
     * @return
     */
    public static KnowledgeRuntimeLogger newFileLogger(KnowledgeRuntimeEventManager session,
                                                       String fileName) {
        return getKnowledgeRuntimeLoggerProvider().newFileLogger( session,
                                                                  fileName );
    }

    /**
     * Creates a file logger that executes in a different thread, where information is written on given intervals
     * 
     * @param session
     * @param fileName
     * @param interval
     * @return
     */
    public static KnowledgeRuntimeLogger newThreadedFileLogger(KnowledgeRuntimeEventManager session,
                                                               String fileName,
                                                               int interval) {
        return getKnowledgeRuntimeLoggerProvider().newThreadedFileLogger( session,
                                                                          fileName,
                                                                          interval );
    }

    /**
     * Logs events to command line console
     * 
     * @param session
     * @return
     */
    public static KnowledgeRuntimeLogger newConsoleLogger(KnowledgeRuntimeEventManager session) {
        return getKnowledgeRuntimeLoggerProvider().newConsoleLogger( session );
    }

    private static synchronized void setKnowledgeRuntimeLoggerProvider(KnowledgeRuntimeLoggerProvider provider) {
        KnowledgeRuntimeLoggerFactory.knowledgeRuntimeLoggerProvider = provider;
    }

    private static synchronized KnowledgeRuntimeLoggerProvider getKnowledgeRuntimeLoggerProvider() {
        if ( knowledgeRuntimeLoggerProvider == null ) {
            loadProvider();
        }
        return knowledgeRuntimeLoggerProvider;
    }

    @SuppressWarnings("unchecked")
    private static void loadProvider() {
        try {
            Class<KnowledgeRuntimeLoggerProvider> cls = (Class<KnowledgeRuntimeLoggerProvider>) Class.forName( "org.drools.audit.KnowledgeRuntimeLoggerProviderImpl" );
            setKnowledgeRuntimeLoggerProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new ProviderInitializationException( "Provider org.drools.audit.KnowledgeRuntimeLoggerProviderImpl could not be set.",
                                                       e );
        }
    }

}
