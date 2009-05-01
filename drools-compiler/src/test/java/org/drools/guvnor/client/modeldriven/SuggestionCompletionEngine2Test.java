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
        for ( String s : methodNames ) {
            assertFalse( "Method " + s + " is not allowed.",
                         allowedMethod( s ) );
        }

    }

    private boolean allowedMethod(String methodName) {
        return ("hashCode".equals( methodName ) || "equals".equals( methodName ) || "listIterator".equals( methodName ) || "lastIndexOf".equals( methodName ) || "indexOf".equals( methodName ) || "subList".equals( methodName )
                || "get".equals( methodName ) || "isEmpty".equals( methodName ) || "containsKey".equals( methodName ) || "values".equals( methodName ) || "entrySet".equals( methodName ) || "containsValue".equals( methodName )
                || "keySet".equals( methodName ) || "size".equals( methodName ) || "toArray".equals( methodName ) || "iterator".equals( methodName ) || "contains".equals( methodName ) || "isEmpty".equals( methodName )
                || "containsAll".equals( methodName ) || "size".equals( methodName ));
    }
}
