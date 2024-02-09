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

import java.util.Collections;
import java.util.List;

import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.dmg.pmml.mining.Segmentation;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.dto.AbstractSpecificCompilationDTO;

import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

public class MiningModelCompilationDTO extends AbstractSpecificCompilationDTO<MiningModel> {

    public static final String SEGMENTATIONNAME_TEMPLATE = "%s_Segmentation";

    private static final long serialVersionUID = 3752088252358264261L;
    private final String segmentationName;
    private final String segmentationPackageName;
    private final String segmentationClassName;
    private final String segmentationCanonicalClassName;
    private final Segmentation segmentation;

    /**
     * Private constructor that use given <code>CommonCompilationDTO</code>
     *
     * @param source
     */
    private MiningModelCompilationDTO(final CompilationDTO<MiningModel> source) {
        super(source);
        segmentationName = String.format(SEGMENTATIONNAME_TEMPLATE, source.getModelName());
        segmentationPackageName = getSanitizedPackageName(getPackageName() + "." + segmentationName);
        segmentationClassName = getSanitizedClassName(segmentationName);
        segmentationCanonicalClassName = String.format(PACKAGE_CLASS_TEMPLATE, segmentationPackageName,
                                                       segmentationClassName);
        this.segmentation = source.getModel().getSegmentation();
    }

    /**
     * Builder that use given <code>CommonCompilationDTO</code>
     *
     * @param source
     */
    public static MiningModelCompilationDTO fromCompilationDTO(final CompilationDTO<MiningModel> source) {
        return new MiningModelCompilationDTO(source);
    }

    public String getSegmentationName() {
        return segmentationName;
    }

    public String getSegmentationPackageName() {
        return segmentationPackageName;
    }

    public String getSegmentationClassName() {
        return segmentationClassName;
    }

    public String getSegmentationCanonicalClassName() {
        return segmentationCanonicalClassName;
    }

    public Segmentation getSegmentation() {
        return segmentation;
    }

    public List<Segment> getSegments() {
        return segmentation != null ? segmentation.getSegments() : Collections.emptyList();
    }
}
