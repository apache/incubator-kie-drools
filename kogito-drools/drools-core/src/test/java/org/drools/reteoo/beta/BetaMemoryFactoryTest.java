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

package org.drools.reteoo.beta;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BetaNodeBinder;
import org.drools.spi.FieldConstraint;
import org.drools.spi.MockConstraint;

/**
 * 
 * BetaMemoryFactoryTest
 * An unit test for the BetaMemoryFactory
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 28/02/2006
 */
public class BetaMemoryFactoryTest extends TestCase {
    BetaNodeBinder binder = null;
    RuleBaseConfiguration config;

    protected void setUp() throws Exception {
        super.setUp();
        config = new RuleBaseConfiguration();
        FieldConstraint[] constraints = new FieldConstraint[]{new MockConstraint()};
        binder = new BetaNodeBinder( constraints );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaMemoryFactory.newLeftMemory(BetaNodeBinder)'
     */
    public void testNewLeftMemory() {
        try {
            BetaLeftMemory memory = BetaMemoryFactory.newLeftMemory( config, null );

            Assert.assertNotNull( "BetaMemoryFactory should not return null",
                                  memory );
            Assert.assertTrue( "Without constraints, BetaMemoryFactory should " + "return an instance of DefaultLeftMemory",
                               memory instanceof DefaultLeftMemory );

            memory = BetaMemoryFactory.newLeftMemory( config, binder );
            Assert.assertNotNull( "BetaMemoryFactory should not return null",
                                  memory );

        } catch ( Exception e ) {
            Assert.fail( "BetaLeftMemory is not supposed to throw exceptions" );
        }
    }

    /*
     * Test method for 'org.drools.reteoo.beta.BetaMemoryFactory.newRightMemory(BetaNodeBinder)'
     */
    public void testNewRightMemory() {
        try {
            BetaRightMemory memory = BetaMemoryFactory.newRightMemory( config, null );

            Assert.assertNotNull( "BetaMemoryFactory should not return null",
                                  memory );
            Assert.assertTrue( "Without constraints, BetaMemoryFactory should " + "return an instance of DefaultRightMemory",
                               memory instanceof DefaultRightMemory );

            memory = BetaMemoryFactory.newRightMemory( config, binder );
            Assert.assertNotNull( "BetaMemoryFactory should not return null",
                                  memory );

        } catch ( Exception e ) {
            Assert.fail( "BetaLeftMemory is not supposed to throw exceptions" );
        }
    }

}
