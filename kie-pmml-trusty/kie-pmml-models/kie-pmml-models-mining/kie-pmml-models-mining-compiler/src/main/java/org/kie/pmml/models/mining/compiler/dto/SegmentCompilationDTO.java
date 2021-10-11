/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.mining.compiler.dto;

import java.util.List;

import org.dmg.pmml.Field;
import org.dmg.pmml.Model;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.mining.Segment;
import org.kie.pmml.compiler.api.dto.CompilationDTO;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

public class SegmentCompilationDTO<T extends Model> extends CompilationDTO<T> {

    private static final long serialVersionUID = 747528700144401388L;

    private final Segment segment;
    private final String segmentModelPackageName;

    public SegmentCompilationDTO(MiningModelCompilationDTO source, Segment segment, List<Field<?>> fields) {
        super(source.getPmml(), (T) segment.getModel(), source.getHasClassloader(),
              getSanitizedPackageName(source.getSegmentationPackageName() + "." + segment.getId()),
              fields);
        this.segment = segment;
        segmentModelPackageName = getSanitizedPackageName(packageName);
    }

    public Segment getSegment() {
        return segment;
    }

    /**
     * Returns the <b>sanitized</b> package name
     * @return
     */
    @Override
    public String getPackageName() {
        return segmentModelPackageName;
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
