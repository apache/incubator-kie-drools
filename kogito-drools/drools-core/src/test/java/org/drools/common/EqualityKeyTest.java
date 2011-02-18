/*
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

package org.drools.common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.reteoo.ReteooFactHandleFactory;

public class EqualityKeyTest {
    @Test
    public void test1() {
        ReteooFactHandleFactory factory = new ReteooFactHandleFactory();
        
        InternalFactHandle ch1 = factory.newFactHandle( new Cheese ("c", 10), null, null, null );
        EqualityKey key = new EqualityKey( ch1 );
        
        assertSame( ch1, key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
        
        InternalFactHandle ch2 = factory.newFactHandle( new Cheese ("c", 10), null, null, null );
        key.addFactHandle( ch2 );
        
        assertEquals( 1, key.getOtherFactHandle().size() );
        assertEquals( ch2, key.getOtherFactHandle().get( 0 ) );
        
        key.removeFactHandle( ch1 );
        assertSame( ch2, key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
        
        key.removeFactHandle( ch2 );
        assertNull( key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
        
        key = new EqualityKey( ch2 );
        key.addFactHandle( ch1 );
        assertSame( ch2, key.getFactHandle() );
        assertEquals( 1, key.getOtherFactHandle().size() );
        assertEquals( ch1, key.getOtherFactHandle().get( 0 ) );
        
        key.removeFactHandle( ch1 );
        assertSame( ch2, key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
        
        key.removeFactHandle( ch2 );
        assertNull( key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
    }
}
