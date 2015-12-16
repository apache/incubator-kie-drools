/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.datamodel.rule;

import java.util.Map;

/**
 * Implementations have parameters
 */
public interface HasParameterizedOperator
        extends
        HasOperator {

    /**
     * Clear all parameters
     */
    public void clearParameters();

    /**
     * Get a parameter
     * @param key
     * @return
     */
    public String getParameter( String key );

    /**
     * Set a parameter
     * @param key
     * @param parameter
     */
    public void setParameter( String key,
                              String parameter );

    /**
     * Delete a parameter
     * @param key
     */
    public void deleteParameter( String key );

    /**
     * Get all parameters
     * @return
     */
    public Map<String, String> getParameters();

    /**
     * Set all parameters
     * @param parameters
     */
    public void setParameters( Map<String, String> parameters );

}
