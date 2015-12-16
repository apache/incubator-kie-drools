/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.verifier.redundancy;

import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.builder.VerifierImpl;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.Subsumption;
import org.junit.Test;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;

import java.util.Collection;

import static org.junit.Assert.*;

public class RedundancyTest {

    @Test
    public void testVerifierLiteralRestrictionRedundancy() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("RedundantRestrictions.drl",
                getClass()),
                                      ResourceType.DRL);

        assertFalse(verifier.hasErrors());

        boolean noProblems = verifier.fireAnalysis();
        assertTrue(noProblems);

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));
        Collection<? extends Object> redundancyList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Redundancy.class));

        assertEquals(2,
                     subsumptionList.size());
        assertEquals(1,
                     redundancyList.size());

        verifier.dispose();
    }
}
