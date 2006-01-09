package org.drools.event;

import java.io.Serializable;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
public class WorkingMemoryEventSupportTest extends TestCase {
    public void testIsSerializable() {
        assertTrue( Serializable.class.isAssignableFrom( WorkingMemoryEventSupport.class ) );
    }

    // TODO: Need more tests but I'M tired! Need sleep.
}