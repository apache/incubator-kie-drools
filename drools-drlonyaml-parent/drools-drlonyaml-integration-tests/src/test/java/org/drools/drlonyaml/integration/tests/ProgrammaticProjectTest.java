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
package org.drools.drlonyaml.integration.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.model.codegen.ExecutableModelProject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgrammaticProjectTest {


    private static Stream<Arguments> params() {
    	return Stream.of(
    			Arguments.of("org/drools/drlonyaml/integration/tests/rule.drl",
    			"""
                package org.drools.drlonyaml.integration.tests

                global java.util.List result;

                rule R when
                    $i : Integer()
                    $m : Message( size == $i )
                then
                    result.add( $m.getText() );
                end"""),

    			Arguments.of("org/drools/drlonyaml/integration/tests/rule.drl.yaml", 
    					"""
    	                name: org.drools.drlonyaml.integration.tests
    	                globals:
    	                - type: java.util.List
    	                  id: result
    	                rules:
    	                - name: R
    	                  when:
    	                  - given: Integer
    	                    as: $i
    	                  - given: Message
    	                    as: $m
    	                    having:
    	                    - size == $i
    	                  then: |
    	                    result.add( $m.getText() );""")
    			);
    }
    
    @ParameterizedTest
    @MethodSource("params")
    public void test1(String name, String content) {
        KieSession ksession = new KieHelper()
                .addContent(content, name)
                .build(ExecutableModelProject.class)
                .newKieSession();

        List<String> result = new ArrayList<>();
		ksession.setGlobal("result", result);
		
		ksession.insert(new Message("test"));
		ksession.insert(new Message("Hello World"));
		ksession.insert(10);
		ksession.insert(11);
		
		int count = ksession.fireAllRules();
		assertThat(count).isEqualTo(1);
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo("Hello World");
    }


}
