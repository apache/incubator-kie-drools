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

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
public class WorkingMemoryEventSupportTest extends TestCase {
    public void testIsSerializable() {
        assertTrue( Serializable.class.isAssignableFrom( WorkingMemoryEventSupport.class ) );
    }

    public void testWorkingMemoryEventListener() {
        RuleBase rb = RuleBaseFactory.newRuleBase();
        WorkingMemory wm = rb.newWorkingMemory();

        final List wmList = new ArrayList();
        WorkingMemoryEventListener workingMemoryListener = new WorkingMemoryEventListener() {

            public void objectAsserted(ObjectAssertedEvent event) {
                wmList.add( event );
            }

            public void objectModified(ObjectModifiedEvent event) {
                wmList.add( event );
            }

            public void objectRetracted(ObjectRetractedEvent event) {
                wmList.add( event );
            }

        };

        wm.addEventListener( workingMemoryListener );

        Cheese stilton = new Cheese( "stilton",
                                     15 );
        Cheese cheddar = new Cheese( "cheddar",
                                     17 );

        FactHandle stiltonHandle = wm.assertObject( stilton );

        ObjectAssertedEvent oae = (ObjectAssertedEvent) wmList.get( 0 );
        assertSame( stiltonHandle,
                    oae.getFactHandle() );

        wm.modifyObject( stiltonHandle,
                         stilton );
        ObjectModifiedEvent ome = (ObjectModifiedEvent) wmList.get( 1 );
        assertSame( stiltonHandle,
                    ome.getFactHandle() );

        wm.retractObject( stiltonHandle );
        ObjectRetractedEvent ore = (ObjectRetractedEvent) wmList.get( 2 );
        assertSame( stiltonHandle,
                    ore.getFactHandle() );

        wm.assertObject( cheddar );
    }
}