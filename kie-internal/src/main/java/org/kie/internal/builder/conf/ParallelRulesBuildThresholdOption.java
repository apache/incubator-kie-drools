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
package org.kie.internal.builder.conf;

import org.kie.api.conf.OptionKey;

/**
 * A class for the parallel rules build threshold configuration.
 */
public class ParallelRulesBuildThresholdOption implements SingleValueRuleBuilderOption {

    private static final long serialVersionUID = 1L;

    /**
     * The property name for the parallel rules build threshold option
     */
    public static final String PROPERTY_NAME = "drools.parallelRulesBuildThreshold";

    public static OptionKey<ParallelRulesBuildThresholdOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private int parallelRulesBuildThreshold;

    /**
     * Private constructor to enforce the use of the factory method
     *
     * @param key
     */
    private ParallelRulesBuildThresholdOption(final int parallelRulesBuildThreshold) {
        this.parallelRulesBuildThreshold = parallelRulesBuildThreshold;
    }

    /**
     * This is a factory method for this ParallelRulesBuildThresholdOption configuration. The factory method is a best practice for the case
     * where the actual object construction is changed in the future.
     *
     * @param vale
     *            the value of the parallel rules build threshold to be configured
     *
     * @return the actual type safe parallel rules build threshold configuration.
     */
    public static ParallelRulesBuildThresholdOption get(final int parallelRulesBuildThreshold) {
        return new ParallelRulesBuildThresholdOption(parallelRulesBuildThreshold);
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public int getParallelRulesBuildThreshold() {
        return parallelRulesBuildThreshold;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + parallelRulesBuildThreshold;
        return result;
    }

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
        ParallelRulesBuildThresholdOption other = (ParallelRulesBuildThresholdOption) obj;
        if (parallelRulesBuildThreshold != other.parallelRulesBuildThreshold) {
            return false;
        }
        return true;
    }

}
