/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.builder.conf;

import org.kie.api.conf.SingleValueKieBaseOption;

/**
 * An Enum for SessionCacheOption option.
 *
 * drools.sessionCache = &lt;none|sync|async&gt;
 *
 * DEFAULT = none
 *
 * If this option is enabled caches disposed sessions and recycles them
 * when a new session is required. In order to reuse a session it has to be
 * resetted first, this resetting process can be synchronous or asynchronous.
 * This option is valid only when using the phreak rule engine (default).
 *
 * This option is new to Drools 6.1. Before 6.1, Drools would never cache sessions.
 */
public enum SessionCacheOption implements SingleValueKnowledgeBuilderOption, SingleValueKieBaseOption {

    NONE("none"),
    SYNC("sync"),
    ASYNC("async");

    /**
     * The property name for the process string escapes option
     */
    public static final String PROPERTY_NAME = "drools.sessionCache";

    private String value;

    SessionCacheOption( final String value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public static SessionCacheOption determineOption( String val ) {
        if (SYNC.value.equalsIgnoreCase(val)) {
            return SYNC;
        }
        if (ASYNC.value.equalsIgnoreCase(val)) {
            return ASYNC;
        }
        return NONE;
    }

    public boolean isEnabled() {
        return this != NONE;
    }

    public boolean isAsync() {
        return this == ASYNC;
    }
}
