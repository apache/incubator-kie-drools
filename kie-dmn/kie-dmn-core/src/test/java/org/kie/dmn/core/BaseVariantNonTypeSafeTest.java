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
package org.kie.dmn.core;

import org.junit.runners.Parameterized;

import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_STRICT;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK;

public abstract class BaseVariantNonTypeSafeTest extends BaseVariantTest {

    public BaseVariantNonTypeSafeTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{KIE_API_TYPECHECK, BUILDER_STRICT, BUILDER_DEFAULT_NOCL_TYPECHECK};
    }

}
