package org.drools.leaps;

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

import junit.framework.TestCase;

import org.drools.FactException;
import org.drools.FactHandle;

/**
 * 
 * @author Alexander Bagerman
 *
 */
public class FactHandleImplTest extends TestCase {

    private LeapsRuleBase ruleBase;

    protected void setUp() throws Exception {
        super.setUp();
        this.ruleBase = new LeapsRuleBase();
    }

    /*
     * Test method for 'leaps.LeapsFactHandle.getId()'
     */
    public void testGetId() {
        final LeapsWorkingMemory memory = (LeapsWorkingMemory) this.ruleBase.newWorkingMemory();

        try {
            final FactHandle fh1 = memory.assertObject( "object1" );
            assertEquals( ((FactHandleImpl) fh1).getId(),
                          ((FactHandleImpl) memory.getFactHandleFactory().newFactHandle( "dummy" )).getId() - 1 );
        } catch ( final FactException fe ) {
        }
    }

    /*
     * Test method for 'leaps.LeapsFactHandle.equals(Object)'
     */
    public void testEqualsObject() {
        // they equal on id, am not sure how to simulate it yet
    }

}