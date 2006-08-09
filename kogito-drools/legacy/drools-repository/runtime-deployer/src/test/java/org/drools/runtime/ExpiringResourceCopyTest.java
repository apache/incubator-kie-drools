package org.drools.runtime;

import junit.framework.TestCase;

public class ExpiringResourceCopyTest extends TestCase {

    public void testExpire() throws Exception {
        ExpiringResourceCopy res = new ExpiringResourceCopy(1);
        assertTrue(res.isExpired());
        byte[] data = "boo".getBytes();
        res.setData( data );
        assertFalse(res.isExpired());
        assertEquals(data, res.getData());
        Thread.sleep( 2000 );
        assertTrue(res.isExpired());
        res.setData( data );
        assertFalse(res.isExpired());
        
        
    }
    
}
