/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools;

/**
 * <p>
 * This interface is used to provide callback style logging for the system events.
 * </p>
 * 
 * <p>
 * The SystemEventListenerFactory is used to provide the default SystemEventListener to various Drools components
 * such as the KnowledgeAgent, ResourceChangeNotifier and ResourceChangeListener. Although many of these components
 * allow the used listener to be overriden with a setSystemEventListener(SystemEventListener) method.
 * </p>
 */
public interface SystemEventListener {
    /**
     * For general info messages
     */
    public void info(String message);

    public void info(String message,
                     Object object);

    /**
     * For a warning (useful when tracking down problems).
     */
    public void warning(String message);

    public void warning(String message,
                        Object object);

    /**
     * An exception occurred.
     */
    public void exception(String message, Throwable e);

    public void exception(Throwable e);

    /**
     * These should not be logged, just shown if needed.
     */
    public void debug(String message);

    public void debug(String message,
                      Object object);
}
