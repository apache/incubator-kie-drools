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
package org.kie.dmn.api.core.ast;

import java.util.Optional;

public interface DMNNode {

    String getId();

    String getName();

    String getModelNamespace();

    String getModelName();

    /**
     * Return the import name (short name alias) as described by this node's parent DMN Model, for the supplied namespace and model name.
     * @param ns the namespace of the imported model
     * @param iModelName the model name of the imported model
     * @return
     */
    default Optional<String> getModelImportAliasFor(String ns, String iModelName) {
        return Optional.empty();
    }
}
