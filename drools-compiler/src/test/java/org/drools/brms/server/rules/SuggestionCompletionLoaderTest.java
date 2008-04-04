package org.drools.brms.server.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

public class SuggestionCompletionLoaderTest extends TestCase {

    public void testSuggestionCompLoader() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person", new ArrayList(), new ArrayList() );
        assertNotNull(eng);

    }


    public void testLoadDifferentFieldTypes() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.brms.server.rules.SomeFact", new ArrayList(), new ArrayList() );
        assertNotNull(eng);

        assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, eng.getFieldType( "SomeFact", "age" ));
        assertEquals(SuggestionCompletionEngine.TYPE_STRING, eng.getFieldType( "SomeFact", "likes"));
        assertEquals(SuggestionCompletionEngine.TYPE_STRING, eng.getFieldType( "SomeFact","name"));
        assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, eng.getFieldType("SomeFact", "bigDecimal"));
        assertEquals(SuggestionCompletionEngine.TYPE_COMPARABLE, eng.getFieldType( "SomeFact", "date"));
        assertEquals(SuggestionCompletionEngine.TYPE_OBJECT, eng.getFieldType( "SomeFact", "cheese"));
        assertEquals(SuggestionCompletionEngine.TYPE_BOOLEAN, eng.getFieldType( "SomeFact", "dead"));
        assertEquals(SuggestionCompletionEngine.TYPE_BOOLEAN, eng.getFieldType( "SomeFact", "alive"));

    }

    public void testGlobal() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n global org.drools.Person p", new ArrayList(), new ArrayList() );
        assertNotNull(eng);
        assertFalse(loader.hasErrors());

        assertEquals(1, eng.getGlobalVariables().length);
        assertEquals("p", eng.getGlobalVariables()[0]);
        assertEquals("Person", eng.globalTypes.get("p"));
        String[] flds = (String[]) eng.fieldsForType.get("Person");
        assertNotNull(flds);

    }

    public void testSortOrderOfFields() throws Exception {

	    SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
	    SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.brms.server.rules.SomeFact", new ArrayList(), new ArrayList() );
	    assertNotNull(eng);

	    String[] fields = eng.getFieldCompletions("SomeFact");

	    assertEquals("age", fields[0]);
	    assertEquals("alive", fields[1]);
	    assertEquals("bigDecimal", fields[2]);

    }

    public void testSortOrderOfFacts() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.brms.server.rules.SomeFact\n import org.drools.Person", new ArrayList(), new ArrayList() );
        assertNotNull(eng);
        String[] facts  = eng.getFactTypes();
        assertEquals(2, facts.length);

        assertEquals("Person", facts[0]);
        assertEquals("SomeFact", facts[1]);


    }


    public void testLoaderWithExistingClassloader() throws Exception {
        MockClassLoader mcl = new MockClassLoader();
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader(mcl);
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.foo.Bar", new ArrayList(), new ArrayList() );
        assertNotNull(eng);
        assertNotNull(eng.dataEnumLists);
        assertTrue(mcl.called);

    }


    static class MockClassLoader extends ClassLoader {

        public boolean called = false;

        public Class loadClass(String name) {
            called = true;
            return Object.class;
        }

    }

}
