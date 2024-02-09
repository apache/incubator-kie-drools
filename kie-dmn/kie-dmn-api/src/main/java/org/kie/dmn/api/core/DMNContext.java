/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.api.core;

import java.util.Map;
import java.util.Optional;

public interface DMNContext
        extends Cloneable {

    Object set(String name, Object value);

    Object get(String name);

    Map<String, Object> getAll();

    boolean isDefined(String name);

    DMNMetadata getMetadata();

    DMNContext clone();

    /**
     * Walks inside the current scope for the identifier `name`, using the supplied `namespace`, and push that as the new current scope.
     * @param name
     */
    void pushScope(String name, String namespace);

    /**
     * The current scope is pop-ed from the current scope stack.
     */
    void popScope();

    /**
     * Returns the current namespace currently at the top of the scope stack, empty if the stack is empty.
     */
    Optional<String> scopeNamespace();

}
