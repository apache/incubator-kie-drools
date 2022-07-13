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

package org.drools.mvel.compiler.compiler;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.ProcessLoadError;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RuleFlowErrorTest {

    @Test
    public void testError() {
        ProcessLoadError err = new ProcessLoadError(null, "XXX", null);
        assertThat(err.getMessage()).isEqualTo("XXX");
        
        Exception e = new RuntimeException("Q");
        err = new ProcessLoadError(null, "X", e);

        assertThat(err.getMessage()).isNotNull();

        assertThat(err instanceof DroolsError).isTrue();
        
    }
    
}
