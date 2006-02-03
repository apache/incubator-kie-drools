package org.drools.util.proxy;

import junit.framework.TestCase;

public class FieldExtractorTest extends TestCase {

    public void testIt() throws Exception {
        FieldOrderInspector ext = new FieldOrderInspector( Person.class );
        assertTrue(ext.getPropertyGetters().size() > 0);
    }
    
}
