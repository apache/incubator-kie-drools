package org.drools.guvnor.client.modeldriven;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.guvnor.server.rules.SuggestionCompletionLoader;

public class SuggestionCompletionEngine2Test extends TestCase {

    public void testArrayList() {

        String pkg = "package org.test\n global java.util.ArrayList testList";
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        SuggestionCompletionEngine engine = loader.getSuggestionEngine( pkg,
                                                                        new ArrayList(),
                                                                        new ArrayList(),
                                                                        new ArrayList() );

        List<String> methodNames = engine.getMethodNames( "ArrayList" );

        //        for ( String name : methodNames ) {
        //            System.out.println( name );
        //        }

        assertNotNull( methodNames );
        assertFalse( methodNames.isEmpty() );
        assertEquals( 12,
                      methodNames.size() );

    }

    /*
     *
     * TODO: Check something.set(int index, Object o)!
     *
     */

}
