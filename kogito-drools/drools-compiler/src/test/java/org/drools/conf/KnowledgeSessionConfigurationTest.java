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
 *
 */
package org.drools.conf;

import junit.framework.TestCase;

import org.drools.KnowledgeBaseFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.conf.ClockTypeOption;

/**
 * @author etirelli
 *
 */
public class KnowledgeSessionConfigurationTest extends TestCase {

    private KnowledgeSessionConfiguration config;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
    }

    public void testClockTypeConfiguration() {
        // setting the option using the type safe method
        config.setOption( ClockTypeOption.get("pseudo") );

        // checking the type safe getOption() method
        assertEquals( ClockTypeOption.get("pseudo"),
                      config.getOption( ClockTypeOption.class ) );
        // checking the string based getProperty() method
        assertEquals( "pseudo",
                      config.getProperty( ClockTypeOption.PROPERTY_NAME ) );

        // setting the options using the string based setProperty() method
        config.setProperty( ClockTypeOption.PROPERTY_NAME,
                            "realtime" );
        
        // checking the type safe getOption() method
        assertEquals( ClockTypeOption.get("realtime"),
                      config.getOption( ClockTypeOption.class ) );
        // checking the string based getProperty() method
        assertEquals( "realtime",
                      config.getProperty( ClockTypeOption.PROPERTY_NAME ) );
    }
    

}
