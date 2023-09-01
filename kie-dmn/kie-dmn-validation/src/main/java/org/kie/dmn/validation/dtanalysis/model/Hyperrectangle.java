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
package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.dmn.feel.util.Generated;

public class Hyperrectangle {

    private final int dimensions;
    private final List<Interval> edges = new ArrayList<>();

    public Hyperrectangle(int dimensions, List<Interval> edges) {
        super();
        this.dimensions = dimensions;
        this.edges.addAll(edges);
    }

    @Override
    public String toString() {
        return IntStream.range(0, dimensions).mapToObj(i -> {
            if (i < edges.size()) {
                return edges.get(i).toString();
            } else {
                return "-";
            }
        }).collect(Collectors.joining(" "));
    }

    public int getDimensions() {
        return dimensions;
    }

    public List<Interval> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dimensions;
        result = prime * result + edges.hashCode();
        return result;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Hyperrectangle other = (Hyperrectangle) obj;
        if (dimensions != other.dimensions) {
            return false;
        }
        if (!edges.equals(other.edges)) {
            return false;
        }
        return true;
    }

    public String asHumanFriendly(DDTATable ddtaTable) {
        List<String> valuesAsH = new ArrayList<>();
        for (int i = 0; i<dimensions; i++) {
            if (i < edges.size()) {
                valuesAsH.add(edges.get(i).asHumanFriendly(ddtaTable.getInputs().get(i)));
            } else {
                valuesAsH.add("-");
            }
        }
        return "[ " + String.join(", ", valuesAsH) + " ]";
    }

}
