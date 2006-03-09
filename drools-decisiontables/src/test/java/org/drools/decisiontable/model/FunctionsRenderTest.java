/*
 * Created on 24/07/2005
 */
package org.drools.decisiontable.model;

import junit.framework.TestCase;

public class FunctionsRenderTest extends TestCase
{

    public void testFunctionRender() {
        Functions func = new Functions();
        
        DRLOutput out = new DRLOutput();
        func.renderDRL(out);
        
        assertEquals("", out.toString());
        
        func.setFunctionsListing("function myFunction() {}");
        out = new DRLOutput();
        func.renderDRL(out);
        String s = out.toString();
        assertEquals("function myFunction() {}\n", s);
    }
    
}
