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
package org.drools.mvel.compiler;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Results;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtil {

    public static void assertDrlHasCompilationError( String str, int errorNr, KieBaseTestConfiguration kieBaseTestConfiguration ) {
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        Results results = kieBuilder.getResults();
        if ( errorNr > 0 ) {
            assertThat(results.getMessages().size()).isEqualTo(errorNr);
        } else {
            assertThat(results.getMessages().size() > 0).isTrue();
        }
    }
}
