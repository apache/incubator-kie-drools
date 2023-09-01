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
package org.drools.base.rule.accessor;

/**
 * This interface is used by semantic modules that are compiled
 * to bytecode. 
 */
public interface CompiledInvoker
    extends
    Invoker {

    /**
     * Generated code should be able to return a String which represents the bytecode.
     * The elements in the list will be used to compare one semantic invoker
     * with another by making sure each item in the list is equivalent (equals()).
     * There are utilities in the ASM package to retrieve the bytecode for this.
     */
    String getMethodBytecode();

    static boolean isCompiledInvoker(Invoker invoker) {
        return invoker instanceof CompiledInvoker;
    }
}
