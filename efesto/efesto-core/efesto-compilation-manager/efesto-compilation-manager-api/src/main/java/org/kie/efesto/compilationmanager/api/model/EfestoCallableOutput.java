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
package org.kie.efesto.compilationmanager.api.model;

import java.util.List;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * A <code>CompilationOutput</code>.
 *
 * It will be translated to a <code>GeneratedExecutableResource</code>,
 * that has a specif json-format and semantic.
 */
public interface EfestoCallableOutput extends EfestoCompilationOutput {

    /**
     * Returns the <b>full resource identifier</b> to be invoked for execution
     * @return
     */
    ModelLocalUriId getModelLocalUriId();

    /**
     * Returns the <b>full class names</b> to be instantiated for execution
     *
     * @return
     */
    List<String> getFullClassNames();
}
