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

package org.kie.kogito.explainability.local.shap;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

import org.apache.commons.math3.linear.RealVector;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.utils.MatrixUtilsExtensions;

public class ShapResults {
    private final Saliency[] saliencies;
    private final RealVector fnull;

    public ShapResults(Saliency[] saliencies, RealVector fnull) {
        this.saliencies = saliencies;
        this.fnull = fnull;
    }

    public Saliency[] getSaliencies() {
        return saliencies;
    }

    public RealVector getFnull() {
        return fnull;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShapResults other = (ShapResults) o;
        if (this.saliencies.length != other.getSaliencies().length) {
            return false;
        }
        if (!this.fnull.equals(other.getFnull())) {
            return false;
        }
        for (int i = 0; i < this.saliencies.length; i++) {
            List<FeatureImportance> thisPFIs = this.saliencies[i].getPerFeatureImportance();
            List<FeatureImportance> otherPFIs = other.getSaliencies()[i].getPerFeatureImportance();
            if (thisPFIs.size() != otherPFIs.size()) {
                return false;
            }
            for (int j = 0; j < thisPFIs.size(); j++) {
                if (!thisPFIs.get(j).equals(otherPFIs.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(saliencies), fnull);
    }

    @Override
    public String toString() {
        return toString(3);
    }

    public String toString(int decimalPlaces) {
        StringBuilder out = new StringBuilder();
        String rawFormatter = "  %%%ds = %%%ds : %%%ds +/- %%%ds%n";

        // create table for each output
        for (int i = 0; i < this.saliencies.length; i++) {
            int startIdx = out.length();
            String[][] table = new String[this.saliencies[i].getPerFeatureImportance().size() + 3][4];

            // add header to table
            table[0] = new String[] { "", "Feature", "SHAP Value", "" };

            // add fnull to table
            table[1] = new String[] { "", "FNull", String.format(String.format("%%.%df", decimalPlaces), fnull.getEntry(i)), "" };

            // iterate over features
            List<FeatureImportance> pfis = saliencies[i].getPerFeatureImportance();
            for (int j = 0; j < pfis.size(); j++) {
                table[2 + j] = new String[] {
                        pfis.get(j).getFeature().getName(),
                        pfis.get(j).getFeature().getValue().toString(),
                        String.format(String.format("%%.%df", decimalPlaces), pfis.get(j).getScore()),
                        String.format(String.format("%%.%df", decimalPlaces), pfis.get(j).getConfidence())
                };
            }

            // add prediction to table
            table[pfis.size() + 2] = new String[] { "", "Prediction", String.format(String.format("%%.%df", decimalPlaces), this.saliencies[i].getOutput().getValue().asNumber()), "" };
            IntFunction<Integer> colSizer = colIdx -> MatrixUtilsExtensions.getColumn(table, colIdx).stream().mapToInt(String::length).max().getAsInt();

            // format table
            String formatter = String.format(rawFormatter, colSizer.apply(0), colSizer.apply(1), colSizer.apply(2), colSizer.apply(3));
            String formatterHeaders = formatter.replace("=", " ").replace("+/-", "   ");
            for (int j = 0; j < table.length; j++) {
                if (j < 2) {
                    out.append(String.format(formatterHeaders, table[j]));
                } else if (j == table.length - 1) {
                    String spacer = "-".repeat(Arrays.stream(out.toString().split("\n")).mapToInt(String::length).max().getAsInt());
                    out.append("  ").append(spacer).append("\n");
                    out.append(String.format(formatterHeaders, table[j]));
                } else {
                    out.append(String.format(formatter, table[j]));
                }
            }

            // add title to table
            String title = String.format(" Output %s ", this.saliencies[i].getOutput().getName());
            int spacerSize = Arrays.stream(out.toString().split("\n")).mapToInt(String::length).max().getAsInt() - title.length();
            String lspace = "-".repeat((int) Math.floor(spacerSize / 2.));
            String rspace = "-".repeat((int) (spacerSize % 2 == 0 ? Math.floor(spacerSize / 2.) : Math.ceil(spacerSize / 2.)));
            out.insert(startIdx, lspace + title.toUpperCase() + rspace + "\n");

            // add new line if this isn't the final table
            if (i != this.saliencies.length - 1) {
                out.append("\n");
            }
        }
        return out.toString();
    }
}
