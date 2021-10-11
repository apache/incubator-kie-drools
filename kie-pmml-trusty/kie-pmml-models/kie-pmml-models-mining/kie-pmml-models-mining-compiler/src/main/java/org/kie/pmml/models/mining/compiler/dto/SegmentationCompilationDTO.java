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

import org.dmg.pmml.Model;
import org.dmg.pmml.mining.Segmentation;
import org.kie.pmml.compiler.api.dto.CompilationDTO;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

public class SegmentationCompilationDTO<T extends Model> extends CompilationDTO<T> {

    private static final long serialVersionUID = 747528700144401388L;

    private final Segmentation segmentation;
    private final String segmentModelPackageName;

    public SegmentationCompilationDTO(CompilationDTO source, Segmentation segmentation) {
        super(source.getPmml(), (T) source.getModel(), source.getHasClassloader(),
              getSanitizedPackageName(source.getPackageName() + "." + segmentation.toString()));
        this.segmentation = segmentation;
        segmentModelPackageName = getSanitizedPackageName(packageName);
    }
}
