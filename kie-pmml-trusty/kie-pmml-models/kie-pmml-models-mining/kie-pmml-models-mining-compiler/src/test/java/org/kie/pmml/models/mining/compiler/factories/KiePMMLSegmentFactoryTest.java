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

package org.kie.pmml.models.mining.compiler.factories;

import java.util.List;
import java.util.Map;

import org.dmg.pmml.mining.Segment;
import org.junit.Test;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegment;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KiePMMLSegmentFactoryTest extends AbstractKiePMMLFactoryTest {

    @Test
    public void getSegments() {
        final List<Segment> segments = MINING_MODEL.getSegmentation().getSegments();
        final List<KiePMMLSegment> retrieved = KiePMMLSegmentFactory.getSegments(DATA_DICTIONARY,
                                                                                 TRANSFORMATION_DICTIONARY,
                                                                                 segments,
                                                                                 KNOWLEDGE_BUILDER);
        assertNotNull(retrieved);
        assertEquals(segments.size(), retrieved.size());
        for (int i = 0; i < segments.size(); i++) {
            commonEvaluateSegment(retrieved.get(i), segments.get(i));
        }
    }

    @Test
    public void getSegment() {
        final Segment segment = MINING_MODEL.getSegmentation().getSegments().get(0);
        final KiePMMLSegment retrieved = KiePMMLSegmentFactory.getSegment(DATA_DICTIONARY,
                                                                          TRANSFORMATION_DICTIONARY,
                                                                          segment,
                                                                          KNOWLEDGE_BUILDER);
        commonEvaluateSegment(retrieved, segment);
    }

    @Test
    public void getSegmentsSourcesMap() {
        final List<Segment> segments = MINING_MODEL.getSegmentation().getSegments();
        final String packageName = "packagename";
        final Map<String, String> retrieved = KiePMMLSegmentFactory.getSegmentsSourcesMap(
                packageName,
                DATA_DICTIONARY,
                TRANSFORMATION_DICTIONARY,
                segments,
                KNOWLEDGE_BUILDER);
        assertNotNull(retrieved);
        for (int i = 0; i < segments.size(); i++) {
            commonEvaluateMap(retrieved, segments.get(i));
        }
    }

    @Test
    public void getSegmentSourcesMap() {
        final Segment segment = MINING_MODEL.getSegmentation().getSegments().get(0);
        final String packageName = "packagename";
        final Map<String, String> retrieved = KiePMMLSegmentFactory.getSegmentSourcesMap(packageName,
                                                                                         DATA_DICTIONARY,
                                                                                         TRANSFORMATION_DICTIONARY,
                                                                                         segment,
                                                                                         KNOWLEDGE_BUILDER);
        commonEvaluateMap(retrieved, segment);
    }

    private void commonEvaluateSegment(final KiePMMLSegment toEvaluate, final Segment segment) {
        assertNotNull(toEvaluate);
        assertEquals(segment.getId(), toEvaluate.getName());
        assertEquals(segment.getPredicate().getClass().getSimpleName(), toEvaluate.getKiePMMLPredicate().getName());
        assertNotNull(toEvaluate.getModel());
        assertTrue(toEvaluate.getModel() instanceof KiePMMLRegressionModel);
    }

    private void commonEvaluateMap(final Map<String, String> toEvaluate, final Segment segment) {
        assertNotNull(toEvaluate);
    }
}