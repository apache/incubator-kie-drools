package org.drools.guvnor.server.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;

public class SuggestionCompletionLoaderTest extends TestCase {

    public void testSuggestionCompLoader() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person", new ArrayList(), new ArrayList() );
        assertNotNull(eng);

    }


    public void testSuggestionCompLoaderWildcards() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        loader.getSuggestionEngine( "package foo \n import org.drools.*", new ArrayList(), new ArrayList() );
        assertEquals(1, loader.getErrors().size());
        String err = loader.getErrors().get(0);
        assertTrue(err.startsWith("Unable"));


    }



    public void testLoadDifferentFieldTypes() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.guvnor.server.rules.SomeFact", new ArrayList(), new ArrayList() );
        assertNotNull(eng);

        assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, eng.getFieldType( "SomeFact", "age" ));
        assertEquals(SuggestionCompletionEngine.TYPE_STRING, eng.getFieldType( "SomeFact", "likes"));
        assertEquals(SuggestionCompletionEngine.TYPE_STRING, eng.getFieldType( "SomeFact","name"));
        assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, eng.getFieldType("SomeFact", "bigDecimal"));
//        assertEquals(SuggestionCompletionEngine.TYPE_COMPARABLE, eng.getFieldType( "SomeFact", "date"));
        assertEquals(SuggestionCompletionEngine.TYPE_DATE, eng.getFieldType( "SomeFact", "date"));
        assertEquals("Cheese", eng.getFieldType( "SomeFact", "cheese"));
        assertEquals(SuggestionCompletionEngine.TYPE_BOOLEAN, eng.getFieldType( "SomeFact", "dead"));
        assertEquals(SuggestionCompletionEngine.TYPE_BOOLEAN, eng.getFieldType( "SomeFact", "alive"));
        assertEquals(SuggestionCompletionEngine.TYPE_COLLECTION,eng.getFieldType( "SomeFact", "factList"));
        assertEquals("SomeFact",eng.getParametricFieldType("SomeFact", "factList"));
        assertEquals(SuggestionCompletionEngine.TYPE_COLLECTION,eng.getFieldType( "SomeFact", "factListString"));
        assertEquals("String",eng.getParametricFieldType("SomeFact", "factListString"));

    }

    public void testGeneratedBeans() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n declare GenBean \n   id: int \n name : String \n end \n declare GenBean2 \n list: java.util.List \n gb: GenBean \n end", new ArrayList(), new ArrayList() );
        assertFalse(loader.hasErrors());
        assertNotNull(eng);

        assertEquals(2, eng.factTypes.length);
        assertEquals("GenBean", eng.factTypes[0]);
        assertEquals("GenBean2", eng.factTypes[1]);

        assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, eng.getFieldType( "GenBean", "id" ));
        assertEquals(SuggestionCompletionEngine.TYPE_STRING, eng.getFieldType( "GenBean", "name"));

        assertEquals("GenBean", eng.getFieldType("GenBean2", "gb"));

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

        assertEquals(0, eng.globalCollections.length);

    }


     public void testGlobalCollections() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n global java.util.List ls", new ArrayList(), new ArrayList() );
        assertNotNull(eng);
        assertFalse(loader.hasErrors());

        assertEquals(1, eng.getGlobalVariables().length);
        assertEquals("ls", eng.getGlobalVariables()[0]);
        assertEquals("List", eng.globalTypes.get("ls"));

        assertNotNull(eng.globalCollections);
        assertEquals(1, eng.globalCollections.length);
         assertEquals("ls", eng.globalCollections[0]);


    }

    public void testSortOrderOfFields() throws Exception {

	    SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
	    SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.guvnor.server.rules.SomeFact", new ArrayList(), new ArrayList() );
	    assertNotNull(eng);

	    String[] fields = eng.getFieldCompletions("SomeFact");

	    assertEquals("age", fields[0]);
	    assertEquals("alive", fields[1]);
	    assertEquals("bigDecimal", fields[2]);

    }
    public void testEnumFields() throws Exception {

	    SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
	    SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.guvnor.server.rules.SomeFact", new ArrayList(), new ArrayList() );
	    assertNotNull(eng);
        assertNotNull(eng.dataEnumLists);
        assertEquals(eng.dataEnumLists.get("SomeFact.anEnum").length,3);
        String a[] = eng.dataEnumLists.get("SomeFact.anEnum") ;
        assertEquals(a[0],"0="+EnumClass.v1);
        assertEquals(a[1],"1="+EnumClass.v2);
        assertEquals(a[2],"2="+EnumClass.v3);


    }
    public void testSortOrderOfFacts() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.guvnor.server.rules.SomeFact\n import org.drools.Person", new ArrayList(), new ArrayList() );
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
