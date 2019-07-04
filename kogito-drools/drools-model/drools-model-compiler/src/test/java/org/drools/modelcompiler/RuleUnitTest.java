/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler;

import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.ruleunit.AdultUnit;
import org.drools.modelcompiler.ruleunit.AdultUnitInstance;
import org.junit.Test;
import org.kie.kogito.rules.DataSource;
import org.drools.core.ruleunit.impl.ListDataStream;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertTrue;

public class RuleUnitTest extends BaseModelTest {

    public RuleUnitTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testRuleUnit() {
        String str =
            "package org.drools.modelcompiler.ruleunit;\n" +
            "unit AdultUnit\n" +
            "import " + Person.class.getCanonicalName() + "\n" +
            "rule Adult when\n" +
            "    $p: /persons[ age >= adultAge ]" +
            "then\n" +
            "    results.add($p.getName());" +
            "end ";

        DataSource<Person> persons = new ListDataStream<>(
                new Person( "Mario", 45 ),
                new Person( "Marilena", 47 ),
                new Person( "Sofia", 7 ) );

        AdultUnit unit = new AdultUnit( persons, 21 );
        AdultUnitInstance unitInstance = new AdultUnitInstance( unit, getKieSession(str) );

        unitInstance.fire();

        assertTrue( unit.getResults().containsAll( asList("Mario", "Marilena") ) );
    }

}
