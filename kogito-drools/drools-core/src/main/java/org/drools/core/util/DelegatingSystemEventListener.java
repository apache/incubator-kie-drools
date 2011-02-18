/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import org.drools.SystemEventListener;

public class DelegatingSystemEventListener
        implements
        SystemEventListener {

    private SystemEventListener listener;

    public DelegatingSystemEventListener(SystemEventListener listener) {
        this.listener = listener;
    }

    public void setSystemEventListener(SystemEventListener listener) {
        this.listener = listener;
    }

    public void debug(String message) {
        this.listener.debug(message);
    }

    public void debug(String message,
                      Object object) {
        this.listener.debug(message, object);
    }

    public void exception(String message, Throwable e) {
        this.listener.exception(message, e);
    }

    public void exception(Throwable e) {
        this.listener.exception(e);
    }

    public void info(String message) {
        this.listener.info(message);
    }

    public void info(String message,
                     Object object) {
        this.listener.info(message, object);
    }

    public void warning(String message) {
        this.listener.warning(message);
    }

    public void warning(String message,
                        Object object) {
        this.listener.warning(message, object);
    }

}
