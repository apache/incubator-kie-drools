package org.kie.api.builder.model;

/**
 * FileLoggerModel is a model allowing to programmatically define a FileLogger and wire it to a KieSession
 */
public interface FileLoggerModel {
    String getFile();
    boolean isThreaded();
    int getInterval();
}
