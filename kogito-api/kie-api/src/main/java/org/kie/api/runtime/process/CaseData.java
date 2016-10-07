/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.process;

import java.util.Map;

/**
 * Top level of Case(File)Data that holds all shared information within a case.
 *
 */
public interface CaseData {

    /**
     * Returns all available case data for given case.
     * @return
     */
    Map<String, Object> getData();
    
    /**
     * Returns case data for given case registered under given name.
     * @return
     */
    Object getData(String name);
    
    
    /**
     * Add single data item into existing case file
     * (replaces already existing data that matches with input)
     * @param name
     * @param data
     */
    void add(String name, Object data);
    
    /**
     * Remove permanently given data from existing case file
     * @param name
     */
    void remove(String name);
}
