/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.guided.scorecard.backend;

import org.dmg.pmml.pmml_4_2.descr.Attribute;
import org.dmg.pmml.pmml_4_2.descr.Characteristic;
import org.dmg.pmml.pmml_4_2.descr.Characteristics;
import org.dmg.pmml.pmml_4_2.descr.Extension;
import org.dmg.pmml.pmml_4_2.descr.False;
import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.dmg.pmml.pmml_4_2.descr.True;
import org.drools.compiler.compiler.GuidedScoreCardProvider;
import org.drools.core.util.IoUtils;
import org.drools.scorecards.ScorecardCompiler;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.pmml_4_2.PMML4Compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class GuidedScoreCardProviderImpl implements GuidedScoreCardProvider {

    @Override
    public String loadFromInputStream(InputStream is) throws IOException {
        String xml = new String(IoUtils.readBytesFromInputStream(is), IoUtils.UTF8_CHARSET);
        ScoreCardModel model = GuidedScoreCardXMLPersistence.getInstance().unmarshall(xml);

        return GuidedScoreCardDRLPersistence.marshal(model);
    }

    @Override
    public KieBase getKieBaseFromInputStream(InputStream is) throws IOException {
        String pmmlString = getPMMLStringFromInputStream(is);
        KieBase kbase = new KieHelper().addContent(pmmlString, ResourceType.PMML).build();
        return kbase;
    }

    @Override
    public String getPMMLStringFromInputStream(InputStream is) throws IOException {
        String xml = new String(IoUtils.readBytesFromInputStream(is), IoUtils.UTF8_CHARSET);
        ScoreCardModel model = GuidedScoreCardXMLPersistence.getInstance().unmarshall(xml);

        PMML pmml = GuidedScoreCardDRLPersistence.createPMMLDocument(model);
        checkCharacteristics(pmml);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PMML4Compiler compiler = new PMML4Compiler();
        compiler.dumpModel(pmml, baos);
        return new String(baos.toByteArray());
    }

    private void checkCharacteristics(PMML pmml) {
        if (pmml != null
                && pmml.getAssociationModelsAndBaselineModelsAndClusteringModels() != null
                && !pmml.getAssociationModelsAndBaselineModelsAndClusteringModels().isEmpty()) {
            for (Serializable s : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
                if (s instanceof Scorecard) {
                    Scorecard scard = (Scorecard) s;
                    if (scard.getExtensionsAndCharacteristicsAndMiningSchemas() != null
                            && !scard.getExtensionsAndCharacteristicsAndMiningSchemas().isEmpty()) {
                        for (Serializable sz : scard.getExtensionsAndCharacteristicsAndMiningSchemas()) {
                            if (sz instanceof Characteristics) {
                                Characteristics characteristics = (Characteristics) sz;
                                if (characteristics.getCharacteristics() == null
                                        || characteristics.getCharacteristics().isEmpty()) {
                                    Characteristic ch = new Characteristic();
                                    ch.setBaselineScore(0.0);
                                    ch.setName("placeholder");
                                    Attribute attr = new Attribute();
                                    attr.setFalse(new False());
                                    ch.getAttributes().add(attr);
                                    characteristics.getCharacteristics().add(ch);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
