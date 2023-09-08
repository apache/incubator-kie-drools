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
package org.kie.efesto.runtimemanager.api.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EfestoMapInputDTO implements Serializable {


    private static final long serialVersionUID = 8616386112525557777L;

    private final List<Object> inserts;
    private final Map<String, Object> globals;

    private final Map<String, Object> unwrappedInputParams;
    private final Map<String, EfestoOriginalTypeGeneratedType> fieldTypeMap;

    private final String modelName;
    private final String packageName;

    public EfestoMapInputDTO(final List<Object> inserts,
                          final Map<String, Object> globals,
                          final Map<String, Object> unwrappedInputParams,
                          final Map<String, EfestoOriginalTypeGeneratedType> fieldTypeMap,
                          final String modelName,
                          final String packageName) {
        this.inserts = inserts;
        this.globals = globals;
        this.unwrappedInputParams = unwrappedInputParams;
        this.fieldTypeMap = fieldTypeMap;
        this.modelName = modelName;
        this.packageName = packageName;
    }

    public List<Object> getInserts() {
        return Collections.unmodifiableList(inserts);
    }

    public Map<String, Object> getGlobals() {
        return Collections.unmodifiableMap(globals);
    }

    public Map<String, Object> getUnwrappedInputParams() {
        return Collections.unmodifiableMap(unwrappedInputParams);
    }

    public Map<String, EfestoOriginalTypeGeneratedType> getFieldTypeMap() {
        return Collections.unmodifiableMap(fieldTypeMap);
    }

    public String getModelName() {
        return modelName;
    }

    public String getPackageName() {
        return packageName;
    }
}
