package org.drools.compiler.kproject.models;

import org.kie.api.builder.model.FileLoggerModel;

public class FileLoggerModelImpl implements FileLoggerModel {

    private String file;
    private boolean threaded = false;
    private int interval = 30;

    public FileLoggerModelImpl() { }

    public FileLoggerModelImpl(String file) {
        this.file = file;
    }

    public FileLoggerModelImpl(String file, int interval, boolean threaded) {
        this.file = file;
        this.interval = interval;
        this.threaded = threaded;
    }

    public String getFile() {
        return file;
    }

    public FileLoggerModel setFile(String file) {
        this.file = file;
        return this;
    }

    public boolean isThreaded() {
        return threaded;
    }

    public FileLoggerModel setThreaded(boolean threaded) {
        this.threaded = threaded;
        return this;
    }

    public int getInterval() {
        return interval;
    }

    public FileLoggerModel setInterval(int interval) {
        this.interval = interval;
        return this;
    }
}
