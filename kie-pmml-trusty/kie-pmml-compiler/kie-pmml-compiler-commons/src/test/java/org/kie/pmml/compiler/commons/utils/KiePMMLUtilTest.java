/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.compiler.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertNotNull;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLUtilTest {

    private static final String NO_MODELNAME_SAMPLE_NAME = "NoModelNameSample.pmml";
    private static final String NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME = "NoModelNameNoSegmentIdSample.pmml";

    @Test
    public void loadString() throws IOException, JAXBException, SAXException {
        commonLoadString(NO_MODELNAME_SAMPLE_NAME);
        commonLoadString(NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME);
    }

    @Test
    public void loadFile() throws JAXBException, IOException, SAXException {
        commonLoadFile(NO_MODELNAME_SAMPLE_NAME);
        commonLoadFile(NO_MODELNAME_NO_SEGMENTID_SAMPLE_NAME);
    }


    private void commonLoadString(String fileName) throws IOException, JAXBException, SAXException {
        InputStream inputStream = getFileInputStream(fileName);

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                                                        (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        PMML retrieved = KiePMMLUtil.load(textBuilder.toString());
        commonValidatePMML(retrieved);
    }

    private void commonLoadFile(String fileName) throws IOException, JAXBException, SAXException {
        PMML retrieved = KiePMMLUtil.load(getFileInputStream(fileName), fileName);
        commonValidatePMML(retrieved);
    }

    private void commonValidatePMML(PMML toValidate) {
        assertNotNull(toValidate);
        for (Model model : toValidate.getModels()) {
            assertNotNull(model.getModelName());
            if (model instanceof MiningModel) {
                commonValidateMiningModel((MiningModel) model);
            }
        }
    }

    private void commonValidateMiningModel(MiningModel toValidate) {
        assertNotNull(toValidate);
        for (Segment segment : toValidate.getSegmentation().getSegments()) {
            assertNotNull(segment.getId());
            Model segmentModel = segment.getModel();
            assertNotNull(segmentModel.getModelName());
            if (segmentModel instanceof MiningModel) {
                commonValidateMiningModel((MiningModel) segmentModel);
            }
        }
    }
}