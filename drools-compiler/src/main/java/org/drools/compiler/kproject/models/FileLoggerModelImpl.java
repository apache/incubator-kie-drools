/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
