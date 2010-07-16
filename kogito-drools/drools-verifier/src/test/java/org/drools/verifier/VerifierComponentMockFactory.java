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

import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.VerifierRule;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierComponentMockFactory {

    public static RulePackage createPackage1() {
        RulePackage rulePackage = new RulePackage();

        rulePackage.setName( "testPackage1" );

        return rulePackage;
    }

    public static VerifierRule createRule1() {
        return createRule( 1 );
    }

    public static Pattern createPattern1() {
        return createPattern( 1 );
    }

    public static VerifierRule createRule2() {
        return createRule( 2 );
    }

    public static Pattern createPattern2() {
        return createPattern( 2 );
    }

    public static VerifierRule createRule(int i) {
        VerifierRule rule = new VerifierRule( createPackage1() );

        rule.setName( "testRule" + i );

        return rule;
    }

    public static Pattern createPattern(int i) {
        Pattern pattern = new Pattern( createRule( i ) );
        pattern.setObjectTypePath( "objectType" + i );

        pattern.setName( "testPattern" + i );

        return pattern;
    }
}
