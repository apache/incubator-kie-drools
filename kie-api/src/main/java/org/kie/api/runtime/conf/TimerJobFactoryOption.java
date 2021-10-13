/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.conf;

/**
 * A class for the timer job factory manager configuration.
 */
public class TimerJobFactoryOption implements SingleValueKieSessionOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the timer job factory manager configuration
     */
    public static final String PROPERTY_NAME = "drools.timerJobFactory";

    /**
     * Timer Job Type
     */
    private final String timerJobType;

    /**
     * Private constructor to enforce the use of the factory method
     * @param timerJobType
     */
    private TimerJobFactoryOption( String timerJobType ) {
        this.timerJobType = timerJobType;
    }

    /**
     * This is a factory method for this timer job factory manager configuration.
     * The factory method is a best practice for the case where the
     * actual object construction is changed in the future.
     *
     * @param timerJobType  the identifier for the belie system
     *
     * @return the actual type safe timer job factory manager configuration.
     */
    public static TimerJobFactoryOption get( String timerJobType ) {
        return new TimerJobFactoryOption( timerJobType );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * @return the configured timer job factory manager
     */
    public String getTimerJobType() {
        return timerJobType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (( timerJobType == null) ? 0 :  timerJobType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) { return true; }
        if ( obj == null ) { return false; }
        if ( getClass() != obj.getClass() ) { return false; }
        TimerJobFactoryOption other = (TimerJobFactoryOption) obj;
        if (  timerJobType == null ) {
            if ( other.timerJobType != null ) {
                return false;
            }
        } else if ( ! timerJobType.equals( other.timerJobType ) ) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "TimerJobFactoryOption( "+ timerJobType +" )";
    }
}
