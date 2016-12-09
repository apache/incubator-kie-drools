/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.api.runtime;

import org.kie.api.KieBase;

public interface RequestContext extends Context {
    Object getResult();
    void setResult(Object result);

    RequestContext with(KieBase kieBase);
    RequestContext with(KieSession kieSession);

    Context getConversationContext();

    Context getApplicationContext();

    static RequestContext create() {
        return create(RequestContext.class.getClassLoader());
    }
    
    static RequestContext create(ClassLoader classLoader) {
        try {
            return (RequestContext) Class.forName( "org.drools.core.command.RequestContextImpl", true, classLoader ).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance RequestContext", e);
        }
    }
}
