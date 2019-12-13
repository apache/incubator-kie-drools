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

import org.kie.kogito.rules.units.ListDataStream;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.ruleunit.AdultUnit;
import org.drools.modelcompiler.ruleunit.AdultUnitInstance;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.DataSource;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class RuleUnitTest {

//    public RuleUnitTest( RUN_TYPE testRunType ) {
//        super( testRunType );
//    }

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

        DataSource<Person> persons = ListDataStream.create(
                new Person( "Mario", 45 ),
                new Person( "Marilena", 47 ),
                new Person( "Sofia", 7 ) );

        AdultUnit unit = new AdultUnit( persons, 21 );
        AdultUnitInstance unitInstance = new AdultUnitInstance( unit, getKieSession(str) );

        unitInstance.fire();

        assertTrue(unit.getResults().containsAll(asList("Mario", "Marilena") ) );
    }

    private KieSession getKieSession(String str) {
        return null;
    }
}
