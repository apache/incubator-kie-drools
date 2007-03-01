package org.drools.ruleflow.common.core;
/*
 * Copyright 2005 JBoss Inc
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

/**
 * Represents a some process definition.
 * A process has a name and a unique id.
 * When a new version of a process is created, the name stays the same,
 * but the id and the version of the process should be different.
 * Different types of processes could be defined (like RuleFlow).
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface IProcess {

    /**
     * Sets the id of this process.
     * The id should uniquely identify this process.
     * @param id the id of the process
     */
    void setId(String id);
    /**
     * Returns the id of this process.
     * @return the id of this process
     */
    String getId();
    
    /**
     * Sets the name of this process.
     * @param name the name of this process
     */
    void setName(String name);
    /**
     * Returns the name of this process.
     * If no name is specified, null is returned.
     * @return the name of this process
     */
    String getName();
    
    /**
     * Sets the version of this process.
     * @param version the version of this process
     */
    void setVersion(String version);
    /**
     * Returns the version of this process.
     * If no version is specified, null is returned.
     * @return the version of this process
     */
    String getVersion();
    
    /**
     * Sets the type of this process.
     * @param type the type of this process
     */
    void setType(String type);
    /**
     * Returns the type of this process.
     * @return the type of this process
     */
    String getType();
    
}
