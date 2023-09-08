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

/**
 * Contains common operations for all nodes, basically naming, metadata and definition completion.
 * 
 * @param <T> concrete node instance
 * @param <P> container parent node
 */
public interface NodeBuilder<T extends NodeBuilder<T, P>, P extends NodeContainerBuilder<P, ?>> {

    /** 
     * Method to notify that definition of this node is done
     * @return container parent node 
    */
    P done();

    T name(String name);

    T setMetadata(String key, Object value);
}
