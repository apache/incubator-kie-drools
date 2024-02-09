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
package org.kie.api.fluent;

import java.util.Collection;
import java.util.Map;

import org.kie.api.definition.process.Process;

/**
 * Builder that contains methods to create a process definition. 
 * @see ProcessBuilderFactory
 */
public interface ProcessBuilder extends NodeContainerBuilder<ProcessBuilder, ProcessBuilder> {

    ProcessBuilder dynamic(boolean dynamic);

    ProcessBuilder version(String version);

    ProcessBuilder packageName(String packageName);

    ProcessBuilder imports(Collection<String> imports);

    ProcessBuilder functionImports(Collection<String> functionImports);

    ProcessBuilder globals(Map<String, String> globals);

    ProcessBuilder global(String name, String type);
    
    ProcessBuilder swimlane(String name);

    /**
     * Validates and returns process definition.<br>
     * Should be invoked after all other method calls.
     * @return validated process definition
     */
    Process build();
}
