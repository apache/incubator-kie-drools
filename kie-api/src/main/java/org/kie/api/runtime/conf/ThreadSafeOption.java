/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.conf;

/**
 * An option to define if the KieSession should should be thread safe or not.
 * By default a KieSession is thread-safe. Flagging it as non-thread-sage will bring a performance improvement
 * at the cost of being no longer able to safely use the KieSession in a multithreaded environment.
 *
 * drools.threadSafe = &lt;true|false&gt;
 *
 * DEFAULT = true
 */
public enum ThreadSafeOption implements SingleValueKieSessionOption {

    YES(true),
    NO(false);

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the thread safety configuration
     */
    public static final String PROPERTY_NAME = "drools.threadSafe";

    private final boolean threadSafe;

    /**
     * Private constructor to enforce the use of the factory method
     * @param threadSafe
     */
    ThreadSafeOption( final boolean threadSafe ) {
        this.threadSafe = threadSafe;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isThreadSafe() {
        return threadSafe;
    }

}