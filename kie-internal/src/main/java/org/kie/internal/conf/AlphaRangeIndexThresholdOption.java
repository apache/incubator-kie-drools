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
package org.kie.internal.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * A class for the alpha node range index threshold configuration.
 */
public class AlphaRangeIndexThresholdOption implements SingleValueRuleBaseOption {
    private static final long serialVersionUID = 510l;

    /**
     * The property name
     */
    public static final String PROPERTY_NAME = "drools.alphaNodeRangeIndexThreshold";

    public static OptionKey<AlphaRangeIndexThresholdOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    /**
     * The default value for this option
     */
    public static final int DEFAULT_VALUE = 9;

    /**
     * alpha node range index threshold
     */
    private final int threshold;

    /**
     * Private constructor to enforce the use of the factory method
     * @param threshold
     */
    private AlphaRangeIndexThresholdOption( int threshold ) {
        this.threshold = threshold;
    }

    /**
     * This is a factory method for this Alpha Range Index Threshold configuration.
     * The factory method is a best practice for the case where the
     * actual object construction is changed in the future.
     *
     * @param threshold the threshold value for the alpha range index option
     *
     * @return the actual type safe alpha range index threshold configuration.
     */
    public static AlphaRangeIndexThresholdOption get( int threshold ) {
        return new AlphaRangeIndexThresholdOption( threshold );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * Returns the threshold value for alpha range index
     *
     * @return
     */
    public int getThreshold() {
        return threshold;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + threshold;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) { return true; }
        if ( obj == null ) { return false; }
        if ( getClass() != obj.getClass() ) { return false; }
        AlphaRangeIndexThresholdOption other = (AlphaRangeIndexThresholdOption) obj;
        if ( threshold != other.threshold ) {
            return false;
        }
        return true;
    }

}
