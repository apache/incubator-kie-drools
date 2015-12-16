/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.testscenarios.backend;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.rule.Match;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RuleCoverageListenerTest {

    @Test
    public void testCoverage() throws Exception {
        // configuring mock event
        AfterMatchFiredEvent amfe = mock( AfterMatchFiredEvent.class );
        Match match = mock( Match.class );
        Rule rule = mock( Rule.class );
        when( amfe.getMatch() ).thenReturn( match );
        when( match.getRule() ).thenReturn( rule );
        when( rule.getName() ).thenReturn( "rule1" ).thenReturn( "rule2" ).thenReturn( "rule3" );

        HashSet<String> rules = new HashSet<String>();
        rules.add( "rule1" );
        rules.add( "rule2" );
        rules.add( "rule3" );

        RuleCoverageListener ls = new RuleCoverageListener( rules );
        Assert.assertEquals( 3,
                             ls.rules.size() );
        Assert.assertEquals( 0,
                             ls.getPercentCovered() );

        ls.afterMatchFired( amfe );
        Assert.assertEquals( 2,
                             ls.rules.size() );
        assertTrue( ls.rules.contains( "rule2" ) );
        assertTrue( ls.rules.contains( "rule3" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        Assert.assertEquals( 33,
                             ls.getPercentCovered() );

        ls.afterMatchFired( amfe );
        Assert.assertEquals( 1,
                             ls.rules.size() );
        assertFalse( ls.rules.contains( "rule2" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        assertTrue( ls.rules.contains( "rule3" ) );

        Assert.assertEquals( 66,
                             ls.getPercentCovered() );

        ls.afterMatchFired( amfe );
        Assert.assertEquals( 0,
                             ls.rules.size() );
        assertFalse( ls.rules.contains( "rule2" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        assertFalse( ls.rules.contains( "rule3" ) );

        Assert.assertEquals( 100,
                             ls.getPercentCovered() );

    }

}

