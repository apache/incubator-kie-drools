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
*/

package org.drools.mvel.compiler.lang;

import org.drools.compiler.lang.DroolsSoftKeywords;
import org.drools.core.base.evaluators.EvaluatorRegistry;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DroolsSoftKeywordsTest {

    /**
     * Test method for {@link org.kie.lang.DroolsSoftKeywords#isOperator(java.lang.String, boolean)}.
     */
    @Test
    public void testIsOperator() {
        // initializes the registry
        new EvaluatorRegistry();

        // test the registry
        assertThat(DroolsSoftKeywords.isOperator("matches", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("matches", true)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("contains", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("contains", true)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("after", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("after", true)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("before", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("before", true)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("finishes", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("finishes", true)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("overlappedby", false)).isTrue();
        assertThat(DroolsSoftKeywords.isOperator("overlappedby", true)).isTrue();

        assertThat(DroolsSoftKeywords.isOperator("xyz", false)).isFalse();
        assertThat(DroolsSoftKeywords.isOperator("xyz", true)).isFalse();

    }

}
