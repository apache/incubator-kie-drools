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

package org.kie.api.runtime.rule;


/**
 * This interface is used as part of the {@link org.kie.api.runtime.StatelessKieSession}, which is the interface
 * returned from the {@link org.kie.api.KieBase}.
 *
 * Please see {@link org.kie.api.runtime.StatelessKieSession} for more details on how to use this api.
 *
 * @see org.kie.api.runtime.StatelessKieSession
 */
public interface StatelessRuleSession {

    /**
     * Execute a StatelessKieSession inserting just a single object. If a collection (or any other Iterable) or an array is used here, it will be inserted as-is,
     * It will not be iterated and its internal elements inserted.
     *
     * @param object
     */
    void execute(Object object);

    /**
     * Execute a StatelessKieSession, iterate the Iterable inserting each of its elements. If you have an array, use the Arrays.asList(...) method
     * to make that array Iterable.
     * @param objects
     */
    void execute(Iterable objects);

}
