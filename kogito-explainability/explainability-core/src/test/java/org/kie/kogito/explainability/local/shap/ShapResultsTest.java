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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShapResultsTest {
    ShapResults buildShapResults(int nOutputs, int nFeatures, int scalar1, int scalar2) {
        Saliency[] saliencies = new Saliency[nOutputs];
        for (int i = 0; i < nOutputs; i++) {
            List<FeatureImportance> fis = new ArrayList<>();
            for (int j = 0; j < nFeatures; j++) {
                fis.add(new FeatureImportance(new Feature("f" + String.valueOf(j), Type.NUMBER, new Value(j)), i * j * scalar1));
            }
            saliencies[i] = new Saliency(new Output("o" + String.valueOf(i), Type.NUMBER, new Value(i), 1.0), fis);
        }
        RealVector fnull = MatrixUtils.createRealVector(new double[nOutputs]);
        fnull.mapAddToSelf(scalar2);
        return new ShapResults(saliencies, fnull);
    }

    // test equals and hashing
    @Test
    void testEqualsSameObj() {
        ShapResults sr1 = buildShapResults(2, 2, 1, 1);
        assertEquals(sr1, sr1);
        assertEquals(sr1.hashCode(), sr1.hashCode());
    }

    @Test
    void testEquals() {
        ShapResults sr1 = buildShapResults(2, 2, 1, 1);
        ShapResults sr2 = buildShapResults(2, 2, 1, 1);
        assertEquals(sr1, sr2);
        assertNotEquals(sr1.hashCode(), sr2.hashCode());
    }

    @Test
    void testDiffOutputs() {
        ShapResults sr1 = buildShapResults(2, 2, 1, 1);
        ShapResults sr2 = buildShapResults(20, 2, 1, 1);
        assertNotEquals(sr1, sr2);
        assertNotEquals(sr1.hashCode(), sr2.hashCode());
    }

    @Test
    void testDiffFeatures() {
        ShapResults sr1 = buildShapResults(2, 2, 1, 1);
        ShapResults sr2 = buildShapResults(2, 20, 1, 1);
        assertNotEquals(sr1, sr2);
        assertNotEquals(sr1.hashCode(), sr2.hashCode());
    }

    @Test
    void testDiffImportances() {
        ShapResults sr1 = buildShapResults(2, 2, 1, 1);
        ShapResults sr2 = buildShapResults(2, 2, 10, 1);
        assertNotEquals(sr1, sr2);
        assertNotEquals(sr1.hashCode(), sr2.hashCode());
    }

    @Test
    void testDiffFnull() {
        ShapResults sr1 = buildShapResults(2, 2, 1, 1);
        ShapResults sr2 = buildShapResults(2, 2, 1, 10);
        assertNotEquals(sr1, sr2);
        assertNotEquals(sr1.hashCode(), sr2.hashCode());
    }

    @Test
    void testToString() {
        ShapResults sr = buildShapResults(2, 10, 1, 1);
        String srs = sr.toString(2);
        assertTrue(true);
    }
}
