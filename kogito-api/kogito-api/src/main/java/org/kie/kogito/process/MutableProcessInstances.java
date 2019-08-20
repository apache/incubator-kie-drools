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

package org.kie.kogito.process;

public interface MutableProcessInstances<T> extends ProcessInstances<T> {
    
    void update(String id, ProcessInstance<T> instance);
    
    void remove(String id);
    
    default boolean isActive(ProcessInstance<T> instance) {
        return instance.status() == ProcessInstance.STATE_ACTIVE || instance.status() == ProcessInstance.STATE_ERROR;        
    }
}
