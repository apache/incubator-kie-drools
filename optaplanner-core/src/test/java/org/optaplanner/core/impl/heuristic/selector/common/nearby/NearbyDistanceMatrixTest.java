/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

public class NearbyDistanceMatrixTest {

    @Test
    public void addAllDestinations() {
        final MatrixTestdataObject a = new MatrixTestdataObject("a", 0, new double[] { 0.0, 4.0, 2.0, 6.0 });
        final MatrixTestdataObject b = new MatrixTestdataObject("b", 1, new double[] { 4.0, 0.0, 5.0, 10.0 });
        final MatrixTestdataObject c = new MatrixTestdataObject("c", 2, new double[] { 2.0, 5.0, 0.0, 7.0 });
        final MatrixTestdataObject d = new MatrixTestdataObject("d", 3, new double[] { 6.0, 10.0, 7.0, 0.0 });
        List<Object> entityList = Arrays.asList(a, b, c, d);
        NearbyDistanceMeter<MatrixTestdataObject, MatrixTestdataObject> meter = (origin,
                destination) -> origin.distances[destination.index];

        NearbyDistanceMatrix nearbyDistanceMatrix = new NearbyDistanceMatrix(meter, 4);
        nearbyDistanceMatrix.addAllDestinations(a, entityList.iterator(), 4);
        nearbyDistanceMatrix.addAllDestinations(b, entityList.iterator(), 4);
        nearbyDistanceMatrix.addAllDestinations(c, entityList.iterator(), 4);
        nearbyDistanceMatrix.addAllDestinations(d, entityList.iterator(), 4);

        assertThat(nearbyDistanceMatrix.getDestination(a, 0)).isSameAs(a);
        assertThat(nearbyDistanceMatrix.getDestination(a, 1)).isSameAs(c);
        assertThat(nearbyDistanceMatrix.getDestination(a, 2)).isSameAs(b);
        assertThat(nearbyDistanceMatrix.getDestination(a, 3)).isSameAs(d);
        assertThat(nearbyDistanceMatrix.getDestination(b, 0)).isSameAs(b);
        assertThat(nearbyDistanceMatrix.getDestination(b, 1)).isSameAs(a);
        assertThat(nearbyDistanceMatrix.getDestination(b, 2)).isSameAs(c);
        assertThat(nearbyDistanceMatrix.getDestination(b, 3)).isSameAs(d);
        assertThat(nearbyDistanceMatrix.getDestination(c, 0)).isSameAs(c);
        assertThat(nearbyDistanceMatrix.getDestination(c, 1)).isSameAs(a);
        assertThat(nearbyDistanceMatrix.getDestination(c, 2)).isSameAs(b);
        assertThat(nearbyDistanceMatrix.getDestination(c, 3)).isSameAs(d);
        assertThat(nearbyDistanceMatrix.getDestination(d, 0)).isSameAs(d);
        assertThat(nearbyDistanceMatrix.getDestination(d, 1)).isSameAs(a);
        assertThat(nearbyDistanceMatrix.getDestination(d, 2)).isSameAs(c);
        assertThat(nearbyDistanceMatrix.getDestination(d, 3)).isSameAs(b);
    }

    @Test
    public void addAllDestinationsWithSameDistance() {
        final MatrixTestdataObject a = new MatrixTestdataObject("a", 0, new double[] { 0.0, 1.0, 1.0, 1.0 });
        final MatrixTestdataObject b = new MatrixTestdataObject("b", 1, new double[] { 1.0, 0.0, 2.0, 1.0 });
        final MatrixTestdataObject c = new MatrixTestdataObject("c", 2, new double[] { 1.0, 2.0, 0.0, 3.0 });
        final MatrixTestdataObject d = new MatrixTestdataObject("d", 3, new double[] { 1.0, 1.0, 3.0, 0.0 });
        List<Object> entityList = Arrays.asList(a, b, c, d);
        NearbyDistanceMeter<MatrixTestdataObject, MatrixTestdataObject> meter = (origin,
                destination) -> origin.distances[destination.index];

        NearbyDistanceMatrix nearbyDistanceMatrix = new NearbyDistanceMatrix(meter, 4);
        nearbyDistanceMatrix.addAllDestinations(a, entityList.iterator(), 4);
        nearbyDistanceMatrix.addAllDestinations(b, entityList.iterator(), 4);
        nearbyDistanceMatrix.addAllDestinations(c, entityList.iterator(), 4);
        nearbyDistanceMatrix.addAllDestinations(d, entityList.iterator(), 4);

        assertThat(nearbyDistanceMatrix.getDestination(a, 0)).isSameAs(a);
        assertThat(nearbyDistanceMatrix.getDestination(a, 1)).isSameAs(b);
        assertThat(nearbyDistanceMatrix.getDestination(a, 2)).isSameAs(c);
        assertThat(nearbyDistanceMatrix.getDestination(a, 3)).isSameAs(d);
        assertThat(nearbyDistanceMatrix.getDestination(b, 0)).isSameAs(b);
        assertThat(nearbyDistanceMatrix.getDestination(b, 1)).isSameAs(a);
        assertThat(nearbyDistanceMatrix.getDestination(b, 2)).isSameAs(d);
        assertThat(nearbyDistanceMatrix.getDestination(b, 3)).isSameAs(c);
        assertThat(nearbyDistanceMatrix.getDestination(c, 0)).isSameAs(c);
        assertThat(nearbyDistanceMatrix.getDestination(c, 1)).isSameAs(a);
        assertThat(nearbyDistanceMatrix.getDestination(c, 2)).isSameAs(b);
        assertThat(nearbyDistanceMatrix.getDestination(c, 3)).isSameAs(d);
        assertThat(nearbyDistanceMatrix.getDestination(d, 0)).isSameAs(d);
        assertThat(nearbyDistanceMatrix.getDestination(d, 1)).isSameAs(a);
        assertThat(nearbyDistanceMatrix.getDestination(d, 2)).isSameAs(b);
        assertThat(nearbyDistanceMatrix.getDestination(d, 3)).isSameAs(c);
    }

    private static class MatrixTestdataObject extends TestdataObject {
        private int index;
        private double[] distances;

        public MatrixTestdataObject(String code, int index, double[] distances) {
            super(code);
            this.index = index;
            this.distances = distances;
        }

    }

}
