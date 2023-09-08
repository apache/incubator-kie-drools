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
package org.kie.api.builder;

/**
 * A KieModule is a container of all the resources necessary to define a set of KieBases like
 * a pom.xml defining its ReleaseId, a kmodule.xml file declaring the KieBases names and configurations
 * together with all the KieSession that can be created from them and all the other files
 * necessary to build the KieBases themselves
 */
public interface KieModule {

    /**
     * Returns the ReleaseId identifying this KieModule
     */
    ReleaseId getReleaseId();
}
