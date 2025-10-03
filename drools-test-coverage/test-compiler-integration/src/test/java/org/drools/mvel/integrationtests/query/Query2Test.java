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

import java.util.stream.Stream;

import org.drools.mvel.compiler.Order;
import org.drools.mvel.compiler.OrderItem;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class Query2Test {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testEvalRewrite(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String drl = """
    	    package org.drools.mvel.compiler;
    	    global java.util.List results;

    	    rule "eval rewrite"
    	        when
    	            $o1 : OrderItem(order.number == 11, $seq : seq == 1)
    	    //        $o2 : OrderItem(order.number == $o1.order.number, seq != $seq)
    	            $o2 : Order(items[(Integer) 1] == $o1) 
    	        then
    	            System.out.println($o1 + ":" + $o2);
    	    end        
    	    """;

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Order order1 = new Order(11, "Bob");
        final OrderItem item11 = new OrderItem(order1, 1);
        final OrderItem item12 = new OrderItem(order1, 2);

        ksession.insert(order1);
        ksession.insert(item11);
        ksession.insert(item12);
        
        ksession.fireAllRules();
    }
}
