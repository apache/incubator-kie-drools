package org.drools.guvnor.modeldriven;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ActionFieldValue;

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
