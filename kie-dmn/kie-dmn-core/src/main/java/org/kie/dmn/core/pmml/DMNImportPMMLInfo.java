/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.pmml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.DataField;
import org.dmg.pmml.Interval;
import org.dmg.pmml.PMML;
import org.dmg.pmml.Value;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.api.Import;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNImportPMMLInfo extends PMMLInfo<DMNPMMLModelInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(DMNImportPMMLInfo.class);

    private final Import i;

    public DMNImportPMMLInfo(Import i, Collection<DMNPMMLModelInfo> models, PMMLHeaderInfo h) {
        super(models, h);
        this.i = i;
    }

    public static Either<Exception, DMNImportPMMLInfo> from(InputStream is, DMNCompilerConfigurationImpl cc, DMNModelImpl model, Import i) {
        try {
            final PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(is);
            PMMLHeaderInfo h = PMMLInfo.pmmlToHeaderInfo(pmml, pmml.getHeader());
            for (DataField df : pmml.getDataDictionary().getDataFields()) {
                String dfName = df.getName().getValue();
                BuiltInType ft = null;
                switch (df.getDataType()) {
                    case BOOLEAN:
                        ft = BuiltInType.BOOLEAN;
                        break;
                    case DATE:
                        ft = BuiltInType.DATE;
                        break;
                    case DATE_TIME:
                        ft = BuiltInType.DATE_TIME;
                        break;
                    case DOUBLE:
                    case FLOAT:
                    case INTEGER:
                        ft = BuiltInType.NUMBER;
                        break;
                    case STRING:
                        ft = BuiltInType.STRING;
                        break;
                    case TIME:
                        ft = BuiltInType.TIME;
                        break;
                    default:
                        ft = BuiltInType.UNKNOWN;
                        break;
                }
                List<FEELProfile> helperFEELProfiles = cc.getFeelProfiles();
                DMNFEELHelper feel = new DMNFEELHelper(cc.getRootClassLoader(), helperFEELProfiles);
                List<UnaryTest> av = new ArrayList<>();
                if (df.getValues() != null && !df.getValues().isEmpty() && ft != BuiltInType.UNKNOWN) {
                    final BuiltInType feelType = ft;
                    String lov = df.getValues().stream().map(Value::getValue).map(o -> feelType == BuiltInType.STRING ? "\"" + o.toString() + "\"" : o.toString()).collect(Collectors.joining(","));
                    av = feel.evaluateUnaryTests(lov, Collections.emptyMap());
                } else if (df.getIntervals() != null && !df.getIntervals().isEmpty() && ft != BuiltInType.UNKNOWN) {
                    for (Interval interval : df.getIntervals()) {
                        String utString = null;
                        switch (interval.getClosure()) {
                            case CLOSED_CLOSED:
                                utString = new StringBuilder("[").append(interval.getLeftMargin()).append("..").append(interval.getRightMargin()).append("]").toString();
                                break;
                            case CLOSED_OPEN:
                                utString = new StringBuilder("[").append(interval.getLeftMargin()).append("..").append(interval.getRightMargin()).append(")").toString();
                                break;
                            case OPEN_CLOSED:
                                utString = new StringBuilder("(").append(interval.getLeftMargin()).append("..").append(interval.getRightMargin()).append("]").toString();
                                break;
                            case OPEN_OPEN:
                                utString = new StringBuilder("(").append(interval.getLeftMargin()).append("..").append(interval.getRightMargin()).append(")").toString();
                                break;
                        }
                        List<UnaryTest> ut = feel.evaluateUnaryTests(utString, Collections.emptyMap());
                        av.addAll(ut);
                    }
                }
                DMNType type = new SimpleTypeImpl(i.getNamespace(), dfName, null, false, av, null, ft);
                model.getTypeRegistry().registerType(type);
            }
            List<DMNPMMLModelInfo> models = pmml.getModels()
                                                .stream()
                                                .map(m -> PMMLInfo.pmmlToModelInfo(m))
                                                .map(proto -> DMNPMMLModelInfo.from(proto, model, i)).collect(Collectors.toList());
            DMNImportPMMLInfo info = new DMNImportPMMLInfo(i, models, h);
            return Either.ofRight(info);
        } catch (Throwable e) {
            return Either.ofLeft(new Exception("Unable to process DMNImportPMMLInfo", e));
        }
    }

    public String getImportName() {
        return i.getName();
    }

}
