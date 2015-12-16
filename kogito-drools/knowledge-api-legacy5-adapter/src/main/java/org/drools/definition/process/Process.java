/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.definition.process;

import java.util.Map;

import org.drools.definition.KnowledgeDefinition;

/**
 * A Process represents one modular piece of business logic that can be executed by
 * a process engine.  Different types of processes may exist.
 * 
 */
public interface Process
    extends
    KnowledgeDefinition {

	/**
	 * The unique id of the Process.
	 * 
	 * @return the id
	 */
    String getId();

    /**
     * The name of the Process.
     * 
     * @return the name
     */
    String getName();

    /**
     * The version of the Process.
     * You may use your own versioning format
     * (as the version is not interpreted by the engine). 
     * 
     * @return the version
     */
    String getVersion();

    /**
     * The package name of this process.
     *
     * @return the package name
     */
    String getPackageName();

    /**
     * The type of process.
     * Different types of processes may exist.
     * This defaults to "RuleFlow".
     * 
     * @return the type
     */
    String getType();
    
	/**
	 * Meta data associated with this Node.
	 */
    Map<String, Object> getMetaData();

	/**
	 * Meta data associated with this Node.
	 */
    @Deprecated Object getMetaData(String name);

}
