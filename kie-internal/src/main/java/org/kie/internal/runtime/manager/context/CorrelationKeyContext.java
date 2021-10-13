/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.runtime.manager.context;

import org.kie.api.runtime.manager.Context;
import org.kie.internal.process.CorrelationKey;

/**
 * Context implementation to deliver capabilities to find proper <code>RuntimeEngine</code>
 * instances based on correlation key instead of process instance id. Use by strategy:
 * <ul>
 *  <li>PerProcessInstance</li>
 * </ul>
 * To obtain instances of this context use one of the following static methods:
 * <ul>
 *  <li><code>get()</code> to get empty context when starting process instances</li>
 *  <li><code>get(CorrelationKey)</code> to get context for specific process instance</li>
 * </ul>
 *
 */
public class CorrelationKeyContext implements Context<CorrelationKey> {

    private CorrelationKey correlationKey;

    public CorrelationKeyContext(CorrelationKey key) {
        this.correlationKey = key;
    }

    @Override
    public CorrelationKey getContextId() {

        return correlationKey;
    }

    /**
     * Returns new instance of <code>CorrelationKeyContext</code> without correlation key.
     * Used for starting new instances of the process.
     * @return
     */
    public static CorrelationKeyContext get() {
        return new CorrelationKeyContext(null);
    }

    /**
     * Returns new instance of <code>CorrelationKeyContext</code> with correlation key of already existing process instance
     * @param key actual correlation key of process instance
     * @return
     */
    public static CorrelationKeyContext get(CorrelationKey key) {
        return new CorrelationKeyContext(key);
    }
}
