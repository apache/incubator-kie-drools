package org.drools.logger;

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
     * Creates a file logger in the current thread. The file is in XML format, suitable for interpretation by Eclipse's Drools Audit View
     * or other tools. Note that while events are written as they happen, the file will not be flushed until it is closed or the underlying
     * file buffer is filled. If you need real time logging then use a Console Logger or a Threaded File Logger.
     *
     * @param session
     * @param fileName - .log is appended to this.
     * @return
     */
    public static KnowledgeRuntimeLogger newFileLogger(KnowledgeRuntimeEventManager session,
                                                       String fileName) {
        return getKnowledgeRuntimeLoggerProvider().newFileLogger( session,
                                                                  fileName );
    }

    /**
     * Creates a file logger that executes in a different thread, where information is written on given intervals (in milliseconds).
     * The file is in XML format, suitable for interpretation by Eclipse's Drools Audit View or other tools.
     *
     * @param session
     * @param fileName - .log is appended to this.
     * @param interval - in milliseconds.
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
     * Logs events to command line console. This is not in XML format, so it cannot be parsed
     * by other tools, but is in real time and is more human readable.
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
            throw new RuntimeException( "Provider org.drools.audit.KnowledgeRuntimeLoggerProviderImpl could not be set.",
                                                       e );
        }
    }

}
