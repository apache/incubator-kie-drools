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
        duration.setSnippet("1234");
        DRLOutput out = new DRLOutput();
        duration.renderDRL(out);
        String res = out.getDRL();
        System.out.println(res);
        assertEquals("\tduration 1234\n", res);

    }
    
}
