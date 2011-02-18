/**
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

package org.drools.verifier.overlaps;

import java.util.Collection;

import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ClassObjectFilter;
import org.drools.verifier.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.builder.VerifierImpl;
import org.drools.verifier.report.components.Overlap;

public class OverlappingRestrictionsTest extends TestBase {

	// TODO: Add this feature
    @Test @Ignore
	public void testOverlap() {
		VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

		Verifier verifier = vBuilder.newVerifier();

		verifier.addResourcesToVerify(ResourceFactory.newClassPathResource(
				"RestrictionsTest.drl", getClass()), ResourceType.DRL);

		assertFalse(verifier.hasErrors());

		boolean noProblems = verifier.fireAnalysis();
		assertTrue(noProblems);

		Collection<Object> overlaps = ((VerifierImpl) verifier)
				.getKnowledgeSession().getObjects(
						new ClassObjectFilter(Overlap.class));

		for (Object object : overlaps) {
			System.out.println(object);
		}

		assertEquals(3, overlaps.size());

		verifier.dispose();

	}

    @Test
    public void testDUMMY() throws Exception {
		assertTrue(true);
	}
}
