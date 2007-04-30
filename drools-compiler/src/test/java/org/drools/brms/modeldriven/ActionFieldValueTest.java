package org.drools.brms.modeldriven;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.ActionFieldValue;

import junit.framework.TestCase;

public class ActionFieldValueTest extends TestCase {

    public void testFormula() {
        ActionFieldValue val = new ActionFieldValue( "x",
                                                     "y",
                                                     SuggestionCompletionEngine.TYPE_NUMERIC );
        assertFalse( val.isFormula() );
        val = new ActionFieldValue( "x",
                                    "=y * 20",
                                    SuggestionCompletionEngine.TYPE_NUMERIC );
        assertTrue( val.isFormula() );
    }

}
