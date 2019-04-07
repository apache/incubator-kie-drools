/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Process extends org.kie.api.definition.process.Process, ContextContainer {

    /**
     * Sets the id of this process.
     * The id should uniquely identify this process.
     * @param id the id of the process
     */
    void setId(String id);

    /**
     * Sets the name of this process.
     * @param name the name of this process
     */
    void setName(String name);

    /**
     * Sets the version of this process.
     * @param version the version of this process
     */
    void setVersion(String version);

    /**
     * Sets the type of this process.
     * @param type the type of this process
     */
    void setType(String type);

    /**
     * Sets the package name of this RuleFlow process
     * 
     * @param packageName   the package name of this RuleFlow process
     */
    void setPackageName(String packageName);
    
    void setMetaData(String name, Object value);

    /**
     * Returns the imports of this RuleFlow process.
     * They are defined as a List of fully qualified class names.
     * 
     * @return	the imports of this RuleFlow process
     */
    Set<String> getImports();
    
    /**
     * Sets the imports of this RuleFlow process
     * 
     * @param imports	the imports as a List of fully qualified class names
     */
    void setImports(Set<String> imports);
    
    /**
     * Returns the function imports of this RuleFlow process.
     * They are defined as a List of fully qualified class names.
     * 
     * @return  the function imports of this RuleFlow process
     */
     List<String> getFunctionImports();             


      /** 
       * Sets the imports of this RuleFlow process
       * 
       * @param functionImports   the imports as a List of fully qualified class names
       */
     void setFunctionImports(List<String> functionImports);      

    /**
     * Returns the globals of this RuleFlow process.
     * They are defined as a Map with the name as key and the type as value.
     * 
     * @return	the imports of this RuleFlow process
     */
    Map<String, String> getGlobals();
    
    /**
     * Sets the imports of this RuleFlow process
     * 
     * @param globals	the globals as a Map with the name as key and the type as value
     */
    void setGlobals(Map<String, String> globals);

    /**
     * Returns the names of the globals used in this RuleFlow process
     * 
     * @return	the names of the globals of this RuleFlow process
     */
    String[] getGlobalNames();
    
}
