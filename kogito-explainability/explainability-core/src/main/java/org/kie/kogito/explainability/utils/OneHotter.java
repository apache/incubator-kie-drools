/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

public class OneHotter {
    private String oheSplitter = "_OHE_";
    private String proxySplitter = "_OHEPROXY";
    private final Random rn;
    private final String alpha = "abcdefghijklmnopqrstuvwxyz";
    private Map<String, LinkedHashSet<Value>> categoricals = new HashMap<>();

    private void addFeatureValue(Feature f, boolean initialization) {
        // check to see that no new features have been added since initialized
        if (!initialization && !categoricals.containsKey(f.getName())) {
            throw new IllegalArgumentException(String.format(
                    "Feature name %s was not present in initialized dataset", f.getName()));
        }
        // if the name of a feature clashes with the ohe splitters, fix them
        if (f.getName().contains(oheSplitter) || f.getName().contains(proxySplitter)) {
            String randString = String.valueOf(alpha.charAt(rn.nextInt(26)));
            oheSplitter = oheSplitter.substring(0, oheSplitter.length() - 1) + "_" + randString + "_";
            proxySplitter = proxySplitter + "_" + randString;
        }

        // add feature to categoricals if new
        if (!categoricals.containsKey(f.getName())) {
            this.categoricals.put(f.getName(), new LinkedHashSet<>(List.of(f.getValue())));
        } else {
            LinkedHashSet<Value> currVals = this.categoricals.get(f.getName());
            currVals.add(f.getValue());
            this.categoricals.put(f.getName(), currVals);
        }
    }

    public OneHotter(List<PredictionInput> pis, PerturbationContext pc) {
        this.rn = pc.getRandom();
        for (PredictionInput pi : pis) {
            for (Feature f : pi.getFeatures()) {
                if (f.getType() == Type.CATEGORICAL) {
                    addFeatureValue(f, true);
                }
            }
        }
    }

    private void featureGenerator(Feature prototype, Value[] comparedVals, boolean proxy, List<Feature> encodedFeatures) {
        for (int i = 0; i < comparedVals.length; i++) {
            if (proxy && comparedVals[i].equals(prototype.getValue())) {
                Feature newFeature = new Feature(
                        prototype.getName() + this.proxySplitter,
                        Type.NUMBER,
                        new Value(i));
                encodedFeatures.add(newFeature);
                break;
            } else if (!proxy) {
                Feature newFeature = new Feature(
                        prototype.getName() + this.oheSplitter + i + this.oheSplitter + comparedVals[i],
                        Type.NUMBER,
                        new Value(prototype.getValue().equals(comparedVals[i]) ? 1 : 0));
                encodedFeatures.add(newFeature);
            }
        }
    }

    // === ENCODERS ====================================================================================================
    public PredictionInput oneHotEncode(PredictionInput pi, boolean proxy) {
        if (categoricals.isEmpty()) {
            return pi;
        }
        List<Feature> encodedFeatures = new ArrayList<>();
        for (Feature f : pi.getFeatures()) {
            if (categoricals.containsKey(f.getName()) && f.getType() == Type.CATEGORICAL) {
                if (!categoricals.get(f.getName()).contains(f.getValue())) {
                    addFeatureValue(f, false);
                }
                Value[] comparedVals = categoricals.get(f.getName()).toArray(new Value[0]);
                featureGenerator(f, comparedVals, proxy, encodedFeatures);
            } else {
                encodedFeatures.add(f);
            }
        }
        return new PredictionInput(encodedFeatures);
    }

    public List<PredictionInput> oneHotEncode(List<PredictionInput> pis, boolean proxy) {
        if (categoricals.isEmpty()) {
            return pis;
        }
        List<PredictionInput> encodedPIs = new ArrayList<>();
        for (PredictionInput pi : pis) {
            encodedPIs.add(oneHotEncode(pi, proxy));
        }
        return encodedPIs;
    }

    // === DECODERS ====================================================================================================
    public PredictionInput oneHotDecode(PredictionInput pi, boolean proxy) {
        if (categoricals.isEmpty()) {
            return pi;
        }
        String proxyIndicator = proxy ? this.proxySplitter : this.oheSplitter;
        List<Feature> decodedFeatures = new ArrayList<>();
        for (Feature f : pi.getFeatures()) {
            if (f.getName().contains(proxyIndicator)) {
                if (proxy) {
                    String parentFeature = f.getName().split(this.proxySplitter)[0];
                    decodedFeatures.add(new Feature(
                            parentFeature,
                            Type.CATEGORICAL,
                            (Value) categoricals.get(parentFeature).toArray()[(int) f.getValue().asNumber()]));
                } else if (f.getValue().asNumber() == 1) {
                    String[] splitName = f.getName().split(this.oheSplitter);
                    String parentFeature = splitName[0];
                    int categoricalValue = Integer.parseInt(splitName[1]);
                    decodedFeatures.add(new Feature(
                            parentFeature,
                            Type.CATEGORICAL,
                            (Value) categoricals.get(parentFeature).toArray()[categoricalValue]));
                }
            } else {
                decodedFeatures.add(f);
            }
        }
        return new PredictionInput(decodedFeatures);
    }

    public List<PredictionInput> oneHotDecode(List<PredictionInput> pis, boolean proxy) {
        if (categoricals.isEmpty()) {
            return pis;
        }
        List<PredictionInput> decodedPIs = new ArrayList<>();
        for (PredictionInput pi : pis) {
            decodedPIs.add(oneHotDecode(pi, proxy));
        }
        return decodedPIs;
    }
}
