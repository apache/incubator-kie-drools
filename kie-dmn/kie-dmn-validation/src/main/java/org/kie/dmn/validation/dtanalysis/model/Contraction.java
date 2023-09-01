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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.feel.util.Generated;

public class Contraction {

    public final int rule;
    public final List<Integer> pairedRules;
    public final int adjacentDimension;
    public final List<Interval> dimensionAsContracted;

    public Contraction(int rule, Collection<Integer> pairedRules, int adjacentDimension, List<Interval> dimensionAsContracted) {
        super();
        this.rule = rule;
        this.pairedRules = Collections.unmodifiableList(new ArrayList<>(pairedRules));
        this.adjacentDimension = adjacentDimension;
        this.dimensionAsContracted = Collections.unmodifiableList(dimensionAsContracted);
    }

    public List<Integer> impactedRules() {
        List<Integer> results = new ArrayList<>();
        results.add(rule);
        results.addAll(pairedRules);
        return results;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + adjacentDimension;
        result = prime * result + ((pairedRules == null) ? 0 : pairedRules.hashCode());
        result = prime * result + rule;
        return result;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Contraction other = (Contraction) obj;
        if (adjacentDimension != other.adjacentDimension)
            return false;
        if (pairedRules == null) {
            if (other.pairedRules != null)
                return false;
        } else if (!pairedRules.equals(other.pairedRules))
            return false;
        if (rule != other.rule)
            return false;
        return true;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Contraction [rule=");
        builder.append(rule);
        builder.append(", pairedRule=");
        builder.append(pairedRules);
        builder.append(", adjacentDimension=");
        builder.append(adjacentDimension);
        builder.append(", dimensionAsContracted=");
        builder.append(dimensionAsContracted);
        builder.append("]");
        return builder.toString();
    }

}
