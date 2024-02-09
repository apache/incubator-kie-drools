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
package org.kie.pmml.models.mining.compiler.factories;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import jakarta.xml.bind.JAXBException;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Field;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.drools.util.FileUtils;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getDerivedFields;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getFieldsFromDataDictionaryAndTransformationDictionary;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getTargetFieldName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLUtil.SEGMENTID_TEMPLATE;
import static org.kie.pmml.models.mining.compiler.dto.MiningModelCompilationDTO.SEGMENTATIONNAME_TEMPLATE;

public abstract class AbstractKiePMMLFactoryTest {

    protected static final String SOURCE_MIXED = "MiningModel_Mixed.pmml";
    protected static DataDictionary DATA_DICTIONARY;
    protected static TransformationDictionary TRANSFORMATION_DICTIONARY;
    protected static PMML pmml;
    protected static MiningModel MINING_MODEL;
    protected static List<DerivedField> DERIVED_FIELDS;
    protected static String targetFieldName;

    protected static void innerSetup() throws JAXBException, SAXException, IOException {
        FileInputStream fis = FileUtils.getFileInputStream(SOURCE_MIXED);
        pmml = KiePMMLUtil.load(fis, SOURCE_MIXED);
        assertThat(pmml).isNotNull();
        DATA_DICTIONARY = pmml.getDataDictionary();
        assertThat(DATA_DICTIONARY).isNotNull();
        TRANSFORMATION_DICTIONARY = pmml.getTransformationDictionary();
        assertThat(pmml.getModels().get(0)).isInstanceOf(MiningModel.class);
        MINING_MODEL = (MiningModel) pmml.getModels().get(0);
        assertThat(MINING_MODEL).isNotNull();
        populateMissingIds(MINING_MODEL);
        DERIVED_FIELDS = getDerivedFields(TRANSFORMATION_DICTIONARY,
                                          MINING_MODEL.getLocalTransformations());
        List<Field<?>> fields =
                getFieldsFromDataDictionaryAndTransformationDictionary(DATA_DICTIONARY, TRANSFORMATION_DICTIONARY);
        targetFieldName = getTargetFieldName(fields, MINING_MODEL).get();
    }

    protected String getExpectedNestedModelClass(final Segment segment) {
        final String basePackage = getSanitizedPackageName(String.format(PACKAGE_CLASS_TEMPLATE, PACKAGE_NAME,
                                                                         MINING_MODEL.getModelName()));
        final String segmentationName = String.format(SEGMENTATIONNAME_TEMPLATE, MINING_MODEL.getModelName());
        final String segmentationPackageName = getSanitizedPackageName(basePackage + "." + segmentationName);
        final String segmentModelPackageName = getSanitizedPackageName(segmentationPackageName + "." + segment.getId());
        final String simpleClassName = getSanitizedClassName(segment.getModel().getModelName());
        return String.format(PACKAGE_CLASS_TEMPLATE, segmentModelPackageName, simpleClassName);
    }

    /**
     * Recursively populate <code>Segment</code>s with auto generated id
     * if missing in original model
     */
    private static void populateMissingIds(final MiningModel model) {
        final List<Segment> segments = model.getSegmentation().getSegments();
        for (int i = 0; i < segments.size(); i++) {
            Segment segment = segments.get(i);
            if (segment.getId() == null || segment.getId().isEmpty()) {
                String toSet = String.format(SEGMENTID_TEMPLATE, model.getModelName(), i);
                segment.setId(toSet);
                if (segment.getModel() instanceof MiningModel) {
                    populateMissingIds((MiningModel) segment.getModel());
                }
            }
        }
    }
}