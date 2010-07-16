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

package org.drools.verifier;

import junit.framework.Assert;

import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Test;

/**
 * 
 * @author rikkola
 *
 */
public class RangeCheckTest {

    @Test
    public void testVerifier() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        Assert.assertFalse( vBuilder.hasErrors() );
        Assert.assertEquals( 0,
                             vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( new ClassPathResource( "RangeTest.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        Assert.assertFalse( verifier.hasErrors() );
        Assert.assertEquals( 0,
                             verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        Assert.assertTrue( works );

        VerifierReport result = verifier.getResult();
        Assert.assertNotNull( result );

        for ( VerifierMessageBase message : result.getBySeverity( Severity.ERROR ) ) {
            System.out.println( message );
        }

        // This rule should not have errors, evereververevernever!
        Assert.assertEquals( 0,
                             result.getBySeverity( Severity.ERROR ).size() );

    }

}
