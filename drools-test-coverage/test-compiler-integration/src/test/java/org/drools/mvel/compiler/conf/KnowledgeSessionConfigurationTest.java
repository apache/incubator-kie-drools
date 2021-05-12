/*
 * Copyright 2008 Red Hat
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
package org.drools.mvel.compiler.conf;

import org.drools.core.BeliefSystemType;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.conf.AccumulateNullPropagationOption;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import static org.junit.Assert.*;


public class KnowledgeSessionConfigurationTest {

    private KieSessionConfiguration config;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        config = KieServices.Factory.get().newKieSessionConfiguration();
    }

    @Test
    public void testClockTypeConfiguration() {
        // setting the option using the type safe method
        config.setOption( ClockTypeOption.PSEUDO );

        // checking the type safe getOption() method
        assertEquals( ClockTypeOption.PSEUDO,
                      config.getOption( ClockTypeOption.class ) );
        // checking the string based getProperty() method
        assertEquals( "pseudo",
                      config.getProperty( ClockTypeOption.PROPERTY_NAME ) );

        // setting the options using the string based setProperty() method
        config.setProperty( ClockTypeOption.PROPERTY_NAME,
                            "realtime" );
        
        // checking the type safe getOption() method
        assertEquals( ClockTypeOption.REALTIME,
                      config.getOption( ClockTypeOption.class ) );
        // checking the string based getProperty() method
        assertEquals( "realtime",
                      config.getProperty( ClockTypeOption.PROPERTY_NAME ) );
    }


    @Test
    public void testBeliefSystemType() {
        config.setOption( BeliefSystemTypeOption.get( BeliefSystemType.JTMS.toString() ) );

        assertEquals( BeliefSystemTypeOption.get( BeliefSystemType.JTMS.toString() ),
                      config.getOption( BeliefSystemTypeOption.class ) );

        // checking the string based getProperty() method
        assertEquals( BeliefSystemType.JTMS.getId(),
                      config.getProperty( BeliefSystemTypeOption.PROPERTY_NAME ) );

        // setting the options using the string based setProperty() method
        config.setProperty( BeliefSystemTypeOption.PROPERTY_NAME,
                            BeliefSystemType.DEFEASIBLE.getId() );

        // checking the type safe getOption() method
        assertEquals( BeliefSystemTypeOption.get( BeliefSystemType.DEFEASIBLE.getId() ),
                      config.getOption( BeliefSystemTypeOption.class ) );
        // checking the string based getProperty() method
        assertEquals( BeliefSystemType.DEFEASIBLE.getId(),
                      config.getProperty( BeliefSystemTypeOption.PROPERTY_NAME ) );
    }

    @Test
    public void testAccumulateNullPropagation() {
        // false by default
        assertEquals(AccumulateNullPropagationOption.NO, config.getOption(AccumulateNullPropagationOption.class));
        assertEquals("false", config.getProperty(AccumulateNullPropagationOption.PROPERTY_NAME));

        config.setOption(AccumulateNullPropagationOption.YES);

        assertEquals(AccumulateNullPropagationOption.YES,
                     config.getOption(AccumulateNullPropagationOption.class));

        // checking the string based getProperty() method
        assertEquals("true",
                     config.getProperty(AccumulateNullPropagationOption.PROPERTY_NAME));

        // setting the options using the string based setProperty() method
        config.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME,
                           "false");

        // checking the type safe getOption() method
        assertEquals(AccumulateNullPropagationOption.NO,
                     config.getOption(AccumulateNullPropagationOption.class));
        // checking the string based getProperty() method
        assertEquals("false",
                     config.getProperty(AccumulateNullPropagationOption.PROPERTY_NAME));
    }
}
