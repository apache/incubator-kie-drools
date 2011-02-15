/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.cloudbalancing.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CloudProcess")
public class CloudProcess extends AbstractPersistable implements Comparable<CloudProcess> {

    private int minimalCpuPower; // in gigahertz
    private int minimalMemory; // in gigabyte RAM
    private int minimalNetworkBandwidth; // in gigabyte per hour

    public int getMinimalCpuPower() {
        return minimalCpuPower;
    }

    public void setMinimalCpuPower(int minimalCpuPower) {
        this.minimalCpuPower = minimalCpuPower;
    }

    public int getMinimalMemory() {
        return minimalMemory;
    }

    public void setMinimalMemory(int minimalMemory) {
        this.minimalMemory = minimalMemory;
    }

    public int getMinimalNetworkBandwidth() {
        return minimalNetworkBandwidth;
    }

    public void setMinimalNetworkBandwidth(int minimalNetworkBandwidth) {
        this.minimalNetworkBandwidth = minimalNetworkBandwidth;
    }

    public int getMinimalMultiplicand() {
        return minimalCpuPower * minimalMemory * minimalNetworkBandwidth;
    }

    public String getLabel() {
        return "Process " + id + ", minimal:\nCPU: " + minimalCpuPower + " GHz\nRAM: " + minimalMemory + " GB\nNetwork: "
                + minimalNetworkBandwidth + " GB";
    }

    public int compareTo(CloudProcess other) {
        return new CompareToBuilder()
                .append(minimalCpuPower, other.minimalCpuPower)
                .append(minimalMemory, other.minimalMemory)
                .append(minimalNetworkBandwidth, other.minimalNetworkBandwidth)
                .toComparison();
    }

}
