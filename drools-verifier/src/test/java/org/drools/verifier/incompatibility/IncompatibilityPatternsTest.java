/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.incompatibility;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.*;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Incompatibility;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class IncompatibilityPatternsTest extends IncompatibilityBase {

    @Test
    public void testPatternsPossibilitiesIncompatibility() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("Patterns.drl"));

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        /*
         * Working pair
         */
        SubPattern pp1 = new SubPattern(pattern1,
                                        0);
        SubPattern pp2 = new SubPattern(pattern2,
                                        0);

        Restriction r1 = LiteralRestriction.createRestriction(pattern1,
                                                              "");
        pp1.add(r1);

        Restriction r2 = LiteralRestriction.createRestriction(pattern2,
                                                              "");
        pp2.add(r2);

        Restriction r3 = LiteralRestriction.createRestriction(pattern1,
                                                              "");
        pp1.add(r3);

        Restriction r4 = LiteralRestriction.createRestriction(pattern2,
                                                              "");
        pp2.add(r4);

        Incompatibility o1 = new Incompatibility(r1,
                                                 r2);
        Incompatibility o2 = new Incompatibility(r3,
                                                 r4);

        Pattern pattern3 = VerifierComponentMockFactory.createPattern(3);
        Pattern pattern4 = VerifierComponentMockFactory.createPattern(4);
        /*
         * Another working pair.
         */
        SubPattern pp3 = new SubPattern(pattern3,
                                        0);
        SubPattern pp4 = new SubPattern(pattern4,
                                        0);

        Restriction r5 = LiteralRestriction.createRestriction(pattern3,
                                                              "");
        pp3.add(r5);

        Restriction r6 = LiteralRestriction.createRestriction(pattern4,
                                                              "");
        pp4.add(r6);

        Restriction r7 = LiteralRestriction.createRestriction(pattern3,
                                                              "");
        pp3.add(r7);

        Restriction r8 = LiteralRestriction.createRestriction(pattern4,
                                                              "");
        pp4.add(r8);

        Incompatibility o3 = new Incompatibility(r5,
                                                 r6);

        data.add(r1);
        data.add(r2);
        data.add(r3);
        data.add(r4);
        data.add(r5);
        data.add(r6);
        data.add(r7);
        data.add(r8);
        data.add(pp1);
        data.add(pp2);
        data.add(pp3);
        data.add(pp4);
        data.add(o1);
        data.add(o2);
        data.add(o3);

        for (Object o : data) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Incompatible Patterns"));

        Map<Cause, Set<Cause>> map = createIncompatibilityMap(VerifierComponentType.SUB_PATTERN,
                                                              (Iterator<Object>)session.getObjects().iterator());

        assertTrue((TestBaseOld.causeMapContains(map,
                                                 pp1,
                                                 pp2) ^ TestBaseOld.causeMapContains(map,
                                                                                     pp2,
                                                                                     pp1)));
        assertTrue((TestBaseOld.causeMapContains(map,
                                                 pp3,
                                                 pp4) ^ TestBaseOld.causeMapContains(map,
                                                                                     pp4,
                                                                                     pp3)));

        if (!map.isEmpty()) {
            fail("More opposites than was expected.");
        }
    }
}
