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
package org.kie.pmml.models.mining.compiler.dto;

import java.util.List;

import org.dmg.pmml.Field;
import org.dmg.pmml.Model;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.mining.Segment;
import org.kie.pmml.compiler.commons.dto.AbstractSpecificCompilationDTO;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

public class SegmentCompilationDTO<T extends Model> extends AbstractSpecificCompilationDTO<T> {

    private static final long serialVersionUID = 747528700144401388L;

    private final Segment segment;

    /**
     * Private constructor that generates a <code>CommonCompilationDTO</code> creating <b>packageName</b> from
     * source' <b>segmentationPackageName</b> and segment'
     * <b>id</b>, preserving given <b>fields</b>
     * <code>CompilationDTO</code>
     *
     * @param source
     * @param segment
     * @param fields
     */
    private SegmentCompilationDTO(MiningModelCompilationDTO source, Segment segment, List<Field<?>> fields) {
        super(source.getPmml(), (T) segment.getModel(), source.getPmmlContext(), source.getFileName(),
              getSanitizedPackageName(source.getSegmentationPackageName() + "." + segment.getId()),
              fields);
        this.segment = segment;
    }

    /**
     * Builder that generates a <code>CommonCompilationDTO</code> creating <b>packageName</b> from source'
     * <b>segmentationPackageName</b> and segment' <b>id</b>,
     * preserving given <b>fields</b>
     * <code>CompilationDTO</code>
     *
     * @param source
     * @param segment
     * @param fields
     */
    public static SegmentCompilationDTO fromGeneratedPackageNameAndFields(MiningModelCompilationDTO source,
                                                                          Segment segment, List<Field<?>> fields) {
        return new SegmentCompilationDTO(source, segment, fields);
    }

    public Segment getSegment() {
        return segment;
    }

    public String getId() {
        return segment.getId();
    }

    public Number getWeight() {
        return segment.getWeight();
    }

    public Predicate getPredicate() {
        return segment.getPredicate();
    }
}
