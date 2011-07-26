/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.agent.conf;

public class ValidationTimeoutOption {

    /**
     * The property name for the validation timeout
     */
    public static final String PROPERTY_NAME = "drools.agent.validationTimeout";

    /**
     * timeout
     */
    private final int timeout;

    /**
     * Private constructor to enforce the use of the factory method
     * @param timeout
     */
    private ValidationTimeoutOption( int timeout ) {
        this.timeout = timeout;
    }


    public static ValidationTimeoutOption get( int threshold ) {
        return new ValidationTimeoutOption( threshold );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * Returns the threshold value for alpha hashing
     *
     * @return
     */
    public int getTimeout() {
        return timeout;
    }



    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationTimeoutOption that = (ValidationTimeoutOption) o;

        if (timeout != that.timeout) return false;

        return true;
    }

    public int hashCode() {
        return timeout;
    }
}
