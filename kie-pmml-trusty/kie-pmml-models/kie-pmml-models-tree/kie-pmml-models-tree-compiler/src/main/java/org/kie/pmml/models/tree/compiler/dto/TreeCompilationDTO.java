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
package org.kie.pmml.models.tree.compiler.dto;

import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.compiler.commons.dto.AbstractSpecificCompilationDTO;
import org.kie.pmml.compiler.api.dto.CompilationDTO;

public class TreeCompilationDTO extends AbstractSpecificCompilationDTO<TreeModel> {

    private static final long serialVersionUID = 6829515292921161468L;

    private final Double missingValuePenalty;
    private final Node node;

    /**
     * Private constructor that use given <code>CommonCompilationDTO</code>
     * @param source
     */
    private TreeCompilationDTO(final CompilationDTO<TreeModel> source) {
        super(source);
        missingValuePenalty = source.getModel().getMissingValuePenalty() != null ?
                source.getModel().getMissingValuePenalty().doubleValue() : null;
        node = source.getModel().getNode();
    }

    /**
     * Builder that use given <code>CommonCompilationDTO</code>
     * @param source
     */
    public static TreeCompilationDTO fromCompilationDTO(final CompilationDTO<TreeModel> source) {
        return new TreeCompilationDTO(source);
    }

    public Double getMissingValuePenalty() {
        return missingValuePenalty;
    }

    public Node getNode() {
        return node;
    }
}
