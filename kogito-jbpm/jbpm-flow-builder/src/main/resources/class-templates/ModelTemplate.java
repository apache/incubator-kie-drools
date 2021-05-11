/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.codegen;

import java.util.Map;
import java.util.HashMap;

import org.kie.kogito.MappableToModel;
import org.kie.kogito.Model;

public class XXXModel implements Model,
                                 MappableToModel<$modelClass$> {
    
    private String id;

    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }

    @Override
    public Map<String, Object> toMap() {
        
    }

    @Override
    public XXXModel fromMap(Map<String, Object> params) {
        return fromMap(null, params);
    }

    @Override
    public void update(Map<String, Object> params) {
        fromMap(getId(), params);
    }

    public XXXModel fromMap(String id, Map<String, Object> params) {
        
    }
}