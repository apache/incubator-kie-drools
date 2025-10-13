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
package org.drools.mvel.integrationtests.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

public class QueryWithErrorsTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithIncompatibleArgs(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl ="""
        		global java.util.List list;
                
                query 
        			foo(String $s, String $s, String $s)
        		end
        		
        		rule React
        		when
        			$i : Integer()
        			foo($i, $x, $i ;)
        		then
        		end
        		""";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors).hasSize(2);
    }
    

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithSyntaxError(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl = """
        		global java.util.List list;
        		query 
        			foo(Integer $i) 
        		end
        		rule React
        		when
        			$i : Integer()
        			foo($i)  // missing ";" should result in 1 compilation error
        		then
        		end
        		""";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors).hasSize(1);
    }
    


    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithWrongParamNumber(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl = """
    			global java.util.List list;
    			query foo(Integer $i) 
                 
    			end
    			rule React
    			when
    				$i : Integer()
    				$j : Integer()
    				foo($i, $j ;)
    			then
    			end
                 """;

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors).hasSize(1);
    }


    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNotExistingDeclarationInQuery(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-414
        String drl ="""
        		import org.drools.compiler.Person;
        		global java.util.List persons;

        		query checkLength(String $s, int $l)
        		    $s := String(length == $l)
        		end

        		rule R when
					$i : Integer()
					$p : Person()
					checkLength($p.name, 1 + $x + $p.age;)
        		then
        		    persons.add($p);
        		end\n"
        		""";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors).as("Should have an error").isNotEmpty();
    }


}
