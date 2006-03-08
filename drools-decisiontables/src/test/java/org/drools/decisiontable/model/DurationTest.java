/*
 * Created on 20/08/2005
 */
package org.drools.decisiontable.model;

import junit.framework.TestCase;

public class DurationTest extends TestCase
{

    /**
     * Test basic rendering and parsing of arguments
     */
    public void testDurationRender() {
        Duration duration = new Duration();
        duration.setSnippet("H2,M30,S30");
        String res = duration.toXML();        
        assertTrue(res.indexOf("duration hours=\"2\" minutes=\"30\" seconds=\"30\"") > 0);

        duration.setSnippet("M30");
        res = duration.toXML();        
        assertTrue(res.indexOf("duration minutes=\"30\"") > 0);
    }
    
}
