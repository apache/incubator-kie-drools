package org.drools.guvnor.client.modeldriven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarInputStream;

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

    public void testJarFileWithSourceFiles() {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        // Add jar file
        String header = "package foo \n import org.test.Person \n import org.test.Banana \n ";
        List jars = new ArrayList();
        JarInputStream jis = null;
        SuggestionCompletionEngine suggestionCompletionEngine = null;

        try {
            jis = new JarInputStream( this.getClass().getResourceAsStream( "/JarWithSourceFiles.jar" ) );
            jars.add( jis );
        } catch ( IOException e ) {
            fail( "Could not load jar" );
        }

        try {
            suggestionCompletionEngine = loader.getSuggestionEngine( header,
                                                                     jars,
                                                                     new ArrayList() );
        } catch ( ClassFormatError e ) {
            fail( "Can not load suggestions " + e );
        }

        // Check that it throws no error and has class files inside.
        assertNotNull( jis );
        assertNotNull( suggestionCompletionEngine );
        assertEquals( 2,
                      suggestionCompletionEngine.getFactTypes().length );

        List<String> list = Arrays.asList( suggestionCompletionEngine.getFactTypes() );

        assertTrue( list.contains( "Person" ) );
        assertTrue( list.contains( "Banana" ) );

    }

    private boolean allowedMethod(String methodName) {
        return ("hashCode".equals( methodName ) || "equals".equals( methodName ) || "listIterator".equals( methodName ) || "lastIndexOf".equals( methodName ) || "indexOf".equals( methodName ) || "subList".equals( methodName )
                || "get".equals( methodName ) || "isEmpty".equals( methodName ) || "containsKey".equals( methodName ) || "values".equals( methodName ) || "entrySet".equals( methodName ) || "containsValue".equals( methodName )
                || "keySet".equals( methodName ) || "size".equals( methodName ) || "toArray".equals( methodName ) || "iterator".equals( methodName ) || "contains".equals( methodName ) || "isEmpty".equals( methodName )
                || "containsAll".equals( methodName ) || "size".equals( methodName ));
    }
}
