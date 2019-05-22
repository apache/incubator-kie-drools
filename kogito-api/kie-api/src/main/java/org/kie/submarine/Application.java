/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.submarine;

import org.kie.submarine.process.Processes;

/**
 * Entry point for accessing business automation components
 * such as processes, rules, decisions, etc.
 * 
 * It should be considered as singleton kind of object that can be safely
 * used across entire application.
 *
 */
public interface Application {
    
    /**
     * Returns configuration of the application
     * @return current configuration
     */
    Config config();

    /**
     * Returns processes found in the application otherwise null
     * @return processes information or null of non found
     */
    default Processes processes() {
        return null;
    }
}
