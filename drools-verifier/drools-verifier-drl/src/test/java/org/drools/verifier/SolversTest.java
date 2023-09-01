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
package org.drools.verifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RuleComponent;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.solver.Solvers;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SolversTest {

    /**
     * <pre>
     * when 
     *     Foo( r &amp;&amp; r2 )
     *     and
     *     not Foo( r3 &amp;&amp; r4 )
     * </pre>
     * 
     * result:<br>
     * r && r2<br>
     * r3 && r4
     */
    @Test
    void testNotAnd() {
        PackageDescr descr = new PackageDescr("testPackage");
        RulePackage rulePackage = new RulePackage(descr);
        rulePackage.setName("testPackage");

        VerifierRule rule = new VerifierRule(descr, rulePackage, new HashMap<String, Object>());
        rule.setName("testRule");
        Pattern pattern = new Pattern(new PatternDescr(), rule );

        Restriction r = LiteralRestriction.createRestriction(pattern,
                "");
        Restriction r2 = LiteralRestriction.createRestriction(pattern,
                "");
        Restriction r3 = LiteralRestriction.createRestriction(pattern,
                "");
        Restriction r4 = LiteralRestriction.createRestriction(pattern,
                "");
        Solvers solvers = new Solvers();

        solvers.startRuleSolver(rule);

        solvers.startOperator(OperatorDescrType.AND);
        solvers.startPatternSolver(pattern);
        solvers.startOperator(OperatorDescrType.AND);
        solvers.addPatternComponent(r);
        solvers.addPatternComponent(r2);
        solvers.endOperator();
        solvers.endPatternSolver();

        solvers.startNot();
        solvers.startPatternSolver(pattern);
        solvers.startOperator(OperatorDescrType.AND);
        solvers.addPatternComponent(r3);
        solvers.addPatternComponent(r4);
        solvers.endOperator();
        solvers.endPatternSolver();
        solvers.endNot();

        solvers.endOperator();

        solvers.endRuleSolver();

        List<SubRule> list = solvers.getRulePossibilities();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getItems().size()).isEqualTo(2);

        List<Restriction> result = new ArrayList<Restriction>();
        result.add(r);
        result.add(r2);

        List<Restriction> result2 = new ArrayList<Restriction>();
        result2.add(r3);
        result2.add(r4);

        Object[] possibilies = list.get(0).getItems().toArray();
        SubPattern p1 = (SubPattern) possibilies[0];
        SubPattern p2 = (SubPattern) possibilies[1];

        /*
         * Order may change but it doesn't matter.
         */
        if (p1.getItems().containsAll(result)) {
            assertThat(p2.getItems().containsAll(result2)).isTrue();
        } else if (p1.getItems().containsAll(result2)) {
            assertThat(p2.getItems().containsAll(result)).isTrue();
        } else {
            fail("No items found.");
        }
    }

    /**
     * <pre>
     * when 
     *     Foo( descr &amp;&amp; descr2 )
     * </pre>
     * 
     * result:<br>
     * descr && descr2
     */
    @Test
    void testBasicAnd() {

        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        Restriction r = LiteralRestriction.createRestriction(pattern,
                "");
        Restriction r2 = LiteralRestriction.createRestriction(pattern,
                "");

        Solvers solvers = new Solvers();

        solvers.startRuleSolver(rule);
        solvers.startPatternSolver(pattern);
        solvers.startOperator(OperatorDescrType.AND);
        solvers.addPatternComponent(r);
        solvers.addPatternComponent(r2);
        solvers.endOperator();
        solvers.endPatternSolver();
        solvers.endRuleSolver();

        List<SubRule> list = solvers.getRulePossibilities();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getItems().size()).isEqualTo(1);

        List<Restriction> result = new ArrayList<Restriction>();
        result.add(r);
        result.add(r2);

        Set<RuleComponent> set = list.get(0).getItems();
        for (RuleComponent component : set) {
            SubPattern possibility = (SubPattern) component;
            assertThat(possibility.getItems().containsAll(result)).isTrue();
        }
    }
}
