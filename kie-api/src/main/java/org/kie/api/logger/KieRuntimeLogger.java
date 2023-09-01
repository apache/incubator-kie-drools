package org.kie.api.logger;

/**
 * A logger for audit events.
 */
public interface KieRuntimeLogger {

    /**
     * Release any resources allocated within the logger such as file
     * handles, network connections, etc.
     * It is a programming error to append to a closed appender.
     */
    void close();

}
