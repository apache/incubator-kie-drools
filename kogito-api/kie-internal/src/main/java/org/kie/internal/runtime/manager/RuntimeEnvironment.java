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
package org.kie.internal.runtime.manager;


/**
 * Extension of <code>org.kie.api.runtime.manager.RuntimeEnvironment</code> that contains internal methods
 */
public interface RuntimeEnvironment extends org.kie.api.runtime.manager.RuntimeEnvironment {

    /**
     * Delivers concrete implementation of <code>Mapper</code> that provides access to mapping between contexts and
     * ksession identifiers for tracking purposes.
     * @return
     */
    Mapper getMapper();

}
