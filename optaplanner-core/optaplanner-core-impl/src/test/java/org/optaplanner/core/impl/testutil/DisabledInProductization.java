/*
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

package org.optaplanner.core.impl.testutil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.condition.DisabledIf;

/**
 * Used to disable tests that take advantage of features which are not available in the Red Hat build of OptaPlanner.
 * <p>
 * Case in point: optaplanner-constraint-streams-bavet module may not be built at all,
 * and all tests that expect its presence need to be disabled.
 */
@Retention(RetentionPolicy.RUNTIME)
// Implemented in this roundabout way because @DisabledIfSystemProperty has issues with null properties.
@DisabledIf("org.optaplanner.core.impl.testutil.DisabledInProductizationCheck#isProductized")
public @interface DisabledInProductization {

    // TODO remove this when Bavet is productized

}
