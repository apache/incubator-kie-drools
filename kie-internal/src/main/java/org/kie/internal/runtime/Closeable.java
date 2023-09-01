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
package org.kie.internal.runtime;

/**
 * Allows various components (e.g. work item handlers, event listeners) to be closed when
 * owning component (ksession) is being closed/disposed.
 * This interface marks an component that is lightweight and it's safe and wise (from performance
 * point of view) to be frequently recreated.
 *
 * @see Cacheable is an alternative that allows to keep single instnace te be cached and reused to avoid recreation
 */
public interface Closeable {

    /**
     * Closes the underlying resources
     */
    void close();
}
