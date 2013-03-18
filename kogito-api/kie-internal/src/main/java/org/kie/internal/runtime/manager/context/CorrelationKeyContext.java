/*
 * Copyright 2013 JBoss Inc
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

import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.manager.Context;

public class CorrelationKeyContext implements Context<CorrelationKey> {

    private CorrelationKey correlationKey;
    
    public CorrelationKeyContext(CorrelationKey key) {
        this.correlationKey = key;
    }
    
    @Override
    public CorrelationKey getContextId() {

        return correlationKey;
    }

    public static CorrelationKeyContext get() {
        return new CorrelationKeyContext(null);
    }
    
    public static CorrelationKeyContext get(CorrelationKey key) {
        return new CorrelationKeyContext(key);
    }
}
