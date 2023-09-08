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
package org.kie.pmml.models.clustering.compiler.dto;

import org.dmg.pmml.clustering.ClusteringModel;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.dto.AbstractSpecificCompilationDTO;

public class ClusteringCompilationDTO extends AbstractSpecificCompilationDTO<ClusteringModel> {

    private static final long serialVersionUID = -5903743905468597652L;

    /**
     * Private constructor
     * @param source
     */
    private ClusteringCompilationDTO(final CompilationDTO<ClusteringModel> source) {
        super(source);
    }

    /**
     * Default builder
     * @param source
     * @return
     */
    public static ClusteringCompilationDTO fromCompilationDTO(final CompilationDTO<ClusteringModel> source) {
        return new ClusteringCompilationDTO(source);
    }
}
