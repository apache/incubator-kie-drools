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

package org.drools.event;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.FactHandle;
import org.drools.WorkingMemory;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
public class WorkingMemoryEventSupportTest extends TestCase {
    public void testIsSerializable() {
        assertTrue( Serializable.class.isAssignableFrom( WorkingMemoryEventSupport.class ) );
    }

    public void testWorkingMemoryEventListener() {
        final RuleBase rb = RuleBaseFactory.newRuleBase();
        final WorkingMemory wm = rb.newStatefulSession();

        final List wmList = new ArrayList();
        final WorkingMemoryEventListener workingMemoryListener = new WorkingMemoryEventListener() {

            public void objectInserted(ObjectInsertedEvent event) {
                wmList.add( event );
            }

            public void objectUpdated(ObjectUpdatedEvent event) {
                wmList.add( event );
            }

            public void objectRetracted(ObjectRetractedEvent event) {
                wmList.add( event );
            }

        };

        wm.addEventListener( workingMemoryListener );

        final Cheese stilton = new Cheese( "stilton",
                                           15 );
        final Cheese cheddar = new Cheese( "cheddar",
                                           17 );

        final FactHandle stiltonHandle = wm.insert( stilton );

        ObjectInsertedEvent oae = (ObjectInsertedEvent) wmList.get( 0 );
        assertSame( stiltonHandle,
                    oae.getFactHandle() );

        wm.update( stiltonHandle,
                   stilton );
        final ObjectUpdatedEvent ome = (ObjectUpdatedEvent) wmList.get( 1 );
        assertSame( stiltonHandle,
                    ome.getFactHandle() );

        wm.retract( stiltonHandle );
        final ObjectRetractedEvent ore = (ObjectRetractedEvent) wmList.get( 2 );
        assertSame( stiltonHandle,
                    ore.getFactHandle() );

        final FactHandle cheddarHandle = wm.insert( cheddar );
        oae = (ObjectInsertedEvent) wmList.get( 3 );
        assertSame( cheddarHandle,
                    oae.getFactHandle() );
    }
}