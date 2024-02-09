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

import org.kie.dmn.feel.runtime.Range;

/**
 * For internal use. A semantically null Bound, that is meant NOT to be used for comparison.
 */
@SuppressWarnings("rawtypes")
public class NullBoundImpl extends Bound {

    /**
     * For internal use. A singleton instance of this semantically null Bound, that is meant NOT to be used for comparison in DT gap analysis
     */
    public static final NullBoundImpl NULL = new NullBoundImpl();

    @SuppressWarnings("unchecked")
    private NullBoundImpl() {
        super(null, null, null);
    }

    @Override
    public int compareTo(Bound o) {
        throw new IllegalStateException();
    }

    @Override
    public Comparable getValue() {
        throw new IllegalStateException();
    }

    @Override
    public Range.RangeBoundary getBoundaryType() {
        throw new IllegalStateException();
    }

    @Override
    public Interval getParent() {
        throw new IllegalStateException();
    }

    @Override
    public boolean isLowerBound() {
        throw new IllegalStateException();
    }

    @Override
    public boolean isUpperBound() {
        throw new IllegalStateException();
    }

    @Override
    public String toString() {
        return "NullBoundImpl []";
    }

}
