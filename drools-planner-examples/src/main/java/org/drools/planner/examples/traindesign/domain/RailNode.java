/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.traindesign.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.pas.domain.AdmissionPart;
import org.drools.planner.examples.pas.domain.Patient;

@XStreamAlias("RailNode")
public class RailNode extends AbstractPersistable implements Comparable<RailNode> {

    private String name;
    private int blockSwapCost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBlockSwapCost() {
        return blockSwapCost;
    }

    public void setBlockSwapCost(int blockSwapCost) {
        this.blockSwapCost = blockSwapCost;
    }

    public int compareTo(RailNode other) {
        return new CompareToBuilder()
                .append(id, other.id)
                .toComparison();
    }

    @Override
    public String toString() {
        return name;
    }

}
