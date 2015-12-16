/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kie.internal.conf;

import org.kie.api.conf.SingleValueKieBaseOption;

/**
 * An option to define after how many evaluations in interpreted mode (with mvel)
 * a constraint should be jitted (translated in bytecode)
 */
public class ConstraintJittingThresholdOption implements SingleValueKieBaseOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the constraint jitting option
     */
    public static final String PROPERTY_NAME = "drools.jittingThreshold";

    /**
     * The defualt value for this option
     */
    public static final int DEFAULT_VALUE = 20;

    /**
     * The number of evaluations in interpreted mode after which a
     * constraint has to be jitted
     */
    private final int threshold;

    /**
     * Private constructor to enforce the use of the factory method
     * @param threshold
     */
    private ConstraintJittingThresholdOption( int threshold ) {
        this.threshold = threshold;
    }

    /**
     * This is a factory method for this Constraint Jitting Threshold configuration.
     * The factory method is a best practice for the case where the
     * actual object construction is changed in the future.
     *
     * @param threshold the threshold value for the Constraint Jitting option
     *
     * @return the actual type safe Constraint Jitting threshold configuration.
     */
    public static ConstraintJittingThresholdOption get( int threshold ) {
        return new ConstraintJittingThresholdOption( threshold );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * Returns the threshold value for PermGen
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
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ConstraintJittingThresholdOption other = (ConstraintJittingThresholdOption) obj;
        if ( threshold != other.threshold ) return false;
        return true;
    }
}
