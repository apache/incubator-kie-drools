package org.drools.reteoo;
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

public class DefaultFactHandleFactoryTest extends TestCase {

    /*
     * Class under test for FactHandle newFactHandle()
     */
    public void testNewFactHandle() {
        DefaultFactHandleFactory factory = new DefaultFactHandleFactory();
        FactHandleImpl handle = (FactHandleImpl) factory.newFactHandle();
        assertEquals( 0,
                      handle.getId() );
        assertEquals( 0,
                      handle.getRecency() );

        handle = (FactHandleImpl) factory.newFactHandle();
        assertEquals( 1,
                      handle.getId() );
        assertEquals( 1,
                      handle.getRecency() );

        handle = (FactHandleImpl) factory.newFactHandle();
        assertEquals( 2,
                      handle.getId() );
        assertEquals( 2,
                      handle.getRecency() );
    }

    /*
     * Class under test for FactHandle newFactHandle(long)
     */
    public void testNewFactHandlelong() {
        DefaultFactHandleFactory factory = new DefaultFactHandleFactory();
        FactHandleImpl handle = (FactHandleImpl) factory.newFactHandle( 5 );
        assertEquals( 5,
                      handle.getId() );
        assertEquals( 0,
                      handle.getRecency() );

        handle = (FactHandleImpl) factory.newFactHandle( 3 );
        assertEquals( 3,
                      handle.getId() );
        assertEquals( 1,
                      handle.getRecency() );

        handle = (FactHandleImpl) factory.newFactHandle( 255 );
        assertEquals( 255,
                      handle.getId() );
        assertEquals( 2,
                      handle.getRecency() );
    }

}