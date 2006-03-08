/*
 * Created on 24/07/2005
 */
package org.drools.decisiontable.model;

import junit.framework.TestCase;

public class FunctionsRenderTest extends TestCase
{

    public void testFunctionRender() {
        Functions func = new Functions();
        assertEquals("", func.toXML());
        
        func.setFunctionsListing("something");
        String s = func.toXML();
        assertTrue(s.indexOf("CDATA") > -1);
        assertTrue(s.indexOf("something") > -1);
    }
    
}
