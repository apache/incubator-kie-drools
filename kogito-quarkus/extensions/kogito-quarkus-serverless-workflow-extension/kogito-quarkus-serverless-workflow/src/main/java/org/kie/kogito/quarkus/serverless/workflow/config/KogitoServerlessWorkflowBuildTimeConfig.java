/*
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
package org.kie.kogito.quarkus.serverless.workflow.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigGroup
public interface KogitoServerlessWorkflowBuildTimeConfig {

    /**
     * Strategy for generating the configuration key of open API specifications.<br>
     * Possible values are:
     * <UL>
     * <LI>file_name. Uses the last element of the spec uri</LI>
     * <LI>full_uri. Uses the full path of the uri</LI>
     * <LI>spec_title. Uses the spec title</LI>
     * <LI>function_name. Uses the function name</LI>
     * </UL>
     */
    @WithName("operationIdStrategy")
    @WithDefault("file_name")
    String operationIdStrategy();

    /**
     * Variable name for foreach loop
     */
    @WithName("states.foreach.outputVarName")
    @WithDefault("_swf_eval_temp")
    String forEachOutputVarName();

}
