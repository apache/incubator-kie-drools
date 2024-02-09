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

import org.kie.api.definition.process.Process;

/**
 * Factory to create process builder instance.<br>
 * It is also a convenient place holder for additional utility methods
 */
public interface ProcessBuilderFactory {

    /**
     * Returns a <code>ProcessBuilder</code> that can be used to create a process definition
     * @param processId the unique id of the process being defined
     * @return builder instance to create process definition
     */
    ProcessBuilder processBuilder(String processId);

    /**
     * Converts process definition into an array of bytes.<br>
     * Typically this array will be converted to a Resource for a KIE base
     * @param builder process definition to be converted to byte[]
     * @return byte[] containing process definition
     */
    byte[] toBytes(Process builder);
}
