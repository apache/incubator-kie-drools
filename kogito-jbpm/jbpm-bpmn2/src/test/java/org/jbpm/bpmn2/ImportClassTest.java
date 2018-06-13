/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.Test;

public class ImportClassTest extends JbpmBpmn2TestCase {
    
    public ImportClassTest() {
        super(false);
    }

    @Test
    public void testResourceType() throws Exception {
        
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> { 
            createKnowledgeBaseWithoutDumper("build/sample.bpmn", "build/sample2.bpmn"); })
        .withMessageContaining("Process Compilation error HelloService cannot be resolved to a type");  
    }

}
