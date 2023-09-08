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
package org.kie.pmml.models.drools.dto;

import java.util.Map;

import org.dmg.pmml.Model;
import org.kie.pmml.compiler.commons.dto.AbstractSpecificCompilationDTO;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

public class DroolsCompilationDTO<T extends Model> extends AbstractSpecificCompilationDTO<T> {

    private static final long serialVersionUID = 3279343826083191443L;
    private final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

    /**
     * Private constructor that use given <code>CommonCompilationDTO</code>
     * @param source
     * @param fieldTypeMap
     */
    private DroolsCompilationDTO(final CompilationDTO<T> source,
                                 final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        super(source);
        this.fieldTypeMap = fieldTypeMap;
    }

    /**
     * Builder that use given <code>CommonCompilationDTO</code>
     * @param source
     * @param fieldTypeMap
     */
    public static <T extends Model> DroolsCompilationDTO<T> fromCompilationDTO(final CompilationDTO<T> source,
                                                                               final Map<String,
                                                                                       KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return new DroolsCompilationDTO(source, fieldTypeMap);
    }

    public Map<String, KiePMMLOriginalTypeGeneratedType> getFieldTypeMap() {
        return fieldTypeMap;
    }
}
